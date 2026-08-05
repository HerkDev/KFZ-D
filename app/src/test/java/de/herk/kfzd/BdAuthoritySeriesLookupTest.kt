package de.herk.kfzd

import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.matcher.IdentifierNormalizer
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import de.herk.kfzd.data.validation.PlateDatasetValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BdAuthoritySeriesLookupTest {
    private val geographicalEntries = GeographicalPlateAssetLoader.parse(File("src/main/assets/data/german_plate_identifiers.json").inputStream())
    private val specialEntries = SpecialPlateAssetLoader.parse(File("src/main/assets/data/german_special_identifiers.json").inputStream())
    private val diplomaticEntries = DiplomaticPlateAssetLoader.parse(File("src/main/assets/data/german_diplomatic_identifiers.json").inputStream())
    private val authoritySeriesEntries = AuthoritySeriesPlateAssetLoader.parse(File("src/main/assets/data/german_authority_series.json").inputStream())
    private val allEntries = geographicalEntries + specialEntries + diplomaticEntries + authoritySeriesEntries
    private val repository = InMemoryPlateRepository(allEntries, authoritySeriesEntries)
    private val matcher = IdentifierMatcher(repository)

    @Test
    fun bdIsExactGeneralFederalServiceAndRemainsExtendable() {
        val result = repository.findByIdentifier("BD")
        val match = matcher.match("BD")
        assertEquals(listOf("Bundesdienst / Bundesbehörden"), result?.authorityNames)
        assertEquals("Deutschland", result?.federalState)
        assertEquals(PlateType.FEDERAL_AUTHORITY, result?.type)
        assertTrue(match.isExact)
        assertTrue(match.hasLongerIdentifiers)
        assertFalse(match.isTerminal)
        assertTrue(matcher.match("B").isExact)
        assertTrue(matcher.match("B").hasLongerIdentifiers)
    }

    @Test
    fun bdNumberSeriesNormalizeOptionalSpacesAndResolveSpecifically() {
        assertEquals("BD8", IdentifierNormalizer.normalize("BD 8"))
        assertEquals("BD8", IdentifierNormalizer.normalize("bd 8"))
        assertEquals("BD8", IdentifierNormalizer.normalize("BD   8"))
        assertEquals("BD16", IdentifierNormalizer.normalize("BD 16"))
        assertTrue(matcher.canAcceptInput("BD8"))
        assertTrue(matcher.canAcceptInput("BD 8"))
        assertTrue(matcher.canAcceptInput("BD16"))
        assertTrue(matcher.canAcceptInput("BD 16"))

        listOf("BD8", "BD 8", "bd8", "bd 8").forEach { input ->
            assertEquals(repository.findByIdentifier("BD8"), repository.findByIdentifier(input))
        }
        listOf("BD16", "BD 16").forEach { input ->
            assertEquals(repository.findByIdentifier("BD16"), repository.findByIdentifier(input))
        }
    }

    @Test
    fun bdSpecificResultsOverrideTheGeneralBdResult() {
        val general = repository.findByIdentifier("BD")
        val bd4 = repository.findByIdentifier("BD4")
        val bd8 = repository.findByIdentifier("BD8")
        val bd16 = repository.findByIdentifier("BD16")

        assertEquals(listOf("Bundesverfassungsgericht"), bd4?.authorityNames)
        assertEquals(PlateType.FEDERAL_CONSTITUTIONAL_COURT, bd4?.type)
        assertEquals(listOf("Generalzolldirektion / Zollverwaltung"), bd8?.authorityNames)
        assertEquals("Deutschland", bd8?.federalState)
        assertEquals(PlateType.FEDERAL_FINANCE_ADMINISTRATION, bd8?.type)
        assertEquals(listOf("Generalzolldirektion / Zollverwaltung"), bd16?.authorityNames)
        assertEquals(PlateType.FEDERAL_FINANCE_ADMINISTRATION, bd16?.type)
        assertNotEquals(general, bd4)
        assertNotEquals(general, bd8)
        assertNotEquals(general, bd16)
    }

    @Test
    fun unsupportedBdNumbersDoNotProduceInventedExactResults() {
        listOf("BD1", "BD2", "BD3", "BD5", "BD7", "BD9", "BD10", "BD15").forEach { identifier ->
            assertFalse("$identifier must not be a fabricated exact result", matcher.match(identifier).isExact)
            assertEquals(null, repository.findByIdentifier(identifier))
        }
        assertFalse(matcher.canAcceptInput("BD9"))
    }

    @Test
    fun otherHyphenatedAndAuthoritySeriesInputsRemainUnchanged() {
        assertEquals("DDQ", IdentifierNormalizer.normalize("DD-Q"))
        assertEquals("0-1", IdentifierNormalizer.normalize("0-1"))
        assertEquals("0-10", IdentifierNormalizer.normalize("0-10"))
        assertEquals(repository.findByIdentifier("DDQ"), repository.findByIdentifier("DD-Q"))
        assertEquals(repository.findByIdentifier("0-1"), repository.findByIdentifier("0-1"))
    }

    @Test
    fun allJsonDatasetsRemainValidUtf8Text() {
        File("src/main/assets/data").listFiles { file -> file.extension == "json" }!!.forEach { file ->
            val content = file.readText(Charsets.UTF_8)
            assertFalse("${file.name} contains a replacement character", content.contains('\uFFFD'))
            assertFalse("${file.name} contains mojibake", content.contains('\u00C3') || content.contains('\u00C2'))
        }
    }
    @Test
    fun allAuthoritySeriesAreUniqueReachableAndSourced() {
        assertEquals(4, authoritySeriesEntries.map { it.identifier }.distinct().size)
        authoritySeriesEntries.forEach { entry ->
            assertTrue(matcher.match(entry.identifier).isExact)
            assertEquals(entry, repository.findByIdentifier(entry.identifier))
        }
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