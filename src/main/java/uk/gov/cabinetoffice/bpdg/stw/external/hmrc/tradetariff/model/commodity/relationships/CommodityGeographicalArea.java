package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
