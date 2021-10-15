/*
 * Copyright 2021 Crown Copyright (Single Trade Window)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
