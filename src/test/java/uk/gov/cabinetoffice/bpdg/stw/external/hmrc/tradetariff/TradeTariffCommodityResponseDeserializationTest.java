package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TradeTariffCommodityResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TaxAndDuty;
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

  private static Stream<Arguments> countryCodesArgument() {
    return Stream.of(
        Arguments.of("false", "false", Boolean.FALSE, Boolean.TRUE),
        Arguments.of("true", "false", Boolean.TRUE, Boolean.TRUE),
        Arguments.of("false", "true", Boolean.FALSE, Boolean.FALSE),
        Arguments.of("true", "true", Boolean.TRUE, Boolean.FALSE)
    );
  }

  @ParameterizedTest
  @MethodSource("countryCodesArgument")
  @SneakyThrows
  public void shouldGetTaxCalculatorFields(String tradeDefence, String zeroMfnDuty, Boolean expectedTradeRemedies, Boolean expectedMFN) {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponse = "{\"data\":{\"meta\":{\"duty_calculator\":{\"trade_defence\":" + tradeDefence + ",\"zero_mfn_duty\":" + zeroMfnDuty + "}}}}";

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
  public void shouldTreatAsNullWhenNoTaxCalculatorFieldsPresent() {
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
  public void shouldDeserialiseSuccessfullyForDeclarableCommodityOrHeading() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/gb_commodities_api_v2_response_1109000000.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
  }

  @Test
  @SneakyThrows
  public void shouldThrowExceptionForNonDeclarableSection() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/gb_commodities_api_v2_response_6300000000.json";

    // when / then
    assertThatThrownBy(() -> objectMapper.readValue(
        new File(commodityResponseFilePath), TradeTariffCommodityResponse.class)).isInstanceOf(JsonMappingException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @SneakyThrows
  public void shouldThrowExceptionForNonDeclarableHeading() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/gb_commodities_api_v2_response_0101000000.json";

    // when / then
    assertThatThrownBy(() -> objectMapper.readValue(
        new File(commodityResponseFilePath), TradeTariffCommodityResponse.class)).isInstanceOf(JsonMappingException.class)
        .hasCauseInstanceOf(IllegalArgumentException.class);
  }
}
