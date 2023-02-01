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

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.Commodity;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityImpl extends TradeTariffHeadingResponseIncludedEntity implements Commodity {

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

    this.leaf = Optional.ofNullable(attributes.get("leaf")).map(Boolean.class::cast);
    this.parentSid = Optional.ofNullable(attributes.get("parent_sid")).map(Integer.class::cast);
    this.sid =
        Optional.ofNullable(attributes.get("goods_nomenclature_sid")).map(Integer.class::cast);
  }
}
