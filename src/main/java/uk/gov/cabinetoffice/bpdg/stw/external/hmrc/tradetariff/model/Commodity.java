package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model;

import java.util.Optional;

public interface Commodity {
  String getGoodsNomenclatureItemId();

  String getDescription();

  Integer getNumberIndents();

  Integer getProductLineSuffix();

  Optional<Boolean> getLeaf();

  Optional<Integer> getParentSid();

  Optional<Integer> getSid();
}
