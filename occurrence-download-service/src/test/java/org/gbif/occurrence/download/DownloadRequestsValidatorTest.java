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
package org.gbif.occurrence.download;

import org.gbif.occurrence.download.service.DownloadRequestsValidator;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DownloadRequestsValidatorTest {

  @Test
  public void downloadTestSendNotificationNull() {
    DownloadRequestsValidator validator = new DownloadRequestsValidator();
    validator.validate("{\n"
                       + "  \"creator\":\"markus\",\n"
                       + "  \"format\": \"SIMPLE_CSV\",\n"
                       + "  \"predicate\":{\n"
                       + "    \"type\":\"within\",\n"
                       + "    \"geometry\":\"POLYGON ((-85.781 17.978,-81.035 14.774,-77.343 10.314,-79.277 6.315,-93.955 14.604,-91.450 18.229,-87.626 19.311,-85.781 17.978))\"\n"
                       + "  }\n"
                       + "}");
  }

  @Test
  public void downloadTestSendNotification() {
    DownloadRequestsValidator validator = new DownloadRequestsValidator();

    //Boolean as string
    validator.validate("{\n"
                       + "  \"creator\":\"markus\",\n"
                       + "  \"format\": \"SIMPLE_CSV\",\n"
                       + "  \"sendNotification\": \"true\",\n"
                       + "  \"predicate\":{\n"
                       + "    \"type\":\"within\",\n"
                       + "    \"geometry\":\"POLYGON ((-85.781 17.978,-81.035 14.774,-77.343 10.314,-79.277 6.315,-93.955 14.604,-91.450 18.229,-87.626 19.311,-85.781 17.978))\"\n"
                       + "  }\n"
                       + "}");

    //Boolean type
    validator.validate("{\n"
                       + "  \"creator\":\"markus\",\n"
                       + "  \"format\": \"SIMPLE_CSV\",\n"
                       + "  \"sendNotification\": true,\n"
                       + "  \"predicate\":{\n"
                       + "    \"type\":\"within\",\n"
                       + "    \"geometry\":\"POLYGON ((-85.781 17.978,-81.035 14.774,-77.343 10.314,-79.277 6.315,-93.955 14.604,-91.450 18.229,-87.626 19.311,-85.781 17.978))\"\n"
                       + "  }\n"
                       + "}");
  }

  @Test
  @Ignore
  public void downloadTestUnknownField() {
    DownloadRequestsValidator validator = new DownloadRequestsValidator();
    Assertions.assertThrows(java.text.ParseException.class, () ->
      validator.validate("{\n"
                         + "  \"creator\":\"markus\",\n"
                         + "  \"predicates\":{\n" // Incorrect name (extra s).
                         + "    \"type\":\"within\",\n"
                         + "    \"geometry\":\"POLYGON ((-85.781 17.978,-81.035 14.774,-77.343 10.314,-79.277 6.315,-93.955 14.604,-91.450 18.229,-87.626 19.311,-85.781 17.978))\"\n"
                         + "  }\n"
                         + "}"));
  }

  @Test
  @Ignore
  public void downloadTestMissingPredicate() {
    DownloadRequestsValidator validator = new DownloadRequestsValidator();
    Assertions.assertThrows(java.text.ParseException.class, () ->
      validator.validate("{\n"
        + "  \"creator\":\"markus\"\n"
        + "}"));
  }

  @Test
  public void downloadTestEmptyPredicate() {
    DownloadRequestsValidator validator = new DownloadRequestsValidator();
    validator.validate("{\n"
      + "  \"creator\":\"markus\",\n"
      + "  \"predicate\":{}\n"
      + "}");
  }

  @Test
  public void downloadTestMatchCaseField() {
    DownloadRequestsValidator validator = new DownloadRequestsValidator();

    //Boolean as string
    validator.validate("{\n"
                       + "  \"creator\":\"markus\",\n"
                       + "  \"format\": \"SIMPLE_CSV\",\n" //Unknown field
                       + "  \"predicate\":{\n"
                       + "    \"type\":\"equals\",\n"
                       + "    \"key\":\"CATALOG_NUMBER\",\n"
                       + "    \"value\":\"Ax1\",\n"
                       + "    \"matchCase\":\"true\"\n"
                       + "  }\n"
                       + "}");

    //Boolean as string
    validator.validate("{\n"
                       + "  \"creator\":\"markus\",\n"
                       + "  \"format\": \"SIMPLE_CSV\",\n" //Unknown field
                       + "  \"predicate\":{\n"
                       + "    \"type\":\"equals\",\n"
                       + "    \"key\":\"CATALOG_NUMBER\",\n"
                       + "    \"value\":\"Ax1\",\n"
                       + "    \"matchCase\": false\n"
                       + "  }\n"
                       + "}");
  }
}
