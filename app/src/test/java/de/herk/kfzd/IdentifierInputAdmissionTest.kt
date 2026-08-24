package de.herk.kfzd

import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierInputAdmission
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class IdentifierInputAdmissionTest {
    private val geographical = load("german_plate_identifiers.json") { GeographicalPlateAssetLoader.parse(it) }
    private val special = load("german_special_identifiers.json") { SpecialPlateAssetLoader.parse(it) }
    private val diplomatic = load("german_diplomatic_identifiers.json") { DiplomaticPlateAssetLoader.parse(it) }
    private val authority = load("german_authority_series.json") { AuthoritySeriesPlateAssetLoader.parse(it) }
    private val allEntries = geographical + special + diplomatic + authority
    private val repository = InMemoryPlateRepository(allEntries, authority)
    private val matcher = IdentifierMatcher(repository)
    private val admission = IdentifierInputAdmission(matcher)

    @Test
    fun unknownFirstLetterStaysVisible() {
        var visibleInput = ""
        val candidate = "x".uppercase()
        if (admission.accepts(visibleInput, candidate)) {
            visibleInput = candidate
        }

        assertEquals("X", visibleInput)
    }

    @Test
    fun unknownIdentifierProducesNoMatch() {
        var visibleInput = ""
        val candidate = "x".uppercase()
        if (admission.accepts(visibleInput, candidate)) {
            visibleInput = candidate
        }

        assertEquals("X", visibleInput)
        assertNull(repository.findByIdentifier(visibleInput))
        assertFalse(matcher.match(visibleInput).isExact)
    }

    @Test
    fun validOneTwoAndThreeLetterIdentifiersStillResolve() {
        listOf(1, 2, 3).forEach { length ->
            val entry = geographical.first { it.identifier.length == length && it.identifier.all(Char::isLetter) }
            var current = ""
            entry.identifier.forEach { character ->
                val proposed = current + character
                assertTrue("${entry.identifier} rejected at $proposed", admission.accepts(current, proposed))
                current = proposed
            }
            assertEquals(entry, repository.findByIdentifier(current))
        }
    }

    @Test
    fun specialDiplomaticAndAuthorityIdentifiersStillUseExistingNormalization() {
        assertEquals(PlateType.TECHNICAL_RELIEF, repository.findByIdentifier("THW")?.type)
        assertEquals(PlateType.DIPLOMATIC_CORPS, repository.findByIdentifier("0-10")?.type)
        assertEquals(repository.findByIdentifier("DDQ"), repository.findByIdentifier("DD-Q"))
        assertEquals(repository.findByIdentifier("BD8"), repository.findByIdentifier("BD 8"))

        listOf("THW", "0-10", "DD-Q", "BD 8").forEach { input ->
            assertTrue(input, admission.accepts("", input))
        }
    }

    private fun <T> load(name: String, parser: (java.io.InputStream) -> List<T>): List<T> =
        File("src/main/assets/data/$name").inputStream().use(parser)
}
