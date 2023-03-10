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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class CommodityMeasure extends TradeTariffCommodityResponseIncludedEntity {

  private boolean isImport;
  private boolean isExport;
  private boolean isVAT;
  private boolean isExcise;
  private String measureTypeId;
  private String geographicalAreaId;
  private String additionalCodeId;
  private String legalActId;
  private String dutyExpressionId;
  private String quotaNumber;
  @Builder.Default private Set<String> measureConditionIds = new HashSet<>();
  @Builder.Default private Set<String> excludedCountries = new HashSet<>();

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.isImport =
        Optional.ofNullable(attributes.get("import"))
            .map(Boolean.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Measure should have import indicator"));
    this.isExport =
        Optional.ofNullable(attributes.get("export"))
            .map(Boolean.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Measure should have export indicator"));
    this.isVAT =
        Optional.ofNullable(attributes.get("vat"))
            .map(Boolean.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Measure should have a vat indicator"));
    this.isExcise =
        Optional.ofNullable(attributes.get("excise"))
            .map(Boolean.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Measure should have a excise indicator"));
  }

  @JsonProperty("relationships")
  private void unpackRelationships(Map<String, Object> relationships) {
    this.measureTypeId =
        Optional.ofNullable(relationships.get("measure_type"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(Map.class::cast)
            .map(m -> m.get("id"))
            .map(String.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Measure should have measure type"));

    this.geographicalAreaId =
        Optional.ofNullable(relationships.get("geographical_area"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(Map.class::cast)
            .map(m -> m.get("id"))
            .map(String.class::cast)
            .orElseThrow(
                () -> new IllegalArgumentException("Measure should have geographical area"));

    this.excludedCountries =
        Optional.ofNullable(relationships.get("excluded_countries"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(l -> (List<Map<String, String>>) l)
            .orElse(List.of())
            .stream()
            .map(m -> m.get("id"))
            .collect(Collectors.toSet());

    this.measureConditionIds =
        Optional.ofNullable(relationships.get("measure_conditions"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(l -> (List<Map<String, String>>) l)
            .orElse(List.of())
            .stream()
            .map(m -> m.get("id"))
            .collect(Collectors.toSet());

    this.additionalCodeId =
        Optional.ofNullable(relationships.get("additional_code"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(Map.class::cast)
            .map(m -> m.get("id"))
            .map(String.class::cast)
            .orElse(null);

    this.legalActId =
        Optional.ofNullable(relationships.get("legal_acts"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(l -> (List<Map<String, String>>) l)
            .orElse(List.of())
            .stream()
            .findFirst()
            .orElse(new HashMap<>())
            .get("id");

    this.dutyExpressionId =
        Optional.ofNullable(relationships.get("duty_expression"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(Map.class::cast)
            .map(m -> m.get("id"))
            .map(String.class::cast)
            .orElse(null);

    this.quotaNumber =
        Optional.ofNullable(relationships.get("order_number"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(Map.class::cast)
            .map(m -> m.get("id"))
            .map(String.class::cast)
            .orElse(null);
  }
}
