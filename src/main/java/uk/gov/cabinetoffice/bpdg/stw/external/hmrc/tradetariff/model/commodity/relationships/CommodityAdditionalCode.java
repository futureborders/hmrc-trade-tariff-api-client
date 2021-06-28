package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityAdditionalCode extends TradeTariffCommodityResponseIncludedEntity {

  private String code;
  private String description;

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.code = Optional.ofNullable(attributes.get("code")).map(String.class::cast).orElse("");
    this.description =
        Optional.ofNullable(attributes.get("description")).map(String.class::cast).orElse("");
  }
}
