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

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.internal.InMemoryTimeLimiterRegistry;
import java.time.Duration;
import java.util.function.Function;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;

public class TestResilience4JConfig {

  public ReactiveCircuitBreaker reactiveCircuitBreaker() {
    final InMemoryTimeLimiterRegistry inMemoryTimeLimiterRegistry =
        new InMemoryTimeLimiterRegistry();
    inMemoryTimeLimiterRegistry.timeLimiter(
        "tradeTariffApi",
        () ->
            TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .cancelRunningFuture(false)
                .build(),
        io.vavr.collection.HashMap.empty());
    ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory =
        new ReactiveResilience4JCircuitBreakerFactory(
            circuitBreakerRegistry(), inMemoryTimeLimiterRegistry);
    reactiveResilience4JCircuitBreakerFactory.configureDefault(buildConfiguration());

    return reactiveResilience4JCircuitBreakerFactory.create("tradeTariffApi");
  }

  private CircuitBreakerRegistry circuitBreakerRegistry() {
    return CircuitBreakerRegistry.of(circuitBreakerConfig());
  }

  private CircuitBreakerConfig circuitBreakerConfig() {
    return CircuitBreakerConfig.custom()
        .slidingWindowType(SlidingWindowType.valueOf("COUNT_BASED"))
        .minimumNumberOfCalls(1)
        .slidingWindowSize(100)
        .waitDurationInOpenState(Duration.parse("PT2S"))
        .permittedNumberOfCallsInHalfOpenState(1)
        .failureRateThreshold(0.1f)
        .build();
  }

  private Function<String, Resilience4JCircuitBreakerConfiguration> buildConfiguration() {
    Resilience4JCircuitBreakerConfiguration resilience4JCircuitBreakerConfiguration =
        new Resilience4JCircuitBreakerConfiguration();
    resilience4JCircuitBreakerConfiguration.setCircuitBreakerConfig(circuitBreakerConfig());
    return (id -> resilience4JCircuitBreakerConfiguration);
  }
}
