package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

public enum CommoditiesApiVersion {
  COMMODITIES_GB_V2("/api/v2/commodities/%s"),
  COMMODITIES_NI_V2("/xi/api/v2/commodities/%s");

  private final String apiPath;

  public String apiPathFor(String entity){
    return String.format(this.apiPath,entity);
  }

  CommoditiesApiVersion(String apiPath) {
    this.apiPath = apiPath;
  }
}
