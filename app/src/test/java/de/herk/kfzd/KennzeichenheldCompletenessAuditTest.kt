package de.herk.kfzd

import de.herk.kfzd.data.loader.AuthoritySeriesPlateAssetLoader
import de.herk.kfzd.data.loader.DiplomaticPlateAssetLoader
import de.herk.kfzd.data.loader.GeographicalPlateAssetLoader
import de.herk.kfzd.data.loader.SpecialPlateAssetLoader
import de.herk.kfzd.data.matcher.IdentifierMatcher
import de.herk.kfzd.data.model.GeographicalAuthorityType
import de.herk.kfzd.data.model.PlateType
import de.herk.kfzd.data.repository.InMemoryPlateRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class KennzeichenheldCompletenessAuditTest {
    private val geographical = load("german_plate_identifiers.json") { GeographicalPlateAssetLoader.parse(it) }
    private val special = load("german_special_identifiers.json") { SpecialPlateAssetLoader.parse(it) }
    private val diplomatic = load("german_diplomatic_identifiers.json") { DiplomaticPlateAssetLoader.parse(it) }
    private val authoritySeries = load("german_authority_series.json") { AuthoritySeriesPlateAssetLoader.parse(it) }
    private val allEntries = geographical + special + diplomatic + authoritySeries
    private val repository = InMemoryPlateRepository(allEntries, authoritySeries)
    private val matcher = IdentifierMatcher(repository)

    @Test
    fun verifiedKennzeichenheldGeographicalReferenceIsComplete() {
        assertEquals(689, verifiedKennzeichenheldGeographicalReference.size)
        assertTrue(
            "Missing verified Kennzeichenheld identifiers: ${(verifiedKennzeichenheldGeographicalReference - geographical.map { it.identifier }.toSet()).sorted()}",
            verifiedKennzeichenheldGeographicalReference.all { repository.findByIdentifier(it)?.type == PlateType.GEOGRAPHICAL }
        )
        assertTrue("EF must remain in the verified reference set", "EF" in verifiedKennzeichenheldGeographicalReference)
    }

    @Test
    fun confirmedCandidatesResolveToIndependentlyVerifiedAuthorities() {
        confirmedCandidates.forEach { (identifier, expected) ->
            val entry = repository.findByIdentifier(identifier)
            assertEquals(identifier, expected.name, entry?.authorityNames?.single())
            assertEquals(identifier, expected.federalState, entry?.federalState)
            assertEquals(identifier, expected.authorityType, entry?.authorities?.single()?.authorityType)
            assertEquals(identifier, PlateType.GEOGRAPHICAL, entry?.type)
            assertEquals(identifier, "PRIMARY", entry?.sourceType?.name)
            assertTrue(identifier, matcher.canAcceptInput(identifier))
            var current = ""
            identifier.forEach { character ->
                val proposed = current + character
                assertTrue("$identifier rejected at $proposed", matcher.canAcceptNextCharacter(current, proposed))
                current = proposed
            }
        }
    }

    @Test
    fun pExactLookupAndCharacterInputResolveToPotsdamBrandenburg() {
        val entry = repository.findByIdentifier("P")
        assertEquals("Potsdam", entry?.authorityNames?.single())
        assertEquals("Brandenburg", entry?.federalState)
        assertEquals(GeographicalAuthorityType.INDEPENDENT_CITY, entry?.authorities?.single()?.authorityType)
        assertTrue(matcher.canAcceptNextCharacter("", "P"))
        assertTrue(matcher.canAcceptInput("P"))
    }

    @Test
    fun websiteFalsePositivesRemainSpecialIdentifiersInsteadOfGeographicalEntries() {
        setOf("HEL", "LSA", "LSN", "MVL", "NL").forEach { identifier ->
            assertNull(identifier, geographical.firstOrNull { it.identifier == identifier })
            assertEquals(identifier, PlateType.GOVERNMENT, repository.findByIdentifier(identifier)?.type)
        }
    }

    @Test
    fun everyProductionGeographicalIdentifierIsReachableThroughRealAdmissionLogic() {
        assertEquals(716, geographical.size)
        assertEquals(geographical.size, geographical.map { it.identifier }.distinct().size)
        geographical.forEach { entry ->
            var current = ""
            entry.identifier.forEach { character ->
                val proposed = current + character
                assertTrue("${entry.identifier} rejected at $proposed", matcher.canAcceptNextCharacter(current, proposed))
                current = proposed
            }
            assertTrue("${entry.identifier} exact input rejected", matcher.canAcceptInput(entry.identifier))
            assertEquals(entry.identifier, entry, repository.findByIdentifier(entry.identifier))
        }
        assertTrue(geographical.any { it.identifier.length == 1 })
        assertTrue(geographical.any { it.identifier.length == 2 })
        assertTrue(geographical.any { it.identifier.length == 3 })
    }

    private fun <T> load(name: String, parser: (java.io.InputStream) -> List<T>): List<T> =
        File("src/main/assets/data/$name").inputStream().use(parser)

    private data class ExpectedAuthority(
        val name: String,
        val federalState: String,
        val authorityType: GeographicalAuthorityType
    )

    private companion object {
        val confirmedCandidates = mapOf(
            "AM" to ExpectedAuthority("Amberg", "Bayern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "BRB" to ExpectedAuthority("Brandenburg an der Havel", "Brandenburg", GeographicalAuthorityType.INDEPENDENT_CITY),
            "C" to ExpectedAuthority("Chemnitz", "Sachsen", GeographicalAuthorityType.INDEPENDENT_CITY),
            "CB" to ExpectedAuthority("Cottbus", "Brandenburg", GeographicalAuthorityType.INDEPENDENT_CITY),
            "ER" to ExpectedAuthority("Erlangen", "Bayern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "FT" to ExpectedAuthority("Frankenthal (Pfalz)", "Rheinland-Pfalz", GeographicalAuthorityType.INDEPENDENT_CITY),
            "G" to ExpectedAuthority("Gera", "Thüringen", GeographicalAuthorityType.INDEPENDENT_CITY),
            "HST" to ExpectedAuthority("Hansestadt Stralsund", "Mecklenburg-Vorpommern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "IN" to ExpectedAuthority("Ingolstadt", "Bayern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "J" to ExpectedAuthority("Jena", "Thüringen", GeographicalAuthorityType.INDEPENDENT_CITY),
            "KE" to ExpectedAuthority("Kelheim", "Bayern", GeographicalAuthorityType.DISTRICT),
            "KF" to ExpectedAuthority("Kaufbeuren", "Bayern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "KO" to ExpectedAuthority("Koblenz", "Rheinland-Pfalz", GeographicalAuthorityType.INDEPENDENT_CITY),
            "LD" to ExpectedAuthority("Landau in der Pfalz", "Rheinland-Pfalz", GeographicalAuthorityType.INDEPENDENT_CITY),
            "LU" to ExpectedAuthority("Ludwigshafen am Rhein", "Rheinland-Pfalz", GeographicalAuthorityType.INDEPENDENT_CITY),
            "MA" to ExpectedAuthority("Mannheim", "Baden-Württemberg", GeographicalAuthorityType.INDEPENDENT_CITY),
            "MM" to ExpectedAuthority("Memmingen", "Bayern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "NB" to ExpectedAuthority("Neubrandenburg", "Mecklenburg-Vorpommern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "NW" to ExpectedAuthority("Neustadt an der Weinstraße", "Rheinland-Pfalz", GeographicalAuthorityType.INDEPENDENT_CITY),
            "P" to ExpectedAuthority("Potsdam", "Brandenburg", GeographicalAuthorityType.INDEPENDENT_CITY),
            "SC" to ExpectedAuthority("Schwabach", "Bayern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "SHL" to ExpectedAuthority("Suhl", "Thüringen", GeographicalAuthorityType.INDEPENDENT_CITY),
            "SN" to ExpectedAuthority("Schwerin", "Mecklenburg-Vorpommern", GeographicalAuthorityType.INDEPENDENT_CITY),
            "SP" to ExpectedAuthority("Speyer", "Rheinland-Pfalz", GeographicalAuthorityType.INDEPENDENT_CITY),
            "WE" to ExpectedAuthority("Weimar", "Thüringen", GeographicalAuthorityType.INDEPENDENT_CITY)
        )

        val verifiedKennzeichenheldGeographicalReference = """
            A AA AB ABG ABI AC AE AH AIB AIC AK ALF ALZ AM AN ANA ANG ANK AÖ AP APD ARN ART AS ASL ASZ AT AU AUR AW AZ AZE B BA BAD BAR BB BBG BC BCH BED BER BF BGD BGL BH BI BID BIN BIR BIT BIW BK BKS BL BLB BLK BM BN BNA BO BÖ BOG BOH BOR BOT BRA BRB BRG BRK BRL BRV BS BSB BSK BT BTF BÜD BUL BÜR BÜS BÜZ BZ C CA CAS CB CE CHA CLP CLZ CO COC COE CR CUX CW D DA DAH DAN DAU DBR DD DE DEG DEL DGF DH DI DIL DIN DIZ DKB DL DLG DM DN DO DON DU DUD DÜW DW DZ E EA EB EBE EBN EBS ED EE EF EG EH EI EIC EIL EIN EIS EL EM EMD EMS EN ER ERB ERH ERK ERZ ES ESB ESW EU EW F FB FD FDB FDS FEU FF FFB FG FI FKB FL FLÖ FN FO FOR FR FRG FRI FRW FS FT FTL FÜ FÜS FW FZ G GA GAP GC GD GDB GE GEL GEO GER GF GG GHA GHC GI GK GL GLA GM GMN GN GNT GÖ GOA GOH GP GR GRA GRH GRI GRM GRZ GS GT GTH GÜ GUB GUN GV GVM GW GZ H HA HAB HAL HAM HAS HB HBN HBS HC HCH HD HDH HDL HE HEB HEF HEI HER HET HG HGN HGW HH HHM HI HIG HIP HK HL HM HMÜ HN HO HOH HOL HOM HOR HÖS HOT HP HR HRO HS HSK HST HU HV HVL HWI HX HY HZ IGB IK IL ILL IN IZ J JE JL JÜL K KA KB KC KE KEH KEL KEM KF KG KH KI KIB KK KL KLE KLZ KM KN KO KÖN KÖT KÖZ KR KRU KS KT KU KÜN KUS KW KY KYF L LA LAN LAU LB LBS LBZ LC LD LDK LDS LEO LER LEV LF LG LH LI LIB LIF LIP LL LM LN LÖ LÖB LOS LP LR LRO LSZ LU LÜN LUP LWL M MA MAB MAI MAK MAL MB MC MD ME MED MEG MEI MEK MEL MER MET MG MGH MGN MH MHL MI MIL MK MKK ML MM MN MO MOD MOL MON MOS MQ MR MS MSE MSH MSP MST MTK MTL MÜ MÜB MÜR MW MY MYK MZ MZG N NAB NAI NAU NB ND NDH NE NEA NEB NEC NEN NES NEW NF NH NI NK NM NMB NMS NÖ NOH NOL NOR NP NR NT NU NVP NW NWM NY NZ OA OAL OB OBB OBG OC OCH OD OE OF OG OH OHA ÖHR OHV OHZ OK OL OP OPR OS OSL OVI OVL OVP OZ P PA PAF PAN PAR PB PCH PE PEG PF PI PIR PL PLÖ PM PN PR PRÜ PS PW PZ QFT QLB R RA RC RD RDG RE REG REH REI RG RH RI RID RIE RL RM RN RO ROD ROF ROK ROL ROS ROT ROW RP RS RSL RT RU RÜD RÜG RV RW RZ S SAB SAD SAN SAW SB SBG SBK SC SCZ SDH SDL SDT SE SEB SEE SEF SEL SFB SFT SG SGH SHA SHK SHL SI SIG SIM SK SL SLE SLF SLG SLK SLN SLS SLÜ SLZ SM SMÜ SN SO SOB SOK SÖM SON SP SPB SPN SR SRB SRO ST STA STB STD STE STL SU SUL SÜW SW SWA SY SZ SZB TBB TDO TE TET TF TG TIR TO TÖL TP TR TS TT TÜ TUT ÜB UE UEM UER UFF UH UL UM UN USI V VAI VB VEC VER VG VIB VIE VIT VK VOH VR VS W WA WAF WAK WAN WAR WAT WB WBS WDA WE WEL WEN WER WES WF WG WHV WI WIL WIS WIT WIZ WK WL WLG WM WMS WN WND WO WOB WOL WOR WOS WR WRN WS WSF WST WSW WT WTL WTM WÜ WUG WUN WUR WW WZ WZL Z ZE ZEL ZI ZIG ZP ZR ZW ZZ
        """.trimIndent().split(Regex("\\s+")).toSet()
    }
}