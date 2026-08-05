package de.herk.kfzd.data.matcher

import de.herk.kfzd.data.repository.PlateRepository

class IdentifierMatcher(repository: PlateRepository) {
    private val root = TrieNode()

    init {
        repository.identifiers().forEach { identifier ->
            require(repository.findByIdentifier(identifier) != null) {
                "Identifier has no repository result: $identifier"
            }
            addIdentifier(identifier)
        }
    }

    fun match(input: String): IdentifierMatch {
        var node = root
        for (character in IdentifierNormalizer.normalize(input)) {
            node = node.children[character] ?: return IdentifierMatch.invalid()
        }

        return IdentifierMatch(
            isExact = node.isIdentifier,
            hasLongerIdentifiers = node.children.isNotEmpty()
        )
    }

    fun canAcceptInput(input: String): Boolean {
        if (input.isEmpty()) return true
        val result = match(input)
        return result.isExact || result.hasLongerIdentifiers
    }

    fun canAcceptNextCharacter(currentInput: String, proposedInput: String): Boolean =
        match(currentInput).hasLongerIdentifiers && canAcceptInput(proposedInput)

    private fun addIdentifier(identifier: String) {
        var node = root
        for (character in IdentifierNormalizer.normalize(identifier)) {
            node = node.children.getOrPut(character) { TrieNode() }
        }
        node.isIdentifier = true
    }

    private class TrieNode {
        val children = HashMap<Char, TrieNode>()
        var isIdentifier = false
    }
}

data class IdentifierMatch(
    val isExact: Boolean,
    val hasLongerIdentifiers: Boolean
) {
    val isValidPrefix: Boolean
        get() = isExact || hasLongerIdentifiers

    val isTerminal: Boolean
        get() = isExact && !hasLongerIdentifiers

    companion object {
        fun invalid() = IdentifierMatch(
            isExact = false,
            hasLongerIdentifiers = false
        )
    }
}