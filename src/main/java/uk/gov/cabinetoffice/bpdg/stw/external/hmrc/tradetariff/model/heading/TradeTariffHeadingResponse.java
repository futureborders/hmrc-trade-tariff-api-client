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

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.TradeTariffResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.relationships.TradeTariffHeadingResponseIncludedEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeTariffHeadingResponse
    extends TradeTariffResponse<TradeTariffHeadingResponseData> {
  @Builder.Default
  private List<TradeTariffHeadingResponseIncludedEntity> included = new ArrayList<>();
}
