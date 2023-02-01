/*
 * Copyright 2021 Crown Copyright (Single Trade Window)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

public enum HeadingsApiVersion {
  HEADINGS_GB_V2("/api/v2/headings/%s"),
  HEADINGS_NI_V2("/xi/api/v2/headings/%s");

  private final String apiPath;

  public String apiPathFor(String headingCode){
    return String.format(this.apiPath,headingCode);
  }

  HeadingsApiVersion(String apiPath) {
    this.apiPath = apiPath;
  }
}
