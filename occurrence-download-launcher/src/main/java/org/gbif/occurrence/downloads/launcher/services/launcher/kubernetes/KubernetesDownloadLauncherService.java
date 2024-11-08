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
package org.gbif.occurrence.downloads.launcher.services.launcher.kubernetes;

import java.util.List;
import java.util.Optional;
import org.gbif.api.model.occurrence.Download;
import org.gbif.api.model.occurrence.Download.Status;
import org.gbif.common.messaging.api.messages.DownloadLauncherMessage;
import org.gbif.occurrence.downloads.launcher.services.launcher.DownloadLauncher;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("kubernetes")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KubernetesDownloadLauncherService implements DownloadLauncher {

  @Override
  public JobStatus create(DownloadLauncherMessage message) {
    throw new UnsupportedOperationException("The method is not implemented!");
  }

  @Override
  public JobStatus cancel(String downloadKey) {
    throw new UnsupportedOperationException("The method is not implemented!");
  }

  @Override
  public Optional<Status> getStatusByName(String downloadKey) {
    return Optional.empty();
  }

  @Override
  public List<Download> renewRunningDownloadsStatuses(List<Download> downloads) {
    throw new UnsupportedOperationException("The method is not implemented!");
  }
}
