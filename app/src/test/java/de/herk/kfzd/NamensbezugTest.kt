package de.herk.kfzd

import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class NamensbezugTest {
    private val entries = GeographicalPlateAssetLoader.parseWithNamensbezug(
        geographicalInput = File("src/main/assets/data/german_plate_identifiers.json").inputStream(),
        namensbezugInput = File("src/main/assets/data/kfzd_namensbezug_mapping.json").inputStream()
    )
    private val entriesByIdentifier = entries.associateBy { it.identifier }

    @Test
    fun allSuppliedMappingsAttachToExistingGeographicalIdentifiers() {
        val mappedEntries = entries.filter { it.namensbezug != null }

        assertEquals(377, mappedEntries.size)
        assertEquals(377, mappedEntries.map { it.identifier }.distinct().size)
        assertTrue(mappedEntries.all { !it.namensbezug.isNullOrBlank() })
    }

    @Test
    fun representativeMappingsUseTheVerifiedValues() {
        assertEquals("Feuchtwangen", entriesByIdentifier["FEU"]?.namensbezug)
        assertEquals("Detmold", entriesByIdentifier["DT"]?.namensbezug)
        assertEquals("Rinteln", entriesByIdentifier["RI"]?.namensbezug)
        assertEquals("Alfeld (Leine)", entriesByIdentifier["ALF"]?.namensbezug)
        assertEquals("Obervogtland", entriesByIdentifier["OVL"]?.namensbezug)
        assertEquals("Aue-Schwarzenberg", entriesByIdentifier["ASZ"]?.namensbezug)
        assertEquals("Torgau, Delitzsch, Oschatz", entriesByIdentifier["TDO"]?.namensbezug)
    }

    @Test
    fun approvedSpecialCasesDoNotExposeNamensbezug() {
        listOf("BK", "HZ", "L", "HGW", "HST", "HWI", "SHG").forEach { identifier ->
            assertNull(identifier, entriesByIdentifier[identifier]?.namensbezug)
        }
        assertNotNull(entriesByIdentifier["TDO"]?.namensbezug)
    }
}
