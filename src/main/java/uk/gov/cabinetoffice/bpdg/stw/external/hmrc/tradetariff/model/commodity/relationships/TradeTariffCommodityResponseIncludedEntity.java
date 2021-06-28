package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = Void.class, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CommoditySection.class, name = "section"),
  @JsonSubTypes.Type(value = CommodityChapter.class, name = "chapter"),
  @JsonSubTypes.Type(value = CommodityImpl.class, name = "commodity"),
  @JsonSubTypes.Type(value = CommodityMeasure.class, name = "measure"),
  @JsonSubTypes.Type(value = CommodityMeasureType.class, name = "measure_type"),
  @JsonSubTypes.Type(value = CommodityGeographicalArea.class, name = "geographical_area"),
  @JsonSubTypes.Type(value = CommodityMeasureCondition.class, name = "measure_condition"),
  @JsonSubTypes.Type(value = CommodityHeading.class, name = "heading"),
  @JsonSubTypes.Type(value = CommodityAdditionalCode.class, name = "additional_code")
})
public abstract class TradeTariffCommodityResponseIncludedEntity {
  protected String id;
}
