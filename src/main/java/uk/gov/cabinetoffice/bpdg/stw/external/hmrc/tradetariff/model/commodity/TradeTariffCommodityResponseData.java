// Copyright 2021 Crown Copyright (Single Trade Window)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private List<DutyCalculatorAdditionalCode> dutyCalculatorAdditionalCodes;

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

    //noinspection unchecked
    this.dutyCalculatorAdditionalCodes = dutyCalculator.map(obj -> obj.get("applicable_additional_codes"))
        .map(l -> (Map<String, Map<String, Object>>) l)
        .map(Map::entrySet)
        .orElse(Set.of())
        .stream()
        .map(l -> ((Entry<String, Map<String, Object>>) l).getValue().get("additional_codes"))
        .flatMap(l -> ((List<Map<String, Object>>) l)
            .stream()
            .map(m -> DutyCalculatorAdditionalCode.builder()
                .code(String.valueOf(m.get("code")))
                .overlay(String.valueOf(m.get("overlay"))).build()))
        .collect(Collectors.toList());
  }

  @Data
  @Builder
  public static class DutyCalculatorAdditionalCode {

    String code;
    String overlay;
  }

}
