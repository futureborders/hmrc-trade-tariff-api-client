package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.Duration;

@Value
@Builder
@AllArgsConstructor
public class TradeTariffApiConfig {

  String url;
  Duration timeout;
  Integer retryMaxAttempt;
  Duration retryMinBackoff;
}
