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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TaxAndDuty;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TradeTariffCommodityResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TradeTariffCommodityResponseData.DutyCalculatorAdditionalCode;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.CommodityMeasure;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships.DutyExpression;

@ExtendWith(MockitoExtension.class)
class TradeTariffCommodityResponseDeserializationTest {

  @Test
  @SneakyThrows
  void shouldConsiderFirstLegalActAsLegalActIdForAMeasure() {
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
    assertThat(legalActIds)
        .isNotNull()
        .hasSize(7)
        .contains(
            "C2100270", "C2100001", "V1970ATZ", "C2100009", "X1906930", "C2100006", "A1907950")
        .doesNotContain("A1900160");
  }

  private static Stream<Arguments> countryCodesArgument() {
    return Stream.of(
        Arguments.of("false", "false", Boolean.FALSE, Boolean.TRUE),
        Arguments.of("true", "false", Boolean.TRUE, Boolean.TRUE),
        Arguments.of("false", "true", Boolean.FALSE, Boolean.FALSE),
        Arguments.of("true", "true", Boolean.TRUE, Boolean.FALSE));
  }

  @ParameterizedTest
  @MethodSource("countryCodesArgument")
  @SneakyThrows
  void shouldGetTaxCalculatorFields(
      String tradeDefence, String zeroMfnDuty, Boolean expectedTradeRemedies, Boolean expectedMFN) {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponse =
        "{\"data\":{\"meta\":{\"duty_calculator\":{\"trade_defence\":"
            + tradeDefence
            + ",\"zero_mfn_duty\":"
            + zeroMfnDuty
            + "}}}}";

    // when
    var expectedResponse =
        objectMapper.readValue(commodityResponse, TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    TaxAndDuty taxAndDuty = expectedResponse.getData().getTaxAndDuty();
    assertThat(taxAndDuty).isNotNull();
    assertThat(taxAndDuty.hasTradeRemedies()).isEqualTo(expectedTradeRemedies);
    assertThat(taxAndDuty.hasMostFavouredNationDuty()).isEqualTo(expectedMFN);
  }

  @Test
  @SneakyThrows
  void shouldTreatAsNullWhenNoTaxCalculatorFieldsPresent() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponse = "{\"data\":{\"meta\":{\"duty_calculator\":{}}}}";

    // when
    var expectedResponse =
        objectMapper.readValue(commodityResponse, TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    TaxAndDuty taxAndDuty = expectedResponse.getData().getTaxAndDuty();
    assertThat(taxAndDuty).isNotNull();
    assertThat(taxAndDuty.hasTradeRemedies()).isNull();
    assertThat(taxAndDuty.hasMostFavouredNationDuty()).isNull();
  }

  @Test
  @SneakyThrows
  void shouldDeserialiseSuccessfullyForDeclarableCommodityOrHeading() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath =
        "src/test/resources/gb_commodities_api_v2_response_1109000000.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
  }

  @Test
  @SneakyThrows
  void shouldThrowExceptionForNonDeclarableSection() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath =
        "src/test/resources/gb_commodities_api_v2_response_6300000000.json";

    // when / then
    assertThatThrownBy(
        () ->
            objectMapper.readValue(
                new File(commodityResponseFilePath), TradeTariffCommodityResponse.class))
        .isInstanceOf(JsonMappingException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @SneakyThrows
  void shouldThrowExceptionForNonDeclarableHeading() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath =
        "src/test/resources/gb_commodities_api_v2_response_0101000000.json";

    // when / then
    assertThatThrownBy(
        () ->
            objectMapper.readValue(
                new File(commodityResponseFilePath), TradeTariffCommodityResponse.class))
        .isInstanceOf(JsonMappingException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @SneakyThrows
  void shouldGetMeasureConditions() {
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
    assertThat(expectedResponse.getMeasureConditions().get(0).getCondition())
        .isEqualTo("B: Presentation of a certificate/licence/document");
    assertThat(expectedResponse.getMeasureConditions().get(0).getId()).isEqualTo("20060652");
    assertThat(expectedResponse.getMeasureConditions().get(0).getConditionCode()).isEqualTo("B");
    assertThat(expectedResponse.getMeasureConditions().get(0).getAction())
        .isEqualTo("Import/export allowed after control");
    assertThat(expectedResponse.getMeasureConditions().get(0).getRequirement())
        .isEqualTo(
            "UN/EDIFACT certificates: UN/EDIFACT certificates: Common Health Entry Document for Products (CHED-P) (as set out in Part 2, Section B of Annex II to Commission Implementing Regulation (EU) 2019/1715 (OJ L 261)) as transposed into UK Law.");
    assertThat(expectedResponse.getMeasureConditions().get(0).getDocumentCode()).isEqualTo("N853");
    assertThat(expectedResponse.getMeasureConditions().get(0).getDutyExpression()).isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldGetAdditionalCodes() {
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
    assertThat(expectedResponse.getAdditionalCodes().get(0).getDescription())
        .isEqualTo("VAT zero rate");
  }

  @Test
  @SneakyThrows
  void shouldGetGeographicalAreas() {
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
    assertThat(expectedResponse.getGeographicalAreas().get(254).getDescription()).isNotEmpty();
    assertThat(expectedResponse.getGeographicalAreas().get(254).getChildrenGeographicalAreas())
        .hasSizeGreaterThan(0);
  }

  @Test
  @SneakyThrows
  void shouldGetMeasureTypes() {
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
    assertThat(expectedResponse.getMeasureTypes().get(0).getSeriesId()).isEqualTo("B");
    assertThat(expectedResponse.getMeasureTypes().get(0).getDescription())
        .isEqualTo("Veterinary control");
  }

  @Test
  @SneakyThrows
  void shouldGetSection() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/flat-oysters-for-human-consumption.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getSection().getDescription())
        .isEqualTo("Live animals; animal products");
  }

  @Test
  @SneakyThrows
  void shouldGetChapter() {
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
    assertThat(expectedResponse.getChapter().getDescription())
        .isEqualTo("Fish and crustaceans, molluscs and other aquatic invertebrates");
  }

  @Test
  @SneakyThrows
  void shouldGetIncludedCommodities() {
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
    assertThat(expectedResponse.getIncludedCommodities().get(0).getDescription())
        .isEqualTo("Oysters");
    assertThat(expectedResponse.getIncludedCommodities().get(0).getGoodsNomenclatureItemId())
        .isEqualTo("0307110000");
    assertThat(expectedResponse.getIncludedCommodities().get(0).getLeaf()).isNotPresent();
    assertThat(expectedResponse.getIncludedCommodities().get(0).getSid()).isNotPresent();
    assertThat(expectedResponse.getIncludedCommodities().get(0).getParentSid()).isNotPresent();
    assertThat(expectedResponse.getIncludedCommodities().get(0).getNumberIndents()).isEqualTo(1);
    assertThat(expectedResponse.getIncludedCommodities().get(0).getProductLineSuffix())
        .isEqualTo(10);
  }

  @Test
  @SneakyThrows
  void shouldGetDutyExpressionForCommodities() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/undenatured-ethyl-alcohol-vol-80-over.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getDutyRates()).hasSize(53);
    assertThat(expectedResponse.getDutyRates()).contains(
        DutyExpression.builder().id("20118018-duty_expression").base("0.00 %").build());
    assertThat(expectedResponse.getDutyRates()).contains(
        DutyExpression.builder().id("20126375-duty_expression").base("16.00 GBP / hl").build());
  }

  @Test
  @SneakyThrows
  void shouldGetMeasureDetails() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/undenatured-ethyl-alcohol-vol-80-over.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getMeasures()).hasSize(53);
    assertThat(expectedResponse.getMeasures()).contains(
        CommodityMeasure.builder().isImport(true).isVAT(false).isExcise(false).measureTypeId("142").geographicalAreaId("1006").additionalCodeId(null).dutyExpressionId("20118018-duty_expression")
            .legalActId("C2100006").quotaNumber(null).measureConditionIds(
                Set.of("20091993", "20091994")).excludedCountries(Set.of()).id("20118018").build());
    assertThat(expectedResponse.getMeasures()).contains(
        CommodityMeasure.builder().isImport(true).isVAT(true).isExcise(false).measureTypeId("305").geographicalAreaId("1011").additionalCodeId(null).dutyExpressionId("-1010374042-duty_expression")
            .legalActId("V1970ATS").quotaNumber(null).measureConditionIds(
                Set.of()).excludedCountries(Set.of()).id("-1010374042").build());
    assertThat(expectedResponse.getMeasures()).contains(
        CommodityMeasure.builder().isImport(true).isVAT(false).isExcise(true).measureTypeId("306").geographicalAreaId("1011").additionalCodeId("-1009206084").dutyExpressionId("-1009444058-duty_expression")
            .legalActId("X1970451").quotaNumber(null).measureConditionIds(
                Set.of()).excludedCountries(Set.of()).id("-1009444058").build());
  }

  @Test
  @SneakyThrows
  void shouldGetTaxAndDutyData() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/undenatured-ethyl-alcohol-vol-80-over.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getData()).isNotNull();
    assertThat(expectedResponse.getData().getTaxAndDuty()).isEqualTo(TaxAndDuty.builder().hasMostFavouredNationDuty(true).hasTradeRemedies(false).build());
  }

  @Test
  @SneakyThrows
  void shouldGetDutyCalculatorAdditionalCodes() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/undenatured-ethyl-alcohol-vol-80-over.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getData()).isNotNull();
    assertThat(expectedResponse.getData().getDutyCalculatorAdditionalCodes()).hasSize(3);
    assertThat(expectedResponse.getData().getDutyCalculatorAdditionalCodes()).contains(DutyCalculatorAdditionalCode.builder().code("2600").overlay("The product I am importing is COVID-19 critical").build());
    assertThat(expectedResponse.getData().getDutyCalculatorAdditionalCodes()).contains(DutyCalculatorAdditionalCode.builder().code("2601").overlay("The product I am importing is not COVID-19 critical").build());
    assertThat(expectedResponse.getData().getDutyCalculatorAdditionalCodes()).contains(DutyCalculatorAdditionalCode.builder().code("X451").overlay("451 - Spirits other than UK-produced whisky").build());
  }
}
