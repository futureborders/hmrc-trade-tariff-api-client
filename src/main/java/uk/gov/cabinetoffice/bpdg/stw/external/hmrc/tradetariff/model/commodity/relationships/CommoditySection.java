package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.Section;

import java.util.Map;
import java.util.Optional;

@SuperBuilder
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommoditySection extends TradeTariffCommodityResponseIncludedEntity implements Section {
  private String description;

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.description =
      Optional.ofNullable(attributes.get("title"))
        .map(String.class::cast)
        .orElse("");
  }
}
