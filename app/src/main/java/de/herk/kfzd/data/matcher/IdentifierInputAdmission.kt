package de.herk.kfzd.data.matcher

/** The single admission rule used by the visible identifier input field. */
class IdentifierInputAdmission(private val matcher: IdentifierMatcher) {
    fun accepts(currentInput: String, proposedInput: String): Boolean {
        val current = IdentifierNormalizer.normalize(currentInput)
        val proposed = IdentifierNormalizer.normalize(proposedInput)
        return if (proposed.length <= current.length) {
            matcher.canAcceptInput(proposedInput)
        } else {
            matcher.canAcceptNextCharacter(currentInput, proposedInput)
        }
    }
}
