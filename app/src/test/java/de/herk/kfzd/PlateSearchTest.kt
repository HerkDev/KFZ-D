package de.herk.kfzd

import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import de.herk.kfzd.data.validation.PlateDatasetValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PlateSearchTest {
    private val geographicalEntries = GeographicalPlateAssetLoader.parse(
        File("src/main/assets/data/german_plate_identifiers.json").inputStream()
    )
    private val specialEntries = SpecialPlateAssetLoader.parse(
        File("src/main/assets/data/german_special_identifiers.json").inputStream()
    )
    private val authoritySeriesEntries = AuthoritySeriesPlateAssetLoader.parse(
        File("src/main/assets/data/german_authority_series.json").inputStream()
    )
    private val diplomaticEntries = DiplomaticPlateAssetLoader.parse(
        File("src/main/assets/data/german_diplomatic_identifiers.json").inputStream()
    )
    private val allEntries = geographicalEntries + specialEntries + diplomaticEntries + authoritySeriesEntries
    private val repository = InMemoryPlateRepository(allEntries, authoritySeriesEntries)
    private val matcher = IdentifierMatcher(repository)

    @Test
    fun allAssetsLoadWithCurrentCounts() {
        assertEquals(691, geographicalEntries.size)
        assertEquals(25, specialEntries.size)
        assertEquals(208, diplomaticEntries.size)
        assertEquals(4, authoritySeriesEntries.size)
        assertEquals(928, allEntries.size)
        assertEquals(geographicalEntries.sortedBy { it.identifier }, geographicalEntries)
    }

    @Test
    fun currentDatasetHasNoDuplicatesOrMissingRequiredFields() {
        val report = PlateDatasetValidator.validate(allEntries, authoritySeriesEntries, matcher::canAcceptInput)
        assertTrue(report.duplicateIdentifiers.isEmpty())
        assertTrue(report.missingRequiredFields.isEmpty())
        assertTrue(report.missingAuthorityTypes.isEmpty())
        assertTrue(report.mismatchedAuthorityCounts.isEmpty())
        assertTrue(report.authoritySeriesMissingSources.isEmpty())
        assertTrue(report.authoritySeriesMissingSourceTypes.isEmpty())
        assertTrue(report.authoritySeriesMissingAuthorityNames.isEmpty())
        assertTrue(report.authoritySeriesMissingFederalStates.isEmpty())
        assertTrue(report.invalidAuthoritySeriesTypes.isEmpty())
        assertTrue(report.unreachableAuthoritySeriesIdentifiers.isEmpty())
        assertTrue(geographicalEntries.none { it.status.name == "HISTORICAL" })
        assertTrue(geographicalEntries.none { it.status.name == "EXPIRING" })
    }

    @Test
    fun currentGeographicalResultsContainNoHistoricalPresentationData() {
        geographicalEntries.forEach { entry ->
            assertTrue(entry.authorities.all { authority ->
                !authority.name.contains("ehem.") &&
                    !authority.name.contains("historisch", ignoreCase = true) &&
                    !authority.name.contains("wiedereingef?hrt", ignoreCase = true) &&
                    !authority.name.contains("auslaufend", ignoreCase = true)
            })
            assertTrue(entry.authorities.map { it.name }.distinct().size == entry.authorities.size)
            assertTrue(entry.authorities.map { it.name to it.authorityType }.distinct().size == entry.authorities.size)
            assertTrue(entry.authorities.none { it.name.contains(Regex("\\s+[a-z]\\)$")) })
        }
    }

    @Test
    fun lUsesOnlyCurrentLeipzigAllocation() {
        val entry = repository.findByIdentifier("L")
        assertEquals(listOf("Leipzig"), entry?.authorities?.map { it.name })
        assertEquals(listOf(GeographicalAuthorityType.INDEPENDENT_CITY), entry?.authorities?.map { it.authorityType })
        assertEquals("Sachsen", entry?.federalState)
        assertFalse(entry?.authorities?.any { it.name.contains("Lahn-Dill-Kreis") } == true)
    }

    @Test
    fun alfAndRiUseOnlyCurrentAuthorities() {
        val alf = repository.findByIdentifier("ALF")
        assertEquals(listOf("Landkreis Hildesheim"), alf?.authorities?.map { it.name })
        assertEquals("Niedersachsen", alf?.federalState)
        assertEquals(listOf(GeographicalAuthorityType.DISTRICT), alf?.authorities?.map { it.authorityType })

        val ri = repository.findByIdentifier("RI")
        assertEquals(listOf("Landkreis Schaumburg"), ri?.authorities?.map { it.name })
        assertEquals("Niedersachsen", ri?.federalState)
        assertEquals(listOf(GeographicalAuthorityType.DISTRICT), ri?.authorities?.map { it.authorityType })
    }

    @Test
    fun currentGeographicalTypesRemainSpecific() {
        assertAuthority("B", "Berlin", GeographicalAuthorityType.CITY_STATE)
        assertAuthority("HH", "Hamburg", GeographicalAuthorityType.CITY_STATE)
        assertAuthority("HB", "Bremen", GeographicalAuthorityType.CITY_STATE)
        assertAuthority("M", "München, Landeshauptstadt", GeographicalAuthorityType.STATE_CAPITAL)
        assertAuthority("M", "Landkreis München", GeographicalAuthorityType.DISTRICT)
        assertAuthority("F", "Frankfurt am Main", GeographicalAuthorityType.INDEPENDENT_CITY)
        assertAuthority("H", "Region Hannover", GeographicalAuthorityType.CITY_REGION)
        assertAuthority("AC", "Städteregion Aachen", GeographicalAuthorityType.CITY_REGION)
        assertAuthority("SB", "Regionalverband Saarbrücken", GeographicalAuthorityType.REGIONAL_ASSOCIATION)
        assertAuthority("EF", "Erfurt", GeographicalAuthorityType.INDEPENDENT_CITY)
    }

    @Test
    fun efExactLookupHasMandatoryFieldsAndDocumentedSource() {
        val entry = repository.findByIdentifier("EF")
        assertEquals("Erfurt", entry?.authorityNames?.single())
        assertEquals("Thüringen", entry?.federalState)
        assertEquals(PlateType.GEOGRAPHICAL, entry?.type)
        assertEquals(GeographicalAuthorityType.INDEPENDENT_CITY, entry?.authorities?.single()?.authorityType)
        assertEquals("https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf", entry?.source)
        assertEquals("PRIMARY", entry?.sourceType?.name)
    }

    @Test
    fun muExactLookupUsesLandkreisMuenchen() {
        assertEquals(listOf("Landkreis München"), repository.findByIdentifier("MU")?.authorityNames)
    }

    @Test
    fun allGeographicalIdentifiersRemainDuplicateFree() {
        assertEquals(geographicalEntries.size, geographicalEntries.map { it.identifier }.distinct().size)
    }

    @Test
    fun allCombinedIdentifiersRemainReachableThroughMatcher() {
        allEntries.forEach { entry ->
            assertTrue("${entry.identifier} is not reachable", matcher.canAcceptInput(entry.identifier))
        }
    }

    @Test
    fun invalidIdentifierIsRejected() {
        assertFalse(matcher.canAcceptInput("ZZZ"))
        assertFalse(matcher.canAcceptNextCharacter("HH", "HHX"))
    }

    @Test
    fun specialAndDiplomaticDatasetsRemainAvailable() {
        assertEquals(PlateType.TECHNICAL_RELIEF, repository.findByIdentifier("THW")?.type)
        assertEquals(PlateType.MILITARY, repository.findByIdentifier("Y")?.type)
        assertEquals(PlateType.FEDERAL_AUTHORITY, repository.findByIdentifier("BP")?.type)
        assertEquals("Heiliger Stuhl", repository.findByIdentifier("0-10")?.authorityNames?.single())
    }

    private fun assertAuthority(identifier: String, name: String, type: GeographicalAuthorityType) {
        val authority = repository.findByIdentifier(identifier)?.authorities?.find { it.name == name }
        assertEquals(type, authority?.authorityType)
    }
}
