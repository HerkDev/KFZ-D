package de.herk.kfzd.data.validation

import de.herk.kfzd.data.matcher.IdentifierNormalizer
import de.herk.kfzd.data.model.PlateEntry
import de.herk.kfzd.data.model.PlateStatus
import de.herk.kfzd.data.model.PlateType

object PlateDatasetValidator {
    fun validate(
        entries: List<PlateEntry>,
        authoritySeriesEntries: List<PlateEntry> = entries.filter { it.type == PlateType.STATE_POLICE },
        authoritySeriesReachability: (String) -> Boolean = { true }
    ): PlateDatasetReport {
        val duplicateIdentifiers = entries.groupingBy { IdentifierNormalizer.normalize(it.identifier) }.eachCount()
            .filterValues { it > 1 }.keys
        val missingRequiredFields = entries.filter { entry ->
            entry.identifier.isBlank() || entry.authorityNames.isEmpty() ||
                entry.authorityNames.any(String::isBlank) || entry.federalState.isBlank()
        }.map { it.identifier }.toSet()
        val missingAuthorityTypes = entries.filter { entry ->
            entry.type == PlateType.GEOGRAPHICAL &&
                (entry.authorities.isEmpty() || entry.authorities.any { it.name.isBlank() })
        }.map { it.identifier }.toSet()
        val mismatchedAuthorityCounts = entries.filter { entry ->
            entry.type == PlateType.GEOGRAPHICAL && entry.authorities.size != entry.authorityNames.size
        }.map { it.identifier }.toSet()
        val diplomaticEntries = entries.filter {
            it.type == PlateType.DIPLOMATIC_CORPS || it.type == PlateType.INTERNATIONAL_ORGANISATION
        }
        val authoritySeriesMissingSources = authoritySeriesEntries.filter { it.source.isNullOrBlank() }
            .map { it.identifier }.toSet()
        val authoritySeriesMissingSourceTypes = authoritySeriesEntries.filter { it.sourceType == null }
            .map { it.identifier }.toSet()
        val authoritySeriesMissingAuthorityNames = authoritySeriesEntries.filter {
            it.authorityNames.isEmpty() || it.authorityNames.any(String::isBlank)
        }.map { it.identifier }.toSet()
        val authoritySeriesMissingFederalStates = authoritySeriesEntries.filter { it.federalState.isBlank() }
            .map { it.identifier }.toSet()
        val validAuthoritySeriesTypes = setOf(
            PlateType.STATE_POLICE,
            PlateType.FEDERAL_FINANCE_ADMINISTRATION,
            PlateType.FEDERAL_CONSTITUTIONAL_COURT
        )
        val invalidAuthoritySeriesTypes = authoritySeriesEntries.filter { it.type !in validAuthoritySeriesTypes }
            .map { it.identifier }.toSet()
        val unreachableAuthoritySeriesIdentifiers = authoritySeriesEntries.filter {
            !authoritySeriesReachability(it.identifier)
        }.map { it.identifier }.toSet()

        return PlateDatasetReport(
            totalCount = entries.size,
            countByStatus = PlateStatus.entries.associateWith { status -> entries.count { it.status == status } },
            countByFederalState = entries.groupingBy { it.federalState }.eachCount(),
            duplicateIdentifiers = duplicateIdentifiers,
            missingRequiredFields = missingRequiredFields,
            missingAuthorityTypes = missingAuthorityTypes,
            mismatchedAuthorityCounts = mismatchedAuthorityCounts,
            diplomaticIdentifierCount = diplomaticEntries.size,
            diplomaticCountryCodeCount = diplomaticEntries.count { it.type == PlateType.DIPLOMATIC_CORPS },
            diplomaticOrganisationCodeCount = diplomaticEntries.count { it.type == PlateType.INTERNATIONAL_ORGANISATION },
            diplomaticMissingSources = diplomaticEntries.filter { it.source.isNullOrBlank() }.map { it.identifier }.toSet(),
            authoritySeriesMissingSources = authoritySeriesMissingSources,
            authoritySeriesMissingSourceTypes = authoritySeriesMissingSourceTypes,
            authoritySeriesMissingAuthorityNames = authoritySeriesMissingAuthorityNames,
            authoritySeriesMissingFederalStates = authoritySeriesMissingFederalStates,
            invalidAuthoritySeriesTypes = invalidAuthoritySeriesTypes,
            unreachableAuthoritySeriesIdentifiers = unreachableAuthoritySeriesIdentifiers
        )
    }
}

data class PlateDatasetReport(
    val totalCount: Int,
    val countByStatus: Map<PlateStatus, Int>,
    val countByFederalState: Map<String, Int>,
    val duplicateIdentifiers: Set<String>,
    val missingRequiredFields: Set<String>,
    val missingAuthorityTypes: Set<String>,
    val mismatchedAuthorityCounts: Set<String>,
    val diplomaticIdentifierCount: Int,
    val diplomaticCountryCodeCount: Int,
    val diplomaticOrganisationCodeCount: Int,
    val diplomaticMissingSources: Set<String>,
    val authoritySeriesMissingSources: Set<String>,
    val authoritySeriesMissingSourceTypes: Set<String>,
    val authoritySeriesMissingAuthorityNames: Set<String>,
    val authoritySeriesMissingFederalStates: Set<String>,
    val invalidAuthoritySeriesTypes: Set<String>,
    val unreachableAuthoritySeriesIdentifiers: Set<String>
)