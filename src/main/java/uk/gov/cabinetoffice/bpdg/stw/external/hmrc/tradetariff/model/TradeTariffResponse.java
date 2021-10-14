package uk.gov.cabinetoffice.bpdg.stw.external.hmrc.tradetariff.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class TradeTariffResponse<T> {

  protected T data;

  @Builder.Default protected List<TradeTariffError> errors = new ArrayList<>();

  @JsonIgnore
  public Boolean resultFound() {
    return errors.isEmpty() && Optional.ofNullable(data).isPresent();
  }

  @JsonIgnore
  public Section getSection() {
    return getFirstIncludedType(Section.class)
        .orElseThrow(() -> new IllegalArgumentException("No section for this heading"));
  }

  @JsonIgnore
  public Chapter getChapter() {
    return getFirstIncludedType(Chapter.class)
        .orElseThrow(() -> new IllegalArgumentException("No chapter for this heading"));
  }

  @JsonIgnore
  public List<Commodity> getIncludedCommodities() {
    return getIncludedType(Commodity.class);
  }

  @JsonIgnore
  protected <T1> Optional<T1> getFirstIncludedType(Class<T1> clazz) {
    return getIncludedType(clazz).stream().findFirst();
  }

  protected <T1> List<T1> getIncludedType(Class<T1> clazz) {
    return getIncluded().stream()
        .filter(Objects::nonNull)
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .collect(Collectors.toList());
  }

  protected abstract List<?> getIncluded();
}
