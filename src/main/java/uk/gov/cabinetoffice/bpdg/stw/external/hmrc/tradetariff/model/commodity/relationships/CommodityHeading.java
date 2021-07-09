package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityHeading extends TradeTariffCommodityResponseIncludedEntity {
  private String goodsNomenclatureItemId;
  private String description;

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
  }
}
