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

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityMeasureCondition extends TradeTariffCommodityResponseIncludedEntity {

  private String conditionCode;
  private String condition;
  private String documentCode;
  private String requirement;
  private String certificateDescription;
  private String action;
  private String dutyExpression;
  private String conditionDutyAmount;
  private String conditionMonetaryUnitCode;
  private String conditionMeasurementUnitCode;

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, String> attributes) {
    this.conditionCode = attributes.getOrDefault("condition_code", "");
    this.condition = attributes.getOrDefault("condition", "");
    this.documentCode = attributes.getOrDefault("document_code", "");
    this.requirement = attributes.getOrDefault("requirement", "");
    this.certificateDescription = Optional.ofNullable(attributes.get("certificate_description")).map(String::trim).orElse("");
    this.action = attributes.getOrDefault("action", "");
    this.dutyExpression = attributes.getOrDefault("duty_expression", "");
    this.conditionDutyAmount = attributes.getOrDefault("condition_duty_amount", "");
    this.conditionMonetaryUnitCode = attributes.getOrDefault("condition_monetary_unit_code", "");
    this.conditionMeasurementUnitCode = attributes.getOrDefault("condition_measurement_unit_code", "");
  }
}
