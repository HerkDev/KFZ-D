package de.herk.kfzd.data.repository

import android.content.Context
import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.matcher.IdentifierNormalizer
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.validation.PlateDatasetValidator

interface PlateRepository {
    fun findByIdentifier(identifier: String): PlateEntry?
    fun identifiers(): Set<String>
    fun entries(): List<PlateEntry>
}

class GeographicalPlateRepository(context: Context) : PlateRepository {
    private val authoritySeriesEntries = AuthoritySeriesPlateAssetLoader(context).load()
    private val delegate = InMemoryPlateRepository(
        GeographicalPlateAssetLoader(context).load() +
            SpecialPlateAssetLoader(context).load() +
            DiplomaticPlateAssetLoader(context).load() +
            authoritySeriesEntries,
        authoritySeriesEntries
    )

    override fun findByIdentifier(identifier: String): PlateEntry? = delegate.findByIdentifier(identifier)

    override fun identifiers(): Set<String> = delegate.identifiers()

    override fun entries(): List<PlateEntry> = delegate.entries()
}

class InMemoryPlateRepository(
    private val entries: List<PlateEntry>,
    private val authoritySeriesEntries: List<PlateEntry> = entries.filter { it.type == PlateType.STATE_POLICE }
) : PlateRepository {
    private val entriesByIdentifier = entries.associateBy { IdentifierNormalizer.normalize(it.identifier) }
    private val validation = PlateDatasetValidator.validate(
        entries = entries,
        authoritySeriesEntries = authoritySeriesEntries,
        authoritySeriesReachability = { identifier -> IdentifierMatcher(this).canAcceptInput(identifier) }
    )

    init {
        require(validation.duplicateIdentifiers.isEmpty()) { "Duplicate identifiers: ${validation.duplicateIdentifiers}" }
        require(validation.missingRequiredFields.isEmpty()) { "Missing required fields: ${validation.missingRequiredFields}" }
        require(validation.authoritySeriesMissingSources.isEmpty()) { "Authority series without source: ${validation.authoritySeriesMissingSources}" }
        require(validation.authoritySeriesMissingSourceTypes.isEmpty()) { "Authority series without source type: ${validation.authoritySeriesMissingSourceTypes}" }
        require(validation.authoritySeriesMissingAuthorityNames.isEmpty()) { "Authority series without authority name: ${validation.authoritySeriesMissingAuthorityNames}" }
        require(validation.authoritySeriesMissingFederalStates.isEmpty()) { "Authority series without federal state: ${validation.authoritySeriesMissingFederalStates}" }
        require(validation.invalidAuthoritySeriesTypes.isEmpty()) { "Invalid authority-series types: ${validation.invalidAuthoritySeriesTypes}" }
        require(validation.unreachableAuthoritySeriesIdentifiers.isEmpty()) { "Unreachable authority series: ${validation.unreachableAuthoritySeriesIdentifiers}" }
    }

    override fun findByIdentifier(identifier: String): PlateEntry? =
        entriesByIdentifier[IdentifierNormalizer.normalize(identifier)]

    override fun identifiers(): Set<String> = entriesByIdentifier.keys

    override fun entries(): List<PlateEntry> = entries
}