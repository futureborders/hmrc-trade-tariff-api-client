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
import java.util.Collections;
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
public class CommodityGeographicalArea extends TradeTariffCommodityResponseIncludedEntity {

  private String description;
  @Builder.Default private Set<String> childrenGeographicalAreas = new HashSet<>();

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.description =
            Optional.ofNullable(attributes.get("description")).map(String.class::cast).orElse("");
  }

  @JsonProperty("relationships")
  private void unpackRelationships(Map<String, Object> relationships) {
    this.childrenGeographicalAreas =
        Optional.ofNullable(relationships.get("children_geographical_areas"))
            .map(Map.class::cast)
            .map(m -> m.get("data"))
            .map(l -> (List<Map<String, String>>) l)
            .orElse(Collections.emptyList())
            .stream()
            .map(m -> m.get("id"))
            .collect(Collectors.toSet());
  }
}
