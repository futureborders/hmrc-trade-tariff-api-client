package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model.commodity.relationships;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityMeasureCondition extends TradeTariffCommodityResponseIncludedEntity {

  private String conditionCode;
  private String condition;
  private String documentCode;
  private String requirement;
  private String action;
  private String dutyExpression;

  @JsonProperty("attributes")
  private void unpackAttributes(Map<String, String> attributes) {
    this.conditionCode = attributes.getOrDefault("condition_code", "");
    this.condition = attributes.getOrDefault("condition", "");
    this.documentCode = attributes.getOrDefault("document_code", "");
    this.requirement = attributes.getOrDefault("requirement", "");
    this.action = attributes.getOrDefault("action", "");
    this.dutyExpression = attributes.getOrDefault("duty_expression", "");
  }
}
