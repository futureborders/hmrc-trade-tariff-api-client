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
