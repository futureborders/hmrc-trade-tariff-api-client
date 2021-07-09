package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.TradeTariffResponse;
import uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.relationships.TradeTariffHeadingResponseIncludedEntity;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeTariffHeadingResponse
    extends TradeTariffResponse<TradeTariffHeadingResponseData> {
  @Builder.Default
  private List<TradeTariffHeadingResponseIncludedEntity> included = new ArrayList<>();
}
