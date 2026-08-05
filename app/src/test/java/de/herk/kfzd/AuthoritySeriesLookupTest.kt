package de.herk.kfzd

import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.matcher.IdentifierNormalizer
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import de.herk.kfzd.data.validation.PlateDatasetValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AuthoritySeriesLookupTest {
    private val geographicalEntries = GeographicalPlateAssetLoader.parse(File("src/main/assets/data/german_plate_identifiers.json").inputStream())
    private val specialEntries = SpecialPlateAssetLoader.parse(File("src/main/assets/data/german_special_identifiers.json").inputStream())
    private val diplomaticEntries = DiplomaticPlateAssetLoader.parse(File("src/main/assets/data/german_diplomatic_identifiers.json").inputStream())
    private val authoritySeriesEntries = AuthoritySeriesPlateAssetLoader.parse(File("src/main/assets/data/german_authority_series.json").inputStream())
    private val allEntries = geographicalEntries + specialEntries + diplomaticEntries + authoritySeriesEntries
    private val repository = InMemoryPlateRepository(allEntries, authoritySeriesEntries)
    private val matcher = IdentifierMatcher(repository)

    @Test
    fun ddRemainsTheExactAndExtendableDresdenIdentifier() {
        val entry = repository.findByIdentifier("DD")
        val match = matcher.match("DD")
        assertEquals(listOf("Dresden"), entry?.authorities?.map { it.name })
        assertEquals("Sachsen", entry?.federalState)
        assertEquals(PlateType.GEOGRAPHICAL, entry?.type)
        assertEquals(listOf(GeographicalAuthorityType.INDEPENDENT_CITY), entry?.authorities?.map { it.authorityType })
        assertTrue(match.isExact)
        assertTrue(match.hasLongerIdentifiers)
        assertFalse(match.isTerminal)
        assertTrue(matcher.canAcceptInput("D"))
        assertTrue(matcher.match("DD ").hasLongerIdentifiers)
    }

    @Test
    fun ddqNormalizesSpaceAndLegacyHyphenAliasesToTheSamePoliceSeries() {
        val canonical = repository.findByIdentifier("DDQ")
        assertNotNull(canonical)
        assertEquals("DDQ", authoritySeriesEntries.single { it.identifier == "DDQ" }.identifier)
        listOf("DD Q", "DDQ", "dd q", "DD-Q").forEach { input ->
            assertEquals("DDQ", IdentifierNormalizer.normalize(input))
            assertTrue("$input must be accepted", matcher.canAcceptInput(input))
            assertEquals(canonical, repository.findByIdentifier(input))
        }
        assertEquals(listOf("Polizei Sachsen"), canonical?.authorityNames)
        assertEquals("Sachsen", canonical?.federalState)
        assertEquals(PlateType.STATE_POLICE, canonical?.type)
        assertFalse(canonical?.authorityNames?.contains("Dresden") == true)
        assertTrue(matcher.match("DDQ").isExact)
        assertTrue(matcher.match("DDQ").isTerminal)
    }

    @Test
    fun hyphenatedSpecialIdentifiersAndUnrelatedSpacingRemainUntouched() {
        assertEquals("0-1", IdentifierNormalizer.normalize(" 0-1 "))
        assertEquals("0-10", IdentifierNormalizer.normalize("0-10"))
        assertEquals("BD8", IdentifierNormalizer.normalize("BD 8"))
        assertEquals(repository.findByIdentifier("0-1"), repository.findByIdentifier(" 0-1 "))
        assertEquals(repository.findByIdentifier("0-10"), repository.findByIdentifier("0-10"))
    }

    @Test
    fun invalidContinuationAfterTerminalDdqIsRejected() {
        assertFalse(matcher.canAcceptNextCharacter("DDQ", "DDQX"))
        assertFalse(matcher.canAcceptInput("DDQX"))
        assertFalse(matcher.canAcceptInput("DD-QX"))
    }

    @Test
    fun everyAuthoritySeriesIdentifierResolvesThroughTheRepository() {
        authoritySeriesEntries.forEach { entry ->
            assertTrue("${entry.identifier} is not exact", matcher.match(entry.identifier).isExact)
            assertEquals(entry, repository.findByIdentifier(entry.identifier))
        }
    }

    @Test
    fun allJsonDatasetsHaveNoDuplicateCanonicalIdentifier() {
        val report = PlateDatasetValidator.validate(allEntries, authoritySeriesEntries, matcher::canAcceptInput)
        assertTrue(report.duplicateIdentifiers.isEmpty())
        assertTrue(report.authoritySeriesMissingSources.isEmpty())
        assertTrue(report.authoritySeriesMissingSourceTypes.isEmpty())
        assertTrue(report.authoritySeriesMissingAuthorityNames.isEmpty())
        assertTrue(report.authoritySeriesMissingFederalStates.isEmpty())
        assertTrue(report.invalidAuthoritySeriesTypes.isEmpty())
        assertTrue(report.unreachableAuthoritySeriesIdentifiers.isEmpty())
    }
}