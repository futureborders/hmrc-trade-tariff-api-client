package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Optional;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityMeasureType extends TradeTariffCommodityResponseIncludedEntity {

  private String seriesId;
  private String description;

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.seriesId =
        Optional.ofNullable(attributes.get("measure_type_series_id"))
            .map(String.class::cast)
            .orElse("");
    this.description =
        Optional.ofNullable(attributes.get("description")).map(String.class::cast).orElse("");
  }
}
