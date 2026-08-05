package de.herk.kfzd

import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BuLookupTest {
    private val entries = GeographicalPlateAssetLoader.parse(
        File("src/main/assets/data/german_plate_identifiers.json").inputStream()
    ) + SpecialPlateAssetLoader.parse(
        File("src/main/assets/data/german_special_identifiers.json").inputStream()
    ) + DiplomaticPlateAssetLoader.parse(
        File("src/main/assets/data/german_diplomatic_identifiers.json").inputStream()
    ) + AuthoritySeriesPlateAssetLoader.parse(
        File("src/main/assets/data/german_authority_series.json").inputStream()
    )
    private val repository = InMemoryPlateRepository(entries)
    private val matcher = IdentifierMatcher(repository)

    @Test
    fun bIsExactAndExtendable() {
        val match = matcher.match("B")
        assertTrue(match.isExact)
        assertTrue(match.hasLongerIdentifiers)
        assertNotNull(repository.findByIdentifier("B"))
    }

    @Test
    fun buIsAValidPrefixForBulButNotAnActiveExactIdentifier() {
        val match = matcher.match("BU")
        assertFalse(match.isExact)
        assertTrue(match.hasLongerIdentifiers)
        assertFalse(match.isTerminal)
        assertTrue(matcher.canAcceptNextCharacter("B", "BU"))
        assertTrue(matcher.canAcceptNextCharacter("BU", "BUL"))
        assertEquals(null, repository.findByIdentifier("BU"))
    }

    @Test
    fun bulResolvesToItsCurrentAuthorities() {
        val entry = repository.findByIdentifier("BUL")
        assertNotNull(entry)
        assertEquals(listOf("Amberg-Sulzbach", "Schwandorf"), entry!!.authorities.map { it.name })
        assertEquals("Bayern", entry.federalState)
        assertTrue(entry.authorities.all { it.authorityType == GeographicalAuthorityType.DISTRICT })
        assertEquals(PlateType.GEOGRAPHICAL, entry.type)
    }

    @Test
    fun invalidContinuationAfterTerminalIdentifierIsRejected() {
        assertTrue(matcher.match("BUL").isTerminal)
        assertFalse(matcher.canAcceptNextCharacter("BUL", "BULX"))
    }

    @Test
    fun everyExactTrieIdentifierResolvesThroughTheRepository() {
        repository.identifiers().forEach { identifier ->
            val match = matcher.match(identifier)
            assertTrue("$identifier is not exact in the matcher", match.isExact)
            assertNotNull("$identifier has no exact repository result", repository.findByIdentifier(identifier))
        }
    }

    @Test
    fun noBrokenVisibleBuValueIsPresent() {
        val entry = repository.findByIdentifier("BUL")
        val visibleValues = entry!!.authorities.map { it.name } + entry.federalState
        assertTrue(visibleValues.none { it.contains("ehem.") || it.contains("historisch", ignoreCase = true) })
        assertTrue(visibleValues.none { it.contains(Char(0x00C3)) || it.contains(Char(0x00C2)) || it.contains(Char(0xFFFD)) })
    }
}