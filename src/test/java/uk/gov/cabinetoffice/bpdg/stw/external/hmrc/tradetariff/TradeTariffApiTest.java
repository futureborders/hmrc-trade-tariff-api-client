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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.CommoditiesApiVersion.COMMODITIES_GB_V2;
import static uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.CommoditiesApiVersion.COMMODITIES_NI_V2;
import static uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.HeadingsApiVersion.HEADINGS_GB_V2;
import static uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.HeadingsApiVersion.HEADINGS_NI_V2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.SocketUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TradeTariffCommodityResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.TradeTariffHeadingResponse;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream.DownstreamEndpointLabelNameResolver;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream.DownstreamRequestMetrics;

@ExtendWith(MockitoExtension.class)
public class TradeTariffApiTest {

  private static final WireMockServer wireMockServer =
      new WireMockServer(SocketUtils.findAvailableTcpPort());
  private static String MOCK_TRADE_TARIFF_API_URL;

  @Mock DownstreamRequestMetrics downstreamRequestMetrics;
  @Mock DownstreamEndpointLabelNameResolver downstreamEndpointLabelNameResolver;

  private TradeTariffApi tradeTariffApi;

  @BeforeAll
  public static void setUp() {
    wireMockServer.start();
    MOCK_TRADE_TARIFF_API_URL =
        String.format("http://localhost:%s", wireMockServer.getOptions().portNumber());
  }

  @AfterAll
  public static void tearDown() {
    wireMockServer.stop();
  }

  @BeforeEach
  public void init() {
    System.setProperty("io.netty.tryReflectionSetAccessible", "false");
    WebClient.Builder builder = WebClient.builder();
    final TradeTariffApiConfig tradeTariffApiConfig =
        TradeTariffApiConfig.builder()
            .url(MOCK_TRADE_TARIFF_API_URL)
            .timeout(Duration.ofSeconds(1))
            .retryMaxAttempt(2)
            .retryMinBackoff(Duration.ofMillis(500))
            .build();
    tradeTariffApi =
        new TradeTariffApi(
            builder,
            tradeTariffApiConfig,
            downstreamRequestMetrics,
            downstreamEndpointLabelNameResolver);
  }

  @AfterEach
  public void destroy() {
    wireMockServer.resetAll();
  }

  @SneakyThrows
  private void stubCommoditiesResponse(
      final String commodityRequestPath, final String responseFilePath) {
    var responseBody = Files.readString(new File(responseFilePath).toPath());
    wireMockServer.stubFor(
        get(urlEqualTo(commodityRequestPath))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody)));
  }

  @SneakyThrows
  private void stubInternalServerResponse(final String commodityOrHeadingRequestPath) {
    wireMockServer.stubFor(
        get(urlEqualTo(commodityOrHeadingRequestPath))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .withHeader("Content-Type", "application/json")));
  }

  @SneakyThrows
  private void stubNotFoundResponse(final String commodityOrHeadingRequestPath) {
    wireMockServer.stubFor(
        get(urlEqualTo(commodityOrHeadingRequestPath))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value())
                    .withHeader("Content-Type", "application/json")));
  }

  @SneakyThrows
  private void stubHeadingsResponseWithCommodityCodeRequestResponse() {
    final String headingCode = "1109000000".substring(0, 4);
    wireMockServer.stubFor(
        get(urlEqualTo("/api/v2/commodities/".concat("1109000000")))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.MOVED_PERMANENTLY.value())
                    .withHeader("Content-Type", "text/html")
                    .withHeader(
                        "location",
                        MOCK_TRADE_TARIFF_API_URL
                            .concat("/api/v2/headings/")
                            .concat(headingCode))));
  }

  @SneakyThrows
  private void stubHeadingResponse(final String headingRequestPath, final String responseFilePath) {
    var body = Files.readString(new File(responseFilePath).toPath());
    wireMockServer.stubFor(
        get(urlEqualTo(headingRequestPath))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", "application/json")
                    .withBody(body)));
  }

  @Nested
  class GetCommodity {

    @Test
    @SneakyThrows
    void shouldReturnCommodityResponseForGB() {
      // given
      var objectMapper = new ObjectMapper();
      var commodityResponseFilePath =
          "src/test/resources/gb_commodities_api_v2_response_1006101000.json";
      var expectedResponse =
          objectMapper.readValue(
              new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);
      stubCommoditiesResponse(
          COMMODITIES_GB_V2.apiPathFor("1006101000"), commodityResponseFilePath);
      // when
      final Mono<TradeTariffCommodityResponse> tradeTariffCommodityResponsePublisher =
          tradeTariffApi.getCommodity("1006101000", COMMODITIES_GB_V2);
      // then
      StepVerifier.create(tradeTariffCommodityResponsePublisher)
          .expectNext(expectedResponse)
          .verifyComplete();
    }

    @Test
    @SneakyThrows
    void shouldReturnCommodityResponseForCommodityWhichIsAlsoAHeading() {
      // given
      var objectMapper = new ObjectMapper();
      var headingResponseFilePath =
          "src/test/resources/gb_commodities_api_v2_response_1109000000.json";
      var expectedResponse =
          objectMapper.readValue(
              new File(headingResponseFilePath), TradeTariffCommodityResponse.class);
      stubHeadingsResponseWithCommodityCodeRequestResponse();
      stubHeadingResponse(HEADINGS_GB_V2.apiPathFor("1109"), headingResponseFilePath);
      // when
      final Mono<TradeTariffCommodityResponse> tradeTariffCommodityResponsePublisher =
          tradeTariffApi.getCommodity("1109000000");
      // then
      StepVerifier.create(tradeTariffCommodityResponsePublisher)
          .expectNext(expectedResponse)
          .verifyComplete();
    }

    @Test
    @SneakyThrows
    void shouldReturnCommodityResponseForNI() {
      // given
      var objectMapper = new ObjectMapper();
      var commodityResponseFilePath =
          "src/test/resources/ni_commodities_api_v2_response_1006101000.json";
      var expectedResponse =
          objectMapper.readValue(
              new File(commodityResponseFilePath), TradeTariffCommodityResponse.class);
      stubCommoditiesResponse(
          COMMODITIES_NI_V2.apiPathFor("1006101000"), commodityResponseFilePath);
      // when
      final Mono<TradeTariffCommodityResponse> tradeTariffCommodityResponsePublisher =
          tradeTariffApi.getCommodity("1006101000", COMMODITIES_NI_V2);
      // then
      StepVerifier.create(tradeTariffCommodityResponsePublisher)
          .expectNext(expectedResponse)
          .verifyComplete();
    }

    @Test
    @SneakyThrows
    void shouldReturnCommodityNotFoundResponse() {
      // given
      stubNotFoundResponse(COMMODITIES_GB_V2.apiPathFor("1006101000"));
      // when
      final Mono<TradeTariffCommodityResponse> tradeTariffCommodityResponsePublisher =
          tradeTariffApi.getCommodity("1006101001", COMMODITIES_GB_V2);
      // then
      StepVerifier.create(tradeTariffCommodityResponsePublisher)
          .expectNextMatches(
              (TradeTariffCommodityResponse tradeTariffCommodityResponse) -> {
                assertThat(tradeTariffCommodityResponse.getErrors()).hasSize(1);
                assertThat(tradeTariffCommodityResponse.getErrors().get(0).getDetail())
                    .isEqualTo("404 - not found.");
                return !tradeTariffCommodityResponse.resultFound();
              })
          .verifyComplete();
    }

    @Test
    void shouldRaiseDownStreamSystemExceptionWhenApiResponseWith5XX() {
      // given
      stubInternalServerResponse(COMMODITIES_GB_V2.apiPathFor("1006101021"));
      // when
      final Mono<TradeTariffCommodityResponse> tradeTariffCommodityResponsePublisher =
          tradeTariffApi.getCommodity("1006101021", COMMODITIES_GB_V2);
      // then
      // then
      StepVerifier.withVirtualTime(() -> tradeTariffCommodityResponsePublisher)
          .thenAwait(Duration.ofSeconds(5))
          .expectError(DownstreamSystemException.class)
          .verify();
    }
  }

  @Nested
  class GetHeading {

    @Test
    @SneakyThrows
    void shouldReturnHeadingResponseForGB() {
      // given
      var objectMapper = new ObjectMapper();
      var headingResponseFilePath = "src/test/resources/gb_headings_api_v2_response_1103.json";
      var expectedResponse =
          objectMapper.readValue(
              new File(headingResponseFilePath), TradeTariffHeadingResponse.class);
      stubHeadingResponse(HEADINGS_GB_V2.apiPathFor("1103"), headingResponseFilePath);
      // when
      final Mono<TradeTariffHeadingResponse> tradeTariffHeadingResponsePublisher =
          tradeTariffApi.getHeading("1103", HEADINGS_GB_V2);
      // then
      StepVerifier.create(tradeTariffHeadingResponsePublisher)
          .expectNext(expectedResponse)
          .verifyComplete();
    }

    @Test
    @SneakyThrows
    void shouldReturnHeadingResponseForNI() {
      // given
      var objectMapper = new ObjectMapper();
      var headingResponseFilePath = "src/test/resources/ni_headings_api_v2_response_1103.json";
      var expectedResponse =
          objectMapper.readValue(
              new File(headingResponseFilePath), TradeTariffHeadingResponse.class);
      stubHeadingResponse(HEADINGS_NI_V2.apiPathFor("1103"), headingResponseFilePath);
      // when
      final Mono<TradeTariffHeadingResponse> tradeTariffHeadingResponsePublisher =
          tradeTariffApi.getHeading("1103", HEADINGS_NI_V2);
      // then
      StepVerifier.create(tradeTariffHeadingResponsePublisher)
          .expectNext(expectedResponse)
          .verifyComplete();
    }

    @Test
    @SneakyThrows
    void shouldReturnHeadingNotFoundResponse() {
      // given
      stubNotFoundResponse(HEADINGS_GB_V2.apiPathFor("1104"));
      // when
      final Mono<TradeTariffHeadingResponse> tradeTariffHeadingResponsePublisher =
          tradeTariffApi.getHeading("1104", HEADINGS_GB_V2);
      // then
      StepVerifier.create(tradeTariffHeadingResponsePublisher)
          .expectNextMatches(
              (TradeTariffHeadingResponse tradeTariffHeadingResponse) -> {
                assertThat(tradeTariffHeadingResponse.getErrors()).hasSize(1);
                assertThat(tradeTariffHeadingResponse.getErrors().get(0).getDetail())
                    .isEqualTo("404 - not found.");
                return !tradeTariffHeadingResponse.resultFound();
              })
          .verifyComplete();
    }

    @Test
    @SneakyThrows
    void shouldRaiseDownStreamSystemExceptionWhenApiResponseWith5XX() {
      // given
      stubInternalServerResponse(HEADINGS_GB_V2.apiPathFor("1105"));
      // when
      final Mono<TradeTariffHeadingResponse> tradeTariffHeadingResponsePublisher =
          tradeTariffApi.getHeading("1105", HEADINGS_GB_V2);
      // then
      StepVerifier.withVirtualTime(() -> tradeTariffHeadingResponsePublisher)
          .thenAwait(Duration.ofSeconds(5))
          .expectError(DownstreamSystemException.class)
          .verify();
    }
  }
}
