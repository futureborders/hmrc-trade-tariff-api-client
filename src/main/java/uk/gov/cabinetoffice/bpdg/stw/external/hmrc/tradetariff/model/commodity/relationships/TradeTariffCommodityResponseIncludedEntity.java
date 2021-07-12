// Copyright 2021 Crown Copyright (Single Trade Window)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = Void.class, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CommoditySection.class, name = "section"),
  @JsonSubTypes.Type(value = CommodityChapter.class, name = "chapter"),
  @JsonSubTypes.Type(value = CommodityImpl.class, name = "commodity"),
  @JsonSubTypes.Type(value = CommodityMeasure.class, name = "measure"),
  @JsonSubTypes.Type(value = CommodityMeasureType.class, name = "measure_type"),
  @JsonSubTypes.Type(value = CommodityGeographicalArea.class, name = "geographical_area"),
  @JsonSubTypes.Type(value = CommodityMeasureCondition.class, name = "measure_condition"),
  @JsonSubTypes.Type(value = CommodityHeading.class, name = "heading"),
  @JsonSubTypes.Type(value = CommodityAdditionalCode.class, name = "additional_code")
})
public abstract class TradeTariffCommodityResponseIncludedEntity {
  protected String id;
}
