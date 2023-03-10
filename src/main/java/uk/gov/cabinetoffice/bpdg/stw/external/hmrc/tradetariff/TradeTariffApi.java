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

package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

import static uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.CommoditiesApiVersion.COMMODITIES_GB_V2;
import static uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.HeadingsApiVersion.HEADINGS_GB_V2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.TradeTariffError;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TradeTariffCommodityResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.TradeTariffHeadingResponse;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.Timer;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream.DownstreamEndpointLabelNameResolver;
import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream.DownstreamRequestMetrics;

@Slf4j
public class TradeTariffApi {

  private static final String DOWNSTREAM_APP_NAME = "HMRC";

  private final WebClient webClient;
  private final TradeTariffApiConfig tradeTariffApiConfig;
  private final DownstreamRequestMetrics downstreamRequestMetrics;
  private final DownstreamEndpointLabelNameResolver downstreamEndpointLabelNameResolver;
  private final ReactiveCircuitBreaker reactiveCircuitBreaker;

  public TradeTariffApi(
      WebClient.Builder webClientBuilder,
      TradeTariffApiConfig tradeTariffApiConfig,
      DownstreamRequestMetrics downstreamRequestMetrics,
      DownstreamEndpointLabelNameResolver downstreamEndpointLabelNameResolver,
      ReactiveCircuitBreaker reactiveCircuitBreaker) {
    this.tradeTariffApiConfig = tradeTariffApiConfig;
    this.downstreamRequestMetrics = downstreamRequestMetrics;
    this.downstreamEndpointLabelNameResolver = downstreamEndpointLabelNameResolver;
    this.reactiveCircuitBreaker = reactiveCircuitBreaker;
    this.webClient =
        webClientBuilder
            .baseUrl(tradeTariffApiConfig.getUrl())
            .clientConnector(
                new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
            .filter(requestFilter())
            .filter(responseFilter())
            .build();
  }

  public Mono<TradeTariffCommodityResponse> getCommodity(String commodityCode) {
    return this.getCommodity(commodityCode, LocalDate.now(), COMMODITIES_GB_V2);
  }

  public Mono<TradeTariffCommodityResponse> getCommodity(
      String commodityCode, LocalDate asOf, CommoditiesApiVersion apiVersion) {
    log.debug(
        "Calling Trade Tariff API: {} with commodity code: {} and asOf: {}",
        apiVersion.name(),
        commodityCode,
        asOf);
    final Timer timer = Timer.startNew();
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromUriString(apiVersion.apiPathFor(commodityCode));
    uriBuilder.queryParam("as_of", asOf.format(DateTimeFormatter.ISO_DATE));
    var uri = uriBuilder.toUriString();
    final String resourceName = downstreamEndpointLabelNameResolver.get("GET", uri);
    return this.reactiveCircuitBreaker.run(
        this.webClient
            .get()
            .uri(uri)
            .exchange()
            .doOnSuccess(
                clientResponse ->
                    downstreamRequestMetrics.record(
                        DOWNSTREAM_APP_NAME,
                        resourceName,
                        String.valueOf(clientResponse.statusCode().value()),
                        timer.end()))
            .flatMap(
                clientResponse -> {
                  if (clientResponse.statusCode().is5xxServerError()) {
                    return Mono.error(
                        new DownstreamSystemException(
                            clientResponse.statusCode().getReasonPhrase()));
                  }
                  if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    log.debug(
                        "No commodity found in commodities API response for code {}",
                        commodityCode);
                    return Mono.just(notFoundResponse());
                  }
                  return clientResponse
                      .bodyToMono(TradeTariffCommodityResponse.class)
                      .onErrorReturn(notFoundResponse());
                })
            .timeout(tradeTariffApiConfig.getTimeout())
            .doOnError(
                TimeoutException.class,
                error ->
                    downstreamRequestMetrics.record(
                        DOWNSTREAM_APP_NAME,
                        resourceName,
                        error.getClass().getSimpleName(),
                        timer.end()))
            .retryWhen(
                Retry.backoff(
                        tradeTariffApiConfig.getRetryMaxAttempt(),
                        tradeTariffApiConfig.getRetryMinBackoff())
                    .onRetryExhaustedThrow(
                        (retryBackoffSpec, retrySignal) ->
                            new DownstreamSystemException(retrySignal.failure().getMessage()))
                    .doBeforeRetry(
                        retrySignal ->
                            log.warn(
                                "Retrying calling Trade Tariff API {} ..",
                                apiVersion.name(),
                                retrySignal.failure()))),
        throwable ->
            Mono.error(
                new DownstreamSystemException(
                    String.format(
                        "TradeTariffApi is down, unable to process the request and request path is: %s",
                        uri))));
  }

  private TradeTariffCommodityResponse notFoundResponse() {
      TradeTariffCommodityResponse response = new TradeTariffCommodityResponse();
      response.setErrors(Collections.singletonList(
              TradeTariffError.builder().detail("404 - not found.").build()));
    return response;
  }

  private ExchangeFilterFunction responseFilter() {
    return ExchangeFilterFunction.ofResponseProcessor(
        clientResponse -> {
          log.info("Response: {}", clientResponse.statusCode().value());
          return Mono.just(clientResponse);
        });
  }

  private ExchangeFilterFunction requestFilter() {
    return ExchangeFilterFunction.ofRequestProcessor(
        clientRequest -> {
          log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
          return Mono.just(clientRequest);
        });
  }

  public Mono<TradeTariffHeadingResponse> getHeading(String headingCode) {
    return this.getHeading(headingCode, LocalDate.now(), HEADINGS_GB_V2);
  }

  public Mono<TradeTariffHeadingResponse> getHeading(
      String headingCode, LocalDate asOf, HeadingsApiVersion apiVersion) {
    log.debug(
        "Calling Trade Tariff API: {} with heading code: {} and asOf: {}",
        apiVersion.name(),
        headingCode,
        asOf);
    final Timer timer = Timer.startNew();
    final String uri = apiVersion.apiPathFor(headingCode);
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromUriString(apiVersion.apiPathFor(headingCode));
    uriBuilder.queryParam("as_of", asOf.format(DateTimeFormatter.ISO_DATE));
    final String resourceName = downstreamEndpointLabelNameResolver.get("GET", uri);
    return this.reactiveCircuitBreaker.run(
        this.webClient
            .get()
            .uri(uri)
            .exchange()
            .doOnSuccess(
                clientResponse ->
                    downstreamRequestMetrics.record(
                        DOWNSTREAM_APP_NAME,
                        resourceName,
                        String.valueOf(clientResponse.statusCode().value()),
                        timer.end()))
            .flatMap(
                clientResponse -> {
                  if (clientResponse.statusCode().is5xxServerError()) {
                    return Mono.error(
                        new DownstreamSystemException(
                            clientResponse.statusCode().getReasonPhrase()));
                  }
                  if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    log.debug("No heading found in headings API response for code {}", headingCode);
                      TradeTariffHeadingResponse response = new TradeTariffHeadingResponse();
                      response.setErrors(Collections.singletonList(
                              TradeTariffError.builder().detail("404 - not found.").build()));
                    return Mono.just(
                        response);
                  }

                  return clientResponse.bodyToMono(TradeTariffHeadingResponse.class);
                })
            .timeout(tradeTariffApiConfig.getTimeout())
            .doOnError(
                TimeoutException.class,
                error ->
                    downstreamRequestMetrics.record(
                        DOWNSTREAM_APP_NAME,
                        resourceName,
                        error.getClass().getSimpleName(),
                        timer.end()))
            .doOnSuccess(
                response ->
                    log.debug("Received response from Trade Tariff API {}.", apiVersion.name()))
            .retryWhen(
                Retry.backoff(
                        tradeTariffApiConfig.getRetryMaxAttempt(),
                        tradeTariffApiConfig.getRetryMinBackoff())
                    .onRetryExhaustedThrow(
                        (retryBackoffSpec, retrySignal) ->
                            new DownstreamSystemException(retrySignal.failure().getMessage()))
                    .doBeforeRetry(
                        retrySignal ->
                            log.error(
                                "Retrying calling Trade Tariff API {} ..",
                                apiVersion.name(),
                                retrySignal.failure()))),
        throwable ->
            Mono.error(
                new DownstreamSystemException(
                    String.format(
                        "TradeTariffApi is down, unable to process the request and request path is: %s",
                        uri))));
  }
}
