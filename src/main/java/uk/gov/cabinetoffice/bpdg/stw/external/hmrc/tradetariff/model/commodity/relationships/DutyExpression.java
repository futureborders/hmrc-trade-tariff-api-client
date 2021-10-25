package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DutyExpression extends TradeTariffCommodityResponseIncludedEntity {
  @JsonProperty("id")
  private String id;
  private String base;

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.base =
        Optional.ofNullable(attributes.get("base"))
            .map(String.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Measure should have a trade type"));
  }
}
