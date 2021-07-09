package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

public class DownstreamSystemException extends RuntimeException {

  public DownstreamSystemException(final String message){
    super(message);
  }
}
