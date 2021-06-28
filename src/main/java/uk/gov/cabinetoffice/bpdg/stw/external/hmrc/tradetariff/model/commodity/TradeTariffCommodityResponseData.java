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
  }
}
