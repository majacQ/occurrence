/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.occurrence.downloads.launcher.services.launcher.oozie;

import static org.gbif.api.model.occurrence.Download.Status.EXECUTING_STATUSES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.gbif.api.model.occurrence.Download;
import org.gbif.api.model.occurrence.Download.Status;
import org.gbif.api.model.occurrence.DownloadRequest;
import org.gbif.common.messaging.api.messages.DownloadLauncherMessage;
import org.gbif.occurrence.downloads.launcher.services.LockerService;
import org.gbif.occurrence.downloads.launcher.services.launcher.DownloadLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service("oozie")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OozieDownloadLauncherService implements DownloadLauncher {

  private final OozieClient client;
  private final DownloadWorkflowParametersBuilder parametersBuilder;
  private final LockerService lockerService;

  public OozieDownloadLauncherService(
      OozieClient client,
      @Qualifier("oozie.properties") Map<String, String> defaultProperties,
      LockerService lockerService) {
    this.client = client;
    this.parametersBuilder = new DownloadWorkflowParametersBuilder(defaultProperties);
    this.lockerService = lockerService;
  }

  @Override
  public JobStatus create(@NotNull DownloadLauncherMessage message) {
    try {
      String downloadKey = message.getDownloadKey();
      DownloadRequest request = message.getDownloadRequest();
      client.run(parametersBuilder.buildWorkflowParameters(downloadKey, request));

      asyncStatusCheck(downloadKey);

      return JobStatus.RUNNING;
    } catch (OozieClientException ex) {
      log.error(ex.getMessage(), ex);
      return JobStatus.FAILED;
    }
  }

  private void asyncStatusCheck(String downloadKey) {
    CompletableFuture.runAsync(
        () -> {
          try {
            String jobId = client.getJobId(downloadKey);

            Status status = Status.valueOf(client.getStatus(jobId));
            while (EXECUTING_STATUSES.contains(status)) {
              TimeUnit.SECONDS.sleep(10);
              status = Status.valueOf(client.getStatus(jobId));
            }

            lockerService.unlock(downloadKey);

            log.info("Job {} finished with status {}", downloadKey, status);
          } catch (Exception ex) {
            lockerService.unlock(downloadKey);
            log.error(ex.getMessage(), ex);
          }
        });
  }

  @Override
  public JobStatus cancel(@NotNull String downloadKey) {
    try {
      String jobId = client.getJobId(downloadKey);
      if (jobId != null && !jobId.isEmpty()) {
        client.kill(jobId);
        return JobStatus.CANCELLED;
      }
    } catch (OozieClientException ex) {
      log.error(ex.getMessage(), ex);
    }
    return JobStatus.FAILED;
  }

  @Override
  public Optional<Status> getStatusByName(String downloadKey) {
    return Optional.empty(); // Ignore status update, it will be done by oozie callback
  }

  @Override
  public List<Download> renewRunningDownloadsStatuses(List<Download> downloads) {
    List<Download> result = new ArrayList<>(downloads.size());
    for (Download d : downloads) {
      try {
        String jobId = client.getJobId(d.getKey());
        String status = client.getStatus(jobId);
        d.setStatus(Status.valueOf(status));
        result.add(d);
      } catch (OozieClientException ex) {
        log.error(ex.getMessage(), ex);
      }
    }
    return result;
  }
}
