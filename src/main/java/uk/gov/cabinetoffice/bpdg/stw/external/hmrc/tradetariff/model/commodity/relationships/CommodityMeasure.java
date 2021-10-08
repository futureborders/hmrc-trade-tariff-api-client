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
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityMeasure extends TradeTariffCommodityResponseIncludedEntity {

  private boolean isImport;
  private String measureTypeId;
  private String geographicalAreaId;
  private String additionalCodeId;
  private String legalActId;
  @Builder.Default private Set<String> measureConditionIds = new HashSet<>();
  @Builder.Default private Set<String> excludedCountries = new HashSet<>();

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.isImport =
        Optional.ofNullable(attributes.get("import"))
            .map(Boolean.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Measure should have a trade type"));
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
  }
}
