package org.gbif.occurrence.download.file;

import org.gbif.api.model.common.MediaObject;
import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.api.vocabulary.*;
import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.*;


/**
 * Test cases for the class {@link OccurrenceMapReader}.
 */
public class OccurrenceMapReaderTest {

  /**
   * Occurrence map test, uses a combination of different set of data types a and terms to check most of the cases.
   */
  @Test
  public void buildOccurrenceMapTest() {
    String scientificName = "Tapirus bairdii (Gill, 1865)";
    UUID datasetKey = UUID.randomUUID();
    Date today = new Date();

    URI reference = URI.create("http://www.gbif.org");
    Occurrence occurrence = new Occurrence();
    occurrence.setBasisOfRecord(BasisOfRecord.HUMAN_OBSERVATION);
    occurrence.setAcceptedScientificName(scientificName);
    occurrence.setScientificName(scientificName);
    occurrence.setContinent(Continent.NORTH_AMERICA);
    occurrence.setCountry(Country.COSTA_RICA);
    occurrence.setPublishingCountry(Country.TRINIDAD_TOBAGO);
    occurrence.setKingdomKey(1);
    occurrence.setTaxonKey(2440897);
    occurrence.setLastInterpreted(today);
    occurrence.setDecimalLatitude(89.2);
    occurrence.setDecimalLongitude(100.2);
    occurrence.setDatasetKey(datasetKey);
    occurrence.setReferences(reference);
    occurrence.setLicense(License.CC_BY_4_0);

    //Varbatim fields not populated by Java fields must be copied into the result
    occurrence.setVerbatimField(DwcTerm.institutionCode, "INST");

    //Latitude and longitude must be superseded by the interpreted values
    occurrence.setVerbatimField(DwcTerm.decimalLatitude, "89.200001");
    occurrence.setVerbatimField(DwcTerm.decimalLongitude, "100.200001");

    MediaObject mediaObjectStillImage = new MediaObject();
    mediaObjectStillImage.setTitle("Image");
    mediaObjectStillImage.setType(MediaType.StillImage);

    MediaObject mediaObjectMovingImage = new MediaObject();
    mediaObjectMovingImage.setTitle("Video");
    mediaObjectMovingImage.setType(MediaType.MovingImage);

    List<MediaObject> mediaObjects = new ArrayList<>();
    mediaObjects.add(mediaObjectMovingImage);
    mediaObjects.add(mediaObjectStillImage);

    occurrence.setMedia(mediaObjects);
    HashSet<OccurrenceIssue> issues = new HashSet<>();
    issues.add(OccurrenceIssue.COUNTRY_COORDINATE_MISMATCH);
    issues.add(OccurrenceIssue.TAXON_MATCH_FUZZY);
    occurrence.setIssues(issues);


    Map<String,String> occurrenceMap = OccurrenceMapReader.buildOccurrenceMap(occurrence);

    Assert.assertEquals(Country.COSTA_RICA.getIso2LetterCode(), occurrenceMap.get(DwcTerm.countryCode.simpleName()));
    Assert.assertEquals(Country.TRINIDAD_TOBAGO.getIso2LetterCode(), occurrenceMap.get(GbifTerm.publishingCountry.simpleName()));
    Assert.assertEquals(Continent.NORTH_AMERICA.name(), occurrenceMap.get(DwcTerm.continent.simpleName()));
    Assert.assertEquals(BasisOfRecord.HUMAN_OBSERVATION.name(), occurrenceMap.get(DwcTerm.basisOfRecord.simpleName()));
    Assert.assertEquals(scientificName, occurrenceMap.get(GbifTerm.acceptedScientificName.simpleName()));
    Assert.assertEquals(scientificName, occurrenceMap.get(DwcTerm.scientificName.simpleName()));
    Assert.assertEquals("1", occurrenceMap.get(GbifTerm.kingdomKey.simpleName()));
    Assert.assertEquals("2440897", occurrenceMap.get(GbifTerm.taxonKey.simpleName()));
    Assert.assertEquals(OccurrenceMapReader.toISO8601Date(today), occurrenceMap.get(GbifTerm.lastInterpreted.simpleName()));
    Assert.assertEquals("89.2", occurrenceMap.get(DwcTerm.decimalLatitude.simpleName()));
    Assert.assertEquals("100.2", occurrenceMap.get(DwcTerm.decimalLongitude.simpleName()));
    Assert.assertEquals(Boolean.TRUE.toString(), occurrenceMap.get(GbifTerm.hasCoordinate.simpleName()));
    Assert.assertEquals(Boolean.TRUE.toString(), occurrenceMap.get(GbifTerm.repatriated.simpleName()));
    Assert.assertEquals(datasetKey.toString(), occurrenceMap.get(GbifTerm.datasetKey.simpleName()));
    Assert.assertEquals(reference.toString(), occurrenceMap.get(DcTerm.references.simpleName()));
    Assert.assertEquals(License.CC_BY_4_0.name(), occurrenceMap.get(DcTerm.license.simpleName()));
    Assert.assertTrue(occurrenceMap.get(GbifTerm.mediaType.simpleName()).contains(MediaType.StillImage.name()));
    Assert.assertTrue(occurrenceMap.get(GbifTerm.mediaType.simpleName()).contains(MediaType.MovingImage.name()));
    Assert.assertTrue(occurrenceMap.get(GbifTerm.issue.simpleName()).contains(OccurrenceIssue.COUNTRY_COORDINATE_MISMATCH.name()));
    Assert.assertTrue(occurrenceMap.get(GbifTerm.issue.simpleName()).contains(OccurrenceIssue.TAXON_MATCH_FUZZY.name()));
    Assert.assertEquals(Boolean.TRUE.toString(), occurrenceMap.get(GbifTerm.hasGeospatialIssues.simpleName()));
    Assert.assertEquals(occurrenceMap.get(DwcTerm.institutionCode.simpleName()), "INST");
    Assert.assertEquals(occurrenceMap.get(DwcTerm.decimalLatitude.simpleName()), "89.2");
    Assert.assertEquals(occurrenceMap.get(DwcTerm.decimalLongitude.simpleName()), "100.2");
  }
}