package de.herk.kfzd.data.model

enum class PlateStatus {
    ACTIVE,
    REINTRODUCED,
    EXPIRING,
    HISTORICAL,
    CONVENTIONAL
}

enum class PlateType {
    GEOGRAPHICAL,
    FEDERAL_AUTHORITY,
    FEDERAL_FINANCE_ADMINISTRATION,
    FEDERAL_CONSTITUTIONAL_COURT,
    STATE_POLICE,
    MILITARY,
    TECHNICAL_RELIEF,
    GOVERNMENT,
    CONSTITUTIONAL_BODY,
    DIPLOMATIC_CORPS,
    INTERNATIONAL_ORGANISATION,
    DIPLOMATIC
}

enum class SourceType {
    PRIMARY,
    SECONDARY
}

enum class GeographicalAuthorityType {
    DISTRICT,
    INDEPENDENT_CITY,
    CITY,
    STATE_CAPITAL,
    CITY_STATE,
    REGION,
    CITY_REGION,
    REGIONAL_ASSOCIATION,
    SPECIAL_ASSOCIATION
}

data class GeographicalAuthority(
    val name: String,
    val authorityType: GeographicalAuthorityType
)

data class PlateEntry(
    val identifier: String,
    val authorityNames: List<String>,
    val federalState: String,
    val status: PlateStatus,
    val type: PlateType = PlateType.GEOGRAPHICAL,
    val authorities: List<GeographicalAuthority> = emptyList(),
    val source: String? = null,
    val sourceType: SourceType? = null,
    val notes: String? = null
)