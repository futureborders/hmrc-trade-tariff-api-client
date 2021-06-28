package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TradeTariffCommodityResponse;

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
            .map(commodityMeasure -> commodityMeasure.getLegalActId())
            .collect(Collectors.toList());
    assertThat(legalActIds).isNotNull();
    assertThat(legalActIds).hasSize(7);
    assertThat(legalActIds)
        .contains(
            "C2100270", "C2100001", "V1970ATZ", "C2100009", "X1906930", "C2100006", "A1907950");
    assertThat(legalActIds).doesNotContain("A1900160");
  }
}
