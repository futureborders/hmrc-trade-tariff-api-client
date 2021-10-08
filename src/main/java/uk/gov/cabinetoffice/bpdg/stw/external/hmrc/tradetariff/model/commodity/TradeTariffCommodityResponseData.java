package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeTariffCommodityResponseData {

  private String id;
  private String type;
  private String goodsNomenclatureItemId;
  private String formattedDescription;
  private Integer numberIndents;
  private TaxAndDuty taxAndDuty;

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.goodsNomenclatureItemId =
        Optional.ofNullable(attributes.get("goods_nomenclature_item_id"))
            .map(String.class::cast)
            .orElse("");
    this.formattedDescription =
        Optional.ofNullable(attributes.get("formatted_description"))
            .map(String.class::cast)
            .orElse("");
    this.numberIndents =
        Optional.ofNullable(attributes.get("number_indents")).map(Integer.class::cast).orElse(null);

    // Check that the heading/commodity has 'declarable: true', otherwise we can't convert to a commodity
    Optional.ofNullable(attributes.get("declarable"))
        .map(Boolean.class::cast)
        .filter(Boolean.TRUE::equals)
        .orElseThrow(() -> new IllegalArgumentException("Response must be 'declarable: true' to map to commodity"));
  }

  @JsonProperty("meta")
  private void unpackMeta(Map<String, Object> meta) {
    var dutyCalculator = Optional.ofNullable(meta.get("duty_calculator")).map(Map.class::cast);
    Boolean tradeDefence = dutyCalculator
        .map(obj -> obj.get("trade_defence"))
        .map(Boolean.class::cast)
        .orElse(null);
    Boolean zeroMfnDuty = dutyCalculator
        .map(obj -> obj.get("zero_mfn_duty"))
        .map(Boolean.class::cast)
        .map(obj -> !obj)
        .orElse(null);
    this.taxAndDuty = new TaxAndDuty(zeroMfnDuty, tradeDefence);
  }

}
