package de.herk.kfzd

import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.matcher.IdentifierNormalizer
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TwoThreeLetterInputAuditTest {
    private val geographical = load("german_plate_identifiers.json") { GeographicalPlateAssetLoader.parse(it) }
    private val special = load("german_special_identifiers.json") { SpecialPlateAssetLoader.parse(it) }
    private val diplomatic = load("german_diplomatic_identifiers.json") { DiplomaticPlateAssetLoader.parse(it) }
    private val authority = load("german_authority_series.json") { AuthoritySeriesPlateAssetLoader.parse(it) }
    private val allEntries = geographical + special + diplomatic + authority
    private val repository = InMemoryPlateRepository(allEntries, authority)
    private val matcher = IdentifierMatcher(repository)

    @Test
    fun everyTwoAndThreeLetterProductionIdentifierIsEnterableCharacterByCharacter() {
        val targets = allEntries.filter { it.identifier.length in 2..3 && it.identifier.all(Char::isLetter) }
        val failures = targets.flatMap { entry -> audit(entry).failures }
        println("Two-letter identifiers tested: ${targets.count { it.identifier.length == 2 }}")
        println("Three-letter identifiers tested: ${targets.count { it.identifier.length == 3 }}")
        println("Blocked identifiers: ${failures.joinToString()}")
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    @Test
    fun efCharacterByCharacterAndTerminalHandlingAreExplicit() {
        assertEquals("Erfurt", repository.findByIdentifier("EF")?.authorityNames?.single())
        assertTrue(matcher.canAcceptNextCharacter("E", "EF"))
        assertTrue(matcher.canAcceptInput("EF"))
        val terminal = allEntries.first { matcher.match(it.identifier).isTerminal }
        assertTrue(matcher.canAcceptInput(terminal.identifier))
        assertTrue(!matcher.canAcceptInput(terminal.identifier + "X"))
        val extendable = allEntries.first { matcher.match(it.identifier).isExact && matcher.match(it.identifier).hasLongerIdentifiers }
        assertTrue(matcher.canAcceptNextCharacter(extendable.identifier.dropLast(1), extendable.identifier))
    }

    @Test
    fun specialNormalizationStillUsesInputAdmissionPath() {
        assertEquals("DDQ", IdentifierNormalizer.normalize("DD-Q"))
        assertTrue(matcher.canAcceptInput("DD-Q"))
        assertEquals(repository.findByIdentifier("DDQ"), repository.findByIdentifier("DD-Q"))
    }

    private fun audit(entry: PlateEntry): Audit {
        var current = ""
        val failures = mutableListOf<String>()
        entry.identifier.forEachIndexed { index, character ->
            val proposed = current + character
            val accepted = if (proposed.length <= current.length) matcher.canAcceptInput(proposed)
            else matcher.canAcceptNextCharacter(current, proposed)
            if (!accepted) failures += "${entry.identifier} position ${index + 1} dataset=${dataset(entry)} category=${entry.type} expected=${entry.authorityNames.joinToString("/")}"
            current = proposed
        }
        if (repository.findByIdentifier(current) != entry) failures += "${entry.identifier} final resolution dataset=${dataset(entry)} category=${entry.type} expected=${entry.authorityNames.joinToString("/")}"
        return Audit(failures)
    }

    private fun dataset(entry: PlateEntry): String = when {
        entry in geographical -> "german_plate_identifiers.json"
        entry in special -> "german_special_identifiers.json"
        entry in diplomatic -> "german_diplomatic_identifiers.json"
        else -> "german_authority_series.json"
    }

    private data class Audit(val failures: List<String>)
    private fun <T> load(name: String, parser: (java.io.InputStream) -> List<T>): List<T> = File("src/main/assets/data/$name").inputStream().use(parser)
}
