package de.herk.kfzd

import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import de.herk.kfzd.data.validation.PlateDatasetValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DatasetQualityAssuranceTest {
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
    fun focusedDatasetQualityAssuranceReportsResults() {
        val sample = buildSample()
        val findings = mutableListOf<String>()
        var exactLookups = 0
        var failedExactLookups = 0
        var duplicateAuthorities = 0
        var duplicateTypes = 0
        var footnoteMarkers = 0
        var encodingFragments = 0
        var blankFields = 0
        var mixedHistoricalAllocations = 0

        sample.forEach { entry ->
            val result = repository.findByIdentifier(entry.identifier)
            if (result == null) {
                failedExactLookups++
                findings += "${entry.identifier}: exact lookup failed"
                return@forEach
            }
            exactLookups++
            if (!matcher.canAcceptInput(entry.identifier)) findings += "${entry.identifier}: matcher rejected exact identifier"

            val visibleNames = result.authorities.map { it.name }.ifEmpty { result.authorityNames }
            if (visibleNames.any(String::isBlank) || result.federalState.isBlank()) blankFields++
            if (visibleNames.distinct().size != visibleNames.size) duplicateAuthorities++
            val visibleTypes = if (result.type == PlateType.GEOGRAPHICAL) {
                result.authorities.map { it.authorityType.name }.distinct()
            } else {
                listOf(result.type.name)
            }
            if (visibleTypes.distinct().size != visibleTypes.size) duplicateTypes++
            val visibleText = (visibleNames + result.federalState + visibleTypes).joinToString(" ")
            if (Regex(listOf(0xC3, 0xC2, 0xFFFD).joinToString("|") { it.toChar().toString() }).containsMatchIn(visibleText)) encodingFragments++
            if (Regex("\\s+\\([a-z]\\)$|\\[[0-9]+]", RegexOption.IGNORE_CASE).containsMatchIn(visibleText)) footnoteMarkers++
            if (Regex("https?://|source:", RegexOption.IGNORE_CASE).containsMatchIn(visibleText)) findings += "${entry.identifier}: raw source annotation visible"
            if (Regex("ehem\\.|historisch|wiedereingeführt|auslaufend", RegexOption.IGNORE_CASE).containsMatchIn(visibleText)) mixedHistoricalAllocations++
        }

        assertExpected("L", listOf("Leipzig"), "Sachsen", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.INDEPENDENT_CITY))
        assertExpected("ALF", listOf("Landkreis Hildesheim"), "Niedersachsen", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.DISTRICT))
        assertExpected("RI", listOf("Landkreis Schaumburg"), "Niedersachsen", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.DISTRICT))
        assertExpected("B", listOf("Berlin"), "Berlin", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.CITY_STATE))
        assertExpected("HH", listOf("Hamburg"), "Hamburg", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.CITY_STATE))
        assertExpected("H", listOf("Region Hannover"), "Niedersachsen", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.CITY_REGION))
        assertExpected("AC", listOf("Städteregion Aachen"), "Nordrhein-Westfalen", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.CITY_REGION))
        assertExpected("HRO", listOf("Hansestadt Rostock"), "Mecklenburg-Vorpommern", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.INDEPENDENT_CITY))
        assertExpected("SB", listOf("Regionalverband Saarbrücken", "Saarbrücken"), "Saarland", PlateType.GEOGRAPHICAL, listOf(GeographicalAuthorityType.REGIONAL_ASSOCIATION, GeographicalAuthorityType.INDEPENDENT_CITY))
        assertExpected("THW", listOf("Technisches Hilfswerk"), "Deutschland", PlateType.TECHNICAL_RELIEF)
        assertExpected("Y", listOf("Bundeswehr"), "Deutschland", PlateType.MILITARY)
        assertExpected("BP", listOf("Bundespolizei"), "Deutschland", PlateType.FEDERAL_AUTHORITY)
        assertExpected("0-1", listOf("Bundespräsident"), "Deutschland", PlateType.GOVERNMENT)
        assertExpected("0-2", listOf("Bundeskanzler"), "Deutschland", PlateType.GOVERNMENT)
        assertExpected("0-3", listOf("Bundesminister des Auswärtigen"), "Deutschland", PlateType.GOVERNMENT)
        assertExpected("0-4", listOf("Erster beamteter Staatssekretär im Auswärtigen Amt"), "Deutschland", PlateType.GOVERNMENT)
        assertExpected("0-10", listOf("Heiliger Stuhl"), "Deutschland", PlateType.DIPLOMATIC_CORPS)
        assertExpected("0-11", listOf("Ägypten"), "Deutschland", PlateType.DIPLOMATIC_CORPS)
        assertExpected("0-12", listOf("Angola"), "Deutschland", PlateType.DIPLOMATIC_CORPS)

        val report = """
            |Focused dataset QA report
            |Total identifiers tested: ${sample.size}
            |Successful exact lookups: $exactLookups
            |Failed exact lookups: $failedExactLookups
            |Incorrect authority names: 0 (mandatory regression set passed)
            |Incorrect federal states: 0 (mandatory regression set passed)
            |Incorrect types: 0 (mandatory regression set passed)
            |Duplicate visible authorities: $duplicateAuthorities
            |Duplicate visible types: $duplicateTypes
            |Footnote markers: $footnoteMarkers
            |Suspicious encoding fragments: $encodingFragments
            |Blank required fields: $blankFields
            |Mixed current/former allocations: $mixedHistoricalAllocations
            |Sample identifiers: ${sample.joinToString(", ") { it.identifier }}
            |Uncertain secondary-source entries left unchanged: ${uncertainEntries().joinToString(", ")}
        """.trimMargin()
        println(report)

        assertTrue(report, failedExactLookups == 0)
        assertTrue(report, findings.isEmpty())
        assertEquals(report, 0, duplicateAuthorities)
        assertEquals(report, 0, duplicateTypes)
        assertEquals(report, 0, footnoteMarkers)
        assertEquals(report, 0, encodingFragments)
        assertEquals(report, 0, blankFields)
        assertEquals(report, 0, mixedHistoricalAllocations)
    }

    private fun buildSample(): List<PlateEntry> {
        val oneLetter = geographicalEntries.filter { it.identifier.length == 1 }.take(15)
        val twoLetters = geographicalEntries.filter { it.identifier.length == 2 }.take(30)
        val threeLetters = geographicalEntries.filter { it.identifier.length == 3 }.take(30)
        val shared = geographicalEntries.filter { it.authorities.size > 1 }.take(10)
        val diplomatic = diplomaticEntries.filter { it.type == PlateType.DIPLOMATIC_CORPS }.take(15)
        val international = diplomaticEntries.filter { it.type == PlateType.INTERNATIONAL_ORGANISATION }.take(10)
        val required = listOf("B", "F", "H", "K", "L", "M", "N", "S", "AC", "ALF", "BP", "BD", "HB", "HH", "RI", "SB", "THW", "WES", "HRO", "MSE", "MSH", "MSP", "Y", "0-1", "0-2", "0-3", "0-4", "0-10", "0-11", "0-12")
        val selected = (oneLetter + twoLetters + threeLetters + shared + specialEntries + authoritySeriesEntries + diplomatic + international + required.mapNotNull(repository::findByIdentifier))
            .distinctBy { it.identifier }
        check(oneLetter.size >= 15 && twoLetters.size >= 30 && threeLetters.size >= 30 && shared.size >= 10)
        check(specialEntries.all { it in selected })
        check(diplomatic.size >= 15 && international.size >= 10)
        return selected
    }

    private fun assertExpected(
        identifier: String,
        names: List<String>,
        federalState: String,
        type: PlateType,
        authorityTypes: List<GeographicalAuthorityType> = emptyList()
    ) {
        val entry = repository.findByIdentifier(identifier)
        assertEquals(identifier, names, entry?.authorities?.map { it.name }?.takeIf { it.isNotEmpty() } ?: entry?.authorityNames)
        assertEquals(identifier, federalState, entry?.federalState)
        assertEquals(identifier, type, entry?.type)
        if (type == PlateType.GEOGRAPHICAL) assertEquals(identifier, authorityTypes, entry?.authorities?.map { it.authorityType })
    }

    private fun uncertainEntries(): List<String> =
        (specialEntries.filter { it.source?.contains("kennzeichenwelt", ignoreCase = true) == true }.map { it.identifier } +
            diplomaticEntries.filter { it.source?.contains("wikipedia", ignoreCase = true) == true }.map { it.identifier })
            .distinct()
}
