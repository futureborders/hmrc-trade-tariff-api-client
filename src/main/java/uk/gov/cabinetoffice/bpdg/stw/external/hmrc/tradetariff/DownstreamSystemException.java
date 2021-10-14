package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class DownstreamSystemException extends RuntimeException {

  public DownstreamSystemException(final String message){
    super(message);
  }
}
