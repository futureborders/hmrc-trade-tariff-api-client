package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff;

public enum HeadingsApiVersion {
  HEADINGS_GB_V2("/api/v2/headings/%s"),
  HEADINGS_NI_V2("/xi/api/v2/headings/%s");

  private final String apiPath;

  public String apiPathFor(String headingCode){
    return String.format(this.apiPath,headingCode);
  }

  HeadingsApiVersion(String apiPath) {
    this.apiPath = apiPath;
  }
}
