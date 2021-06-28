package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.heading.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = Void.class, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CommoditySection.class, name = "section"),
  @JsonSubTypes.Type(value = CommodityChapter.class, name = "chapter"),
  @JsonSubTypes.Type(value = CommodityImpl.class, name = "commodity")
})
public abstract class TradeTariffHeadingResponseIncludedEntity {
  protected String id;
}
