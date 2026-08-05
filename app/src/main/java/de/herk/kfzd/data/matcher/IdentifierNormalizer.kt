package de.herk.kfzd.data.matcher

object IdentifierNormalizer {
    private val whitespace = Regex("\\s+")
    private val ddqSeries = Regex("^DD[\\s-]*Q$")
    private val ddqSeparatorPrefix = Regex("^DD[\\s-]+$")
    private val bdNumberSeries = Regex("^BD\\s+(\\d+)$")

    fun normalize(input: String): String {
        val value = input.trim().uppercase().replace(whitespace, " ")
        return when {
            ddqSeries.matches(value) -> "DDQ"
            ddqSeparatorPrefix.matches(value) -> "DD"
            bdNumberSeries.matches(value) -> "BD" + bdNumberSeries.matchEntire(value)!!.groupValues[1]
            else -> value
        }
    }
}