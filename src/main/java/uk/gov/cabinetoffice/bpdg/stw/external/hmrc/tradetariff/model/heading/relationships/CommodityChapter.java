package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.Chapter;

import java.util.Map;
import java.util.Optional;

@SuperBuilder
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityChapter extends TradeTariffHeadingResponseIncludedEntity implements Chapter {
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
