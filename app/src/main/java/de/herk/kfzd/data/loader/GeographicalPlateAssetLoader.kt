package de.herk.kfzd.data.loader

import android.content.Context
import de.herk.kfzd.data.model.GeographicalAuthority
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.model.PlateStatus
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.model.SourceType
import java.io.InputStream

class GeographicalPlateAssetLoader(private val context: Context) {
    fun load(): List<PlateEntry> = context.assets.open(ASSET_PATH).use(::parse)

    companion object {
        const val ASSET_PATH = "data/german_plate_identifiers.json"

        fun parse(input: InputStream): List<PlateEntry> =
            PlateAssetJsonReaderFactory.parse(input, PlateType.GEOGRAPHICAL)
    }
}

internal object PlateAssetJsonReaderFactory {
    fun parse(input: InputStream, defaultType: PlateType): List<PlateEntry> {
        val json = input.bufferedReader(Charsets.UTF_8).use { it.readText().trimStart('\uFEFF') }
        return JsonPlateReader(json, defaultType).readEntries()
    }
}

private class JsonPlateReader(private val source: String, private val defaultType: PlateType) {
    private var position = 0

    fun readEntries(): List<PlateEntry> {
        val values = readValue() as? List<*> ?: error("Expected JSON array")
        return values.map { readEntry(it as? Map<*, *> ?: error("Expected JSON object")) }
    }

    private fun readEntry(values: Map<*, *>): PlateEntry {
        val identifier = values.string("identifier")
        val authorities = values.value("authorities")?.let { raw ->
            (raw as? List<*>)?.map { authority ->
                val authorityObject = authority as? Map<*, *> ?: error("Expected authority object")
                GeographicalAuthority(
                    name = authorityObject.string("name"),
                    authorityType = GeographicalAuthorityType.valueOf(authorityObject.string("authorityType"))
                )
            } ?: error("Expected authority array")
        } ?: emptyList()
        val authorityNames = when {
            values.value("authorityNames") != null -> values.stringList("authorityNames")
            values.value("countryOrOrganisationName") != null -> listOf(values.string("countryOrOrganisationName"))
            else -> authorities.map { it.name }
        }
        return PlateEntry(
            identifier = identifier,
            authorityNames = authorityNames,
            federalState = values.string("federalState"),
            status = PlateStatus.valueOf(values.string("status")),
            type = values.value("type")?.let { PlateType.valueOf(it as String) } ?: defaultType,
            authorities = authorities,
            source = values.value("source") as? String,
            sourceType = values.value("sourceType")?.let { SourceType.valueOf(it as String) },
            notes = values.value("notes") as? String
        )
    }

    private fun readValue(): Any? {
        skipWhitespace()
        return when (peek()) {
            '[' -> readArray()
            '{' -> readObject()
            '"' -> readString()
            else -> error("Expected JSON value at position $position")
        }
    }

    private fun readArray(): List<Any?> {
        expect('['); val values = mutableListOf<Any?>(); skipWhitespace()
        if (peek() == ']') { position++; return values }
        while (true) {
            values += readValue(); skipWhitespace()
            when (peek()) {
                ',' -> { position++; skipWhitespace() }
                ']' -> { position++; return values }
                else -> error("Expected ',' or ']' at position $position")
            }
        }
    }

    private fun readObject(): Map<String, Any?> {
        expect('{'); val values = linkedMapOf<String, Any?>(); skipWhitespace()
        if (peek() == '}') { position++; return values }
        while (true) {
            val key = readString(); skipWhitespace(); expect(':'); values[key] = readValue(); skipWhitespace()
            when (peek()) {
                ',' -> { position++; skipWhitespace() }
                '}' -> { position++; return values }
                else -> error("Expected ',' or '}' at position $position")
            }
        }
    }

    private fun readString(): String {
        expect('"'); val result = StringBuilder()
        while (position < source.length) when (val character = source[position++]) {
            '"' -> return result.toString()
            '\\' -> result.append(readEscape())
            else -> result.append(character)
        }
        error("Unterminated JSON string")
    }

    private fun readEscape(): Char = when (val escape = source[position++]) {
        '"', '\\', '/' -> escape
        'b' -> '\b'; 'f' -> '\u000C'; 'n' -> '\n'; 'r' -> '\r'; 't' -> '\t'
        'u' -> source.substring(position, position + 4).toInt(16).toChar().also { position += 4 }
        else -> error("Unsupported JSON escape at position $position")
    }

    private fun skipWhitespace() { while (position < source.length && source[position].isWhitespace()) position++ }
    private fun expect(expected: Char) { if (peek() != expected) error("Expected '$expected' at position $position"); position++ }
    private fun peek(): Char? = source.getOrNull(position)
    private fun Map<*, *>.value(key: String): Any? = this[key]
    private fun Map<*, *>.string(key: String): String = value(key) as? String ?: error("Missing string field '$key'")
    @Suppress("UNCHECKED_CAST")
    private fun Map<*, *>.stringList(key: String): List<String> = value(key) as? List<String> ?: error("Missing string list field '$key'")
}








