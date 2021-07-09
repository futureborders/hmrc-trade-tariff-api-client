package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.Commodity;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityImpl extends TradeTariffCommodityResponseIncludedEntity implements Commodity {

  private String goodsNomenclatureItemId;
  private String description;
  private Integer numberIndents;
  private Integer productLineSuffix;

  @Builder.Default private Optional<Boolean> leaf = Optional.empty();
  @Builder.Default private Optional<Integer> parentSid = Optional.empty();
  @Builder.Default private Optional<Integer> sid = Optional.empty();

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, Object> attributes) {
    this.goodsNomenclatureItemId =
        Optional.ofNullable(attributes.get("goods_nomenclature_item_id"))
            .map(String.class::cast)
            .orElse("");
    this.description =
        Optional.ofNullable(attributes.get("formatted_description"))
            .map(String.class::cast)
            .orElse("");
    this.numberIndents =
        Optional.ofNullable(attributes.get("number_indents"))
            .map(Integer.class::cast)
            .orElse(numberIndents);
    this.productLineSuffix =
        Optional.ofNullable(attributes.get("producline_suffix"))
            .map(p -> Integer.valueOf(String.valueOf(p)))
            .orElse(null);
  }
}
