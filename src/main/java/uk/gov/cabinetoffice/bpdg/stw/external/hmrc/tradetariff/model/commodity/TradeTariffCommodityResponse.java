package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.TradeTariffResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.CommodityAdditionalCode;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.CommodityGeographicalArea;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.CommodityHeading;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.CommodityMeasure;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.CommodityMeasureCondition;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.CommodityMeasureType;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.TradeTariffCommodityResponseIncludedEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeTariffCommodityResponse
    extends TradeTariffResponse<TradeTariffCommodityResponseData> {

  @Builder.Default private List<TradeTariffCommodityResponseIncludedEntity> included = new ArrayList<>();

  @JsonIgnore
  public Optional<CommodityHeading> getHeading() {
    return getFirstIncludedType(CommodityHeading.class);
  }

  @JsonIgnore
  public List<CommodityGeographicalArea> getGeographicalAreas() {
    return getIncludedType(CommodityGeographicalArea.class);
  }

  @JsonIgnore
  public List<CommodityMeasureType> getMeasureTypes() {
    return getIncludedType(CommodityMeasureType.class);
  }

  @JsonIgnore
  public List<CommodityMeasureCondition> getMeasureConditions() {
    return getIncludedType(CommodityMeasureCondition.class);
  }

  @JsonIgnore
  public List<CommodityMeasure> getMeasures() {
    return getIncludedType(CommodityMeasure.class);
  }

  @JsonIgnore
  public List<CommodityAdditionalCode> getAdditionalCodes() {
    return getIncludedType(CommodityAdditionalCode.class);
  }
}
