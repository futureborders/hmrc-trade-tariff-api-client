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

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TradeTariffCommodityResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.CommodityMeasure;

@ExtendWith(MockitoExtension.class)
public class TradeTariffCommodityResponseDeserializationTest {

  @Test
  @SneakyThrows
  public void shouldConsiderFirstLegalActAsLegalActIdForAMeasure() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    List<String> legalActIds =
        expectedResponse.getMeasures().stream()
            .map(CommodityMeasure::getLegalActId)
            .collect(Collectors.toList());
    assertThat(legalActIds).isNotNull();
    assertThat(legalActIds).hasSize(7);
    assertThat(legalActIds)
        .contains(
            "C2100270", "C2100001", "V1970ATZ", "C2100009", "X1906930", "C2100006", "A1907950");
    assertThat(legalActIds).doesNotContain("A1900160");
  }

  @Test
  @SneakyThrows
  public void shouldGetHeadingData() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getHeading()).isPresent();
    assertThat(expectedResponse.getHeading().get().getGoodsNomenclatureItemId()).isEqualTo("0307000000");
    assertThat(expectedResponse.getHeading().get().getDescription()).isEqualTo(
        "Molluscs, whether in shell or not, live, fresh, chilled, frozen, dried, salted or in brine; smoked molluscs, whether in shell or not, whether or not cooked before or during the smoking process; flours, meals and pellets of molluscs, fit for human consumption");
  }

  @Test
  @SneakyThrows
  public void shouldGetGeographicalAreas() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getGeographicalAreas()).hasSize(255);
    assertThat(expectedResponse.getGeographicalAreas().get(0).getDescription()).isEqualTo("Andorra");
    assertThat(expectedResponse.getGeographicalAreas().get(0).getId()).isEqualTo("AD");
    assertThat(expectedResponse.getGeographicalAreas().get(0).getChildrenGeographicalAreas()).hasSize(0);
  }

  @Test
  @SneakyThrows
  public void shouldGetMeasureTypes() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getMeasureTypes()).hasSize(7);
    assertThat(expectedResponse.getMeasureTypes().get(0).getDescription()).isEqualTo("Veterinary control");
    assertThat(expectedResponse.getMeasureTypes().get(0).getId()).isEqualTo("410");
    assertThat(expectedResponse.getMeasureTypes().get(0).getSeriesId()).isEqualTo("B");
  }

  @Test
  @SneakyThrows
  public void shouldGetMeasureConditions() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getMeasureConditions()).hasSize(15);
    assertThat(expectedResponse.getMeasureConditions().get(0).getCondition()).isEqualTo("B: Presentation of a certificate/licence/document");
    assertThat(expectedResponse.getMeasureConditions().get(0).getId()).isEqualTo("20060652");
    assertThat(expectedResponse.getMeasureConditions().get(0).getConditionCode()).isEqualTo("B");
    assertThat(expectedResponse.getMeasureConditions().get(0).getAction()).isEqualTo("Import/export allowed after control");
    assertThat(expectedResponse.getMeasureConditions().get(0).getRequirement()).isEqualTo("UN/EDIFACT certificates: UN/EDIFACT certificates: Common Health Entry Document for Products (CHED-P) (as set out in Part 2, Section B of Annex II to Commission Implementing Regulation (EU) 2019/1715 (OJ L 261)) as transposed into UK Law.");
    assertThat(expectedResponse.getMeasureConditions().get(0).getDocumentCode()).isEqualTo("N853");
    assertThat(expectedResponse.getMeasureConditions().get(0).getDutyExpression()).isEqualTo("");
  }

  @Test
  @SneakyThrows
  public void shouldGetAdditionalCodes() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getAdditionalCodes()).hasSize(1);
    assertThat(expectedResponse.getAdditionalCodes().get(0).getCode()).isEqualTo("VATZ");
    assertThat(expectedResponse.getAdditionalCodes().get(0).getId()).isEqualTo("-1009206007");
    assertThat(expectedResponse.getAdditionalCodes().get(0).getDescription()).isEqualTo("VAT zero rate");
  }

  @Test
  @SneakyThrows
  public void shouldGetSection() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getSection().getDescription()).isEqualTo("Live animals; animal products");
  }

  @Test
  @SneakyThrows
  public void shouldGetChapter() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getChapter().getGoodsNomenclatureItemId()).isEqualTo("0300000000");
    assertThat(expectedResponse.getChapter().getDescription()).isEqualTo("Fish and crustaceans, molluscs and other aquatic invertebrates");
  }

  @Test
  @SneakyThrows
  public void shouldGetIncludedCommodities() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getIncludedCommodities()).hasSize(3);
    assertThat(expectedResponse.getIncludedCommodities().get(0).getDescription()).isEqualTo("Oysters");
    assertThat(expectedResponse.getIncludedCommodities().get(0).getGoodsNomenclatureItemId()).isEqualTo("0307110000");
    assertThat(expectedResponse.getIncludedCommodities().get(0).getLeaf()).isEqualTo(Optional.empty());
    assertThat(expectedResponse.getIncludedCommodities().get(0).getSid()).isEqualTo(Optional.empty());
    assertThat(expectedResponse.getIncludedCommodities().get(0).getParentSid()).isEqualTo(Optional.empty());
    assertThat(expectedResponse.getIncludedCommodities().get(0).getNumberIndents()).isEqualTo(1);
    assertThat(expectedResponse.getIncludedCommodities().get(0).getProductLineSuffix()).isEqualTo(10);
  }

}
