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
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.TradeTariffHeadingResponse;

@ExtendWith(MockitoExtension.class)
public class TradeTariffHeadingResponseDeserializationTest {

  @Test
  @SneakyThrows
  public void shouldGetData() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/gb_headings_api_v2_response_1103.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffHeadingResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getData().getGoodsNomenclatureItemId()).isEqualTo("1103000000");
    assertThat(expectedResponse.getData().getFormattedDescription()).isEqualTo("Cereal groats, meal and pellets");
    assertThat(expectedResponse.getData().getId()).isEqualTo("31958");
    assertThat(expectedResponse.getData().getType()).isEqualTo("heading");
  }


  @Test
  @SneakyThrows
  public void shouldGetSection() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/gb_headings_api_v2_response_1103.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffHeadingResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getSection().getDescription()).isEqualTo("Vegetable products");
  }

  @Test
  @SneakyThrows
  public void shouldGetChapter() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/gb_headings_api_v2_response_1103.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffHeadingResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getChapter().getGoodsNomenclatureItemId()).isEqualTo("1100000000");
    assertThat(expectedResponse.getChapter().getDescription()).isEqualTo("Products of the milling industry; malt; starches; inulin; wheat gluten");
  }

  @Test
  @SneakyThrows
  public void shouldGetIncludedCommodities() {
    // given
    var objectMapper = new ObjectMapper();
    var commodityResponseFilePath = "src/test/resources/gb_headings_api_v2_response_1103.json";

    // when
    var expectedResponse =
        objectMapper.readValue(
            new File(commodityResponseFilePath), TradeTariffHeadingResponse.class);

    // then
    assertThat(expectedResponse).isNotNull();
    assertThat(expectedResponse.getIncludedCommodities()).hasSize(23);
    assertThat(expectedResponse.getIncludedCommodities().get(0).getDescription()).isEqualTo("Groats and meal");
    assertThat(expectedResponse.getIncludedCommodities().get(0).getGoodsNomenclatureItemId()).isEqualTo("1103110000");
    assertThat(expectedResponse.getIncludedCommodities().get(0).getLeaf()).isEqualTo(Optional.of(false));
    assertThat(expectedResponse.getIncludedCommodities().get(0).getSid()).isEqualTo(Optional.of(31959));
    assertThat(expectedResponse.getIncludedCommodities().get(0).getParentSid()).isEqualTo(Optional.empty());
    assertThat(expectedResponse.getIncludedCommodities().get(0).getNumberIndents()).isEqualTo(1);
    assertThat(expectedResponse.getIncludedCommodities().get(0).getProductLineSuffix()).isEqualTo(10);
  }

}
