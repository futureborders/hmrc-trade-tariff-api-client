package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

import static uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.CommoditiesApiVersion.COMMODITIES_GB_V2;
import static uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.HeadingsApiVersion.HEADINGS_GB_V2;

import java.util.Collections;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.TradeTariffError;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.TradeTariffCommodityResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.TradeTariffHeadingResponse;
//import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.Timer;
//import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream.DownstreamEndpointLabelNameResolver;
//import uk.gov.cabinetoffice.bpdg.stw.monitoring.prometheus.metrics.downstream.DownstreamRequestMetrics;

@Slf4j
public class TradeTariffApi {

  private static final String DOWNSTREAM_APP_NAME = "HMRC";

  private final WebClient webClient;
  private final TradeTariffApiConfig tradeTariffApiConfig;
//  private final DownstreamRequestMetrics downstreamRequestMetrics;
//  private final DownstreamEndpointLabelNameResolver downstreamEndpointLabelNameResolver;

  public TradeTariffApi(
      WebClient.Builder webClientBuilder,
      TradeTariffApiConfig tradeTariffApiConfig /*,
      DownstreamRequestMetrics downstreamRequestMetrics,
      DownstreamEndpointLabelNameResolver downstreamEndpointLabelNameResolver */) {
    this.tradeTariffApiConfig = tradeTariffApiConfig;
//    this.downstreamRequestMetrics = downstreamRequestMetrics;
//    this.downstreamEndpointLabelNameResolver = downstreamEndpointLabelNameResolver;
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
    return this.getCommodity(commodityCode, COMMODITIES_GB_V2);
  }

  public Mono<TradeTariffCommodityResponse> getCommodity(
      String commodityCode, CommoditiesApiVersion apiVersion) {
    log.debug(
        "Calling Trade Tariff API {} with commodity code {}", apiVersion.name(), commodityCode);
//    final Timer timer = Timer.startNew();
    final String uri = apiVersion.apiPathFor(commodityCode);
//    final String resourceName = downstreamEndpointLabelNameResolver.get("GET", uri);
    return Mono.defer(
        () ->
            this.webClient
                .get()
                .uri(uri)
                .exchange()
//                .doOnSuccess(
//                    clientResponse ->
//                        downstreamRequestMetrics.record(
//                            DOWNSTREAM_APP_NAME,
//                            resourceName,
//                            String.valueOf(clientResponse.statusCode().value()),
//                            timer.end()))
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
//                .doOnError(
//                    TimeoutException.class,
//                    error ->
//                        downstreamRequestMetrics.record(
//                            DOWNSTREAM_APP_NAME,
//                            resourceName,
//                            error.getClass().getSimpleName(),
//                            timer.end()))
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
                                    retrySignal.failure()))));
  }

  private TradeTariffCommodityResponse notFoundResponse() {
    return TradeTariffCommodityResponse.builder()
        .errors(
            Collections.singletonList(
                TradeTariffError.builder().detail("404 - not found.").build()))
        .build();
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
    return this.getHeading(headingCode, HEADINGS_GB_V2);
  }

  public Mono<TradeTariffHeadingResponse> getHeading(
      String headingCode, HeadingsApiVersion apiVersion) {
    log.debug("Calling Trade Tariff API {} with heading code {}", apiVersion.name(), headingCode);
//    final Timer timer = Timer.startNew();
    final String uri = apiVersion.apiPathFor(headingCode);
//    final String resourceName = downstreamEndpointLabelNameResolver.get("GET", uri);
    return Mono.defer(
        () ->
            this.webClient
                .get()
                .uri(uri)
                .exchange()
//                .doOnSuccess(
//                    clientResponse ->
//                        downstreamRequestMetrics.record(
//                            DOWNSTREAM_APP_NAME,
//                            resourceName,
//                            String.valueOf(clientResponse.statusCode().value()),
//                            timer.end()))
                .flatMap(
                    clientResponse -> {
                      if (clientResponse.statusCode().is5xxServerError()) {
                        return Mono.error(
                            new DownstreamSystemException(
                                clientResponse.statusCode().getReasonPhrase()));
                      }
                      if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.debug(
                            "No heading found in headings API response for code {}", headingCode);
                        return Mono.just(
                            TradeTariffHeadingResponse.builder()
                                .errors(
                                    Collections.singletonList(
                                        TradeTariffError.builder()
                                            .detail("404 - not found.")
                                            .build()))
                                .build());
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
                                    retrySignal.failure()))));
  }
}
