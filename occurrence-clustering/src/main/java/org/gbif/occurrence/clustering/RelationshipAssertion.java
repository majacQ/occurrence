package org.gbif.occurrence.clustering;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Models relationships between occurrence records.
 *
 * This uses a String type for record identifiers allowing it to be reused beyond GBIF indexing (e.g. Atlas of Living
 * Australia)
 */
public class RelationshipAssertion<T extends OccurrenceFeatures> {

  // Capture the reasoning why a relationship
  public enum FEATURE_ASSERTION {
    SAME_ACCEPTED_SPECIES, // i.e. binomial level
    SAME_DATE,
    NON_CONFLICTING_DATE, // e.g. one side is null
    APPROXIMATE_DATE, // e.g. one day apart accommodating for timezone quirks in data
    DIFFERENT_DATE, // we accommodate 1 day difference for timezone quirks and e.g. collection of a trap set overnight
    SAME_COORDINATES,
    WITHIN_200m, // 3 decimal place at the equator is 157m
    WITHIN_2Km,
    NON_CONFLICTING_COORDINATES,
    SAME_COUNTRY,
    NON_CONFLICTING_COUNTRY,
    DIFFERENT_COUNTRY,
    IDENTIFIERS_OVERLAP,
    SAME_RECORDER_NAME,
    SAME_SPECIMEN, // use with caution (e.g. same name and both HOLOTYPE)
    TYPIFICATION_RELATION // e.g. Holotype+isotype for same name
  }


  private final T o1;
  private final T o2;
  private final Set<FEATURE_ASSERTION> justification = new TreeSet<>(); // reasons the assertion is being made

  public RelationshipAssertion(T o1, T o2) {
    this.o1 = o1;
    this.o2 = o2;
  }

  public void collect(FEATURE_ASSERTION reason) {
    justification.add(reason);
  }

  public T getOcc1() {
    return o1;
  }

  public T getOcc2() {
    return o2;
  }

  public String getJustificationAsDelimited() {
    return justification.stream().map(Enum::name).collect(Collectors.joining(","));
  }

  public boolean justificationContains(FEATURE_ASSERTION reason) {
    return justification.contains(reason);
  }

  public boolean justificationContainsAll(FEATURE_ASSERTION... reason) {
    return justification.containsAll(Arrays.asList(reason));
  }

  public boolean justificationDoesNotContain(FEATURE_ASSERTION... reason) {
    for (FEATURE_ASSERTION f : reason) {
      if (justification.contains(f)) return false;
    }
    return true;
  }
}
