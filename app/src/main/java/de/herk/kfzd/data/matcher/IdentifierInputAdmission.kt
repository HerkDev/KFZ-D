package de.herk.kfzd.data.matcher

/** The single admission rule used by the visible identifier input field. */
class IdentifierInputAdmission(private val matcher: IdentifierMatcher) {
    fun accepts(currentInput: String, proposedInput: String): Boolean {
        // The known-identifier trie must not filter visible user input: an unknown
        // first letter is still valid input and must remain visible. Keep the value
        // safe for the existing normalizer and repository lookup by accepting only
        // identifier characters and separators used by special identifiers.
        return proposedInput.all { character ->
            character.isLetterOrDigit() || character.isWhitespace() || character == '-'
        }
    }
}
