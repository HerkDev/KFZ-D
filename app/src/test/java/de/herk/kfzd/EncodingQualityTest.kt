package de.herk.kfzd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class EncodingQualityTest {
    private val sourceRoot = File("src")
    private val suspiciousFragments = listOf(
        Char(0x00C3).toString(),
        Char(0x00C2).toString(),
        Char(0x00E2).toString() + Char(0x20AC).toString(),
        Char(0xFFFD).toString()
    )

    @Test
    fun visibleGermanStringsRemainValidUtf8() {
        val strings = File("src/main/res/values/strings.xml").readText(Charsets.UTF_8)
        val oe = Char(0x00F6)
        val ae = Char(0x00E4)
        val ue = Char(0x00FC)
        assertTrue(strings.contains("Landkreis / Beh" + oe + "rde"))
        val projectText = allTextFiles().joinToString("\n") { it.readText(Charsets.UTF_8) }
        assertTrue(projectText.contains("St" + ae + "dteregion"))
        assertTrue(projectText.contains("M" + ue + "nchen"))
        assertTrue(projectText.contains("Th" + ue + "ringen"))
        assertTrue(projectText.contains("W" + ue + "rzburg"))
        assertTrue(projectText.contains("HerkDev"))
    }

    @Test
    fun projectSourceContainsNoKnownMojibakeFragments() {
        val matches = allTextFiles().flatMap { file ->
            suspiciousFragments.filter { fragment -> file.readText(Charsets.UTF_8).contains(fragment) }
                .map { fragment -> file.path + ": " + fragment }
        }
        assertTrue("Suspicious encoding fragments found: " + matches, matches.isEmpty())
    }

    @Test
    fun visibleLabelAndEmptyResultUseCorrectCharacters() {
        val strings = File("src/main/res/values/strings.xml").readText(Charsets.UTF_8)
        val emDash = Char(0x2014).toString()
        assertTrue(strings.contains("Landkreis / Beh" + Char(0x00F6) + "rde"))
        val value = Regex("""<string name="no_result_value">(.*?)</string>""").find(strings)?.groupValues?.get(1)
        assertEquals(emDash, value)
        assertFalse(value!!.contains(Char(0x00E2).toString()))
    }

    private fun allTextFiles(): List<File> =
        sourceRoot.walkTopDown()
            .filter { it.isFile && it.extension.lowercase() in setOf("kt", "xml", "json", "svg", "properties", "gradle", "kts") }
            .toList()
}