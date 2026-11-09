package de.herk.kfzd

import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class FinalAuditCorrectionsTest {
    private val repository = InMemoryPlateRepository(
        GeographicalPlateAssetLoader.parse(
            File("src/main/assets/data/german_plate_identifiers.json").inputStream()
        )
    )

    @Test
    fun finalAuditCorrectionsPreserveTheExistingAuthorityStructure() {
        assertEquals(listOf("Kempten"), repository.findByIdentifier("KE")?.authorities?.map { it.name })
        assertEquals(
            listOf(GeographicalAuthorityType.INDEPENDENT_CITY),
            repository.findByIdentifier("KE")?.authorities?.map { it.authorityType }
        )
        assertEquals("Bayern", repository.findByIdentifier("KE")?.federalState)

        val bk = repository.findByIdentifier("BK")
        assertEquals("Baden-Württemberg / Sachsen-Anhalt", bk?.federalState)
        assertEquals(
            listOf("Börde", "Rems-Murr-Kreis", "Schwäbisch Hall"),
            bk?.authorities?.map { it.name }
        )
        assertEquals(
            listOf(
                GeographicalAuthorityType.DISTRICT,
                GeographicalAuthorityType.DISTRICT,
                GeographicalAuthorityType.DISTRICT
            ),
            bk?.authorities?.map { it.authorityType }
        )

        assertEquals(
            listOf("Landshut", "Straubing-Bogen"),
            repository.findByIdentifier("MAL")?.authorities?.map { it.name }
        )
        assertEquals(
            listOf("Kelheim", "Neumarkt i. d. Opf."),
            repository.findByIdentifier("PAR")?.authorities?.map { it.name }
        )
        assertEquals(
            listOf("Ortenaukreis", "Rastatt"),
            repository.findByIdentifier("BH")?.authorities?.map { it.name }
        )
    }
}
