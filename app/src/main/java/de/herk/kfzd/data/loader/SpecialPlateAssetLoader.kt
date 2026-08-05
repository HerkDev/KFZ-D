package de.herk.kfzd.data.loader

import android.content.Context
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.model.SourceType
import java.io.InputStream

class SpecialPlateAssetLoader(private val context: Context) {
    fun load(): List<PlateEntry> = context.assets.open(ASSET_PATH).use { input ->
        val json = input.bufferedReader(Charsets.UTF_8).use { it.readText().trimStart('\uFEFF') }
        SpecialPlateJsonReader(json).readEntries()
    }

    companion object {
        const val ASSET_PATH = "data/german_special_identifiers.json"

        fun parse(input: InputStream): List<PlateEntry> {
            val json = input.bufferedReader(Charsets.UTF_8).use { it.readText().trimStart('\uFEFF') }
            return SpecialPlateJsonReader(json).readEntries()
        }    }
}

private class SpecialPlateJsonReader(private val source: String) {
    private var position = 0

    fun readEntries(): List<PlateEntry> {
        skipWhitespace(); expect('[')
        val entries = mutableListOf<PlateEntry>(); skipWhitespace()
        if (peek() == ']') { position++; return entries }
        while (true) {
            entries += readEntry(); skipWhitespace()
            when (peek()) {
                ',' -> position++
                ']' -> { position++; return entries }
                else -> error("Expected ',' or ']' at position $position")
            }
        }
    }

    private fun readEntry(): PlateEntry {
        skipWhitespace(); expect('{'); val values = mutableMapOf<String, Any>(); skipWhitespace()
        while (peek() != '}') {
            val key = readString(); skipWhitespace(); expect(':'); skipWhitespace()
            values[key] = if (key == "authorityNames") readStringArray() else readString()
            skipWhitespace()
            if (peek() == ',') { position++; skipWhitespace() } else break
        }
        expect('}')
        return PlateEntry(
            identifier = values.requiredString("identifier"),
            authorityNames = values.requiredStringList("authorityNames"),
            federalState = values.requiredString("federalState"),
            status = values.requiredString("status").let { de.herk.kfzd.data.model.PlateStatus.valueOf(it) },
            type = PlateType.valueOf(values.requiredString("type")),
            source = values["source"] as? String,
            sourceType = (values["sourceType"] as? String)?.let(SourceType::valueOf),
            notes = values["notes"] as? String
        )
    }

    private fun readStringArray(): List<String> {
        expect('['); val values = mutableListOf<String>(); skipWhitespace()
        if (peek() == ']') { position++; return values }
        while (true) {
            values += readString(); skipWhitespace()
            when (peek()) {
                ',' -> { position++; skipWhitespace() }
                ']' -> { position++; return values }
                else -> error("Expected ',' or ']' at position $position")
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
    private fun Map<String, Any>.requiredString(key: String): String = this[key] as? String ?: error("Missing string field '$key'")
    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any>.requiredStringList(key: String): List<String> = this[key] as? List<String> ?: error("Missing string list field '$key'")
}




