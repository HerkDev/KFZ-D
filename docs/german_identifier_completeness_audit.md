# Audit der Vollständigkeit geografischer deutscher Kfz-Unterscheidungszeichen

Stand der Recherche: 10.08.2026. Dieser Audit ersetzt die frühere, fehlerhaft auf eine alte BMVI-Broschüre gestützte Zählung. Die Broschüre ist als amtliche Zuordnungsquelle aus 2017 dokumentiert, aber **nicht** als aktuelle Vollständigkeitsliste bezeichnet.

## Methode und Normalisierung

- Vergleichsquelle: [Kennzeichenheld – Kfz-Kennzeichen-Liste](https://www.kennzeichenheld.de/magazin/kfz-kennzeichen-liste/), vollständig abgerufen am 10.08.2026.
- Extraktion: 788 Tabellenzeilen mit dem Feld Kennzeichen; je Zeichenfolge wurden Leerraum entfernt, Unicode NFC verwendet und Großschreibung angewendet. Mehrfachzeilen eines Kennzeichens wurden zu einem Identifier zusammengeführt.
- Produktionsvergleich: german_plate_identifiers.json gegen die drei übrigen Produktionsdateien. Für den Rohvergleich wurde der geografische Stand **vor** diesem Fix (691) zugrunde gelegt.
- Rechtlicher Rahmen: [§ 9 FZV](https://www.gesetze-im-internet.de/fzv_2023/__9.html) schreibt die Festlegung/Aufhebung von Unterscheidungszeichen für Verwaltungsbezirke durch das Verkehrsministerium und die Veröffentlichung im Bundesanzeiger vor. Die jeweils einschlägigen Bundesanzeiger-Festlegungen wurden daher gegenüber Kennzeichenheld priorisiert; die aktuelle FZV-Anlage 2 wurde zusätzlich zur Klassifikation der Sonderzeichen geprüft.

## Kennzeichenheld completeness comparison

| Kennzahl | Wert |
|---|---:|
| Kennzeichenheld-Tabellenzeilen | 788 |
| Eindeutige normalisierte Identifier auf Kennzeichenheld | 694 |
| Geografische KFZ-D-Identifier vor dem Fix | 691 |
| Rohvergleich: auf Kennzeichenheld, nicht geografisch in KFZ-D | 30 |
| Rohvergleich: geografisch in KFZ-D, nicht auf Kennzeichenheld | 27 |
| Rohvergleich: in beiden Mengen | 664 |

### Vollständige Rohdifferenz: auf Kennzeichenheld, in KFZ-D geografisch fehlend

$(AM BRB C CB ER FT G HEL HST IN J KE KF KO LD LSA LSN LU MA MM MVL NB NL NW P SC SHL SN SP WE -join ', ')

### Vollständige Rohdifferenz: geografisch in KFZ-D, auf Kennzeichenheld nicht enthalten

$(BE BEL BR DS DT ECK GAN HF HOG JB LE LK LUK MU MUC MÜL NEU NOM OTW SÄK SHG SOG STO WBG WOH WÜM ZS -join ', ')

### Vollständige Schnittmenge vor dem Fix

$(A AA AB ABG ABI AC AE AH AIB AIC AK ALF ALZ AN ANA ANG ANK AÖ AP APD ARN ART AS ASL ASZ AT AU AUR AW AZ AZE B BA BAD BAR BB BBG BC BCH BED BER BF BGD BGL BH BI BID BIN BIR BIT BIW BK BKS BL BLB BLK BM BN BNA BO BÖ BOG BOH BOR BOT BRA BRG BRK BRL BRV BS BSB BSK BT BTF BÜD BUL BÜR BÜS BÜZ BZ CA CAS CE CHA CLP CLZ CO COC COE CR CUX CW D DA DAH DAN DAU DBR DD DE DEG DEL DGF DH DI DIL DIN DIZ DKB DL DLG DM DN DO DON DU DUD DÜW DW DZ E EA EB EBE EBN EBS ED EE EF EG EH EI EIC EIL EIN EIS EL EM EMD EMS EN ERB ERH ERK ERZ ES ESB ESW EU EW F FB FD FDB FDS FEU FF FFB FG FI FKB FL FLÖ FN FO FOR FR FRG FRI FRW FS FTL FÜ FÜS FW FZ GA GAP GC GD GDB GE GEL GEO GER GF GG GHA GHC GI GK GL GLA GM GMN GN GNT GÖ GOA GOH GP GR GRA GRH GRI GRM GRZ GS GT GTH GÜ GUB GUN GV GVM GW GZ H HA HAB HAL HAM HAS HB HBN HBS HC HCH HD HDH HDL HE HEB HEF HEI HER HET HG HGN HGW HH HHM HI HIG HIP HK HL HM HMÜ HN HO HOH HOL HOM HOR HÖS HOT HP HR HRO HS HSK HU HV HVL HWI HX HY HZ IGB IK IL ILL IZ JE JL JÜL K KA KB KC KEH KEL KEM KG KH KI KIB KK KL KLE KLZ KM KN KÖN KÖT KÖZ KR KRU KS KT KU KÜN KUS KW KY KYF L LA LAN LAU LB LBS LBZ LC LDK LDS LEO LER LEV LF LG LH LI LIB LIF LIP LL LM LN LÖ LÖB LOS LP LR LRO LSZ LÜN LUP LWL M MAB MAI MAK MAL MB MC MD ME MED MEG MEI MEK MEL MER MET MG MGH MGN MH MHL MI MIL MK MKK ML MN MO MOD MOL MON MOS MQ MR MS MSE MSH MSP MST MTK MTL MÜ MÜB MÜR MW MY MYK MZ MZG N NAB NAI NAU ND NDH NE NEA NEB NEC NEN NES NEW NF NH NI NK NM NMB NMS NÖ NOH NOL NOR NP NR NT NU NVP NWM NY NZ OA OAL OB OBB OBG OC OCH OD OE OF OG OH OHA ÖHR OHV OHZ OK OL OP OPR OS OSL OVI OVL OVP OZ PA PAF PAN PAR PB PCH PE PEG PF PI PIR PL PLÖ PM PN PR PRÜ PS PW PZ QFT QLB R RA RC RD RDG RE REG REH REI RG RH RI RID RIE RL RM RN RO ROD ROF ROK ROL ROS ROT ROW RP RS RSL RT RU RÜD RÜG RV RW RZ S SAB SAD SAN SAW SB SBG SBK SCZ SDH SDL SDT SE SEB SEE SEF SEL SFB SFT SG SGH SHA SHK SI SIG SIM SK SL SLE SLF SLG SLK SLN SLS SLÜ SLZ SM SMÜ SO SOB SOK SÖM SON SPB SPN SR SRB SRO ST STA STB STD STE STL SU SUL SÜW SW SWA SY SZ SZB TBB TDO TE TET TF TG TIR TO TÖL TP TR TS TT TÜ TUT ÜB UE UEM UER UFF UH UL UM UN USI V VAI VB VEC VER VG VIB VIE VIT VK VOH VR VS W WA WAF WAK WAN WAR WAT WB WBS WDA WEL WEN WER WES WF WG WHV WI WIL WIS WIT WIZ WK WL WLG WM WMS WN WND WO WOB WOL WOR WOS WR WRN WS WSF WST WSW WT WTL WTM WÜ WUG WUN WUR WW WZ WZL Z ZE ZEL ZI ZIG ZP ZR ZW ZZ -join ', ')

### Unabhängige Prüfung jedes Rohkandidaten

ACTIVE bedeutet hier: amtlich festgelegt und im Audit keine spätere Aufhebungsbekanntmachung gefunden. INDEPENDENT_CITY bezeichnet die vorhandene KFZ-D-Modellkategorie für kreisfreie Städte.

| Identifier | unabhängig bestätigte Zuordnung | Bundesland | KFZ-D-Typ | Ergebnis | exakte Quelle |
|---|---|---|---|---|---|
| `AM` | Amberg | Bayern | `INDEPENDENT_CITY` | bestätigt | [BMVI-Verkehrsfibel, Stand August 2017](https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf) |
| `BRB` | Brandenburg an der Havel | Brandenburg | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `C` | Chemnitz | Sachsen | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `CB` | Cottbus | Brandenburg | `INDEPENDENT_CITY` | bestätigt | [BMVI-Verkehrsfibel, Stand August 2017](https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf) |
| `ER` | Erlangen | Bayern | `INDEPENDENT_CITY` | bestätigt | [BMVI-Verkehrsfibel, Stand August 2017](https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf) |
| `FT` | Frankenthal (Pfalz) | Rheinland-Pfalz | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `G` | Gera | Thüringen | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `HST` | Hansestadt Stralsund | Mecklenburg-Vorpommern | `INDEPENDENT_CITY` | bestätigt | [BMVI-Verkehrsfibel, Stand August 2017](https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf) |
| `IN` | Ingolstadt | Bayern | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `J` | Jena | Thüringen | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `KE` | Kelheim | Bayern | `DISTRICT` | bestätigt | [BMVI-Verkehrsfibel, Stand August 2017](https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf) |
| `KF` | Kaufbeuren | Bayern | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `KO` | Koblenz | Rheinland-Pfalz | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `LD` | Landau in der Pfalz | Rheinland-Pfalz | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `LU` | Ludwigshafen am Rhein | Rheinland-Pfalz | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `MA` | Mannheim | Baden-Württemberg | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `MM` | Memmingen | Bayern | `INDEPENDENT_CITY` | bestätigt | [BMVI-Verkehrsfibel, Stand August 2017](https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf) |
| `NB` | Neubrandenburg | Mecklenburg-Vorpommern | `INDEPENDENT_CITY` | bestätigt | [BMVI-Verkehrsfibel, Stand August 2017](https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf) |
| `NW` | Neustadt an der Weinstraße | Rheinland-Pfalz | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `P` | Potsdam | Brandenburg | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `SC` | Schwabach | Bayern | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `SHL` | Suhl | Thüringen | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `SN` | Schwerin | Mecklenburg-Vorpommern | `INDEPENDENT_CITY` | bestätigt | [BMVI-Verkehrsfibel, Stand August 2017](https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf) |
| `SP` | Speyer | Rheinland-Pfalz | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `WE` | Weimar | Thüringen | `INDEPENDENT_CITY` | bestätigt | [BAnz AT 15.04.2016 B4](https://www.bundesanzeiger.de/pub/publication/3vcfssiy7629N4Z9Iaf/content/3vcfssiy7629N4Z9Iaf/BAnz%20AT%2015.04.2016%20B4.pdf) |
| `HEL` | Hessen Landesregierung und Landtag | Hessen | `GOVERNMENT` | abgelehnt | [FZV Anlage 2](https://www.gesetze-im-internet.de/fzv_2023/anlage_2.html) |
| `LSA` | Sachsen-Anhalt Landesregierung, Landtag und Polizei | Sachsen-Anhalt | `GOVERNMENT` | abgelehnt | [FZV Anlage 2](https://www.gesetze-im-internet.de/fzv_2023/anlage_2.html) |
| `LSN` | Sachsen Landesregierung und Landtag | Sachsen | `GOVERNMENT` | abgelehnt | [FZV Anlage 2](https://www.gesetze-im-internet.de/fzv_2023/anlage_2.html) |
| `MVL` | Mecklenburg-Vorpommern Landesregierung (einschließlich Landespolizei) und Landtag | Mecklenburg-Vorpommern | `GOVERNMENT` | abgelehnt | [FZV Anlage 2](https://www.gesetze-im-internet.de/fzv_2023/anlage_2.html) |
| `NL` | Niedersachsen Landesregierung und Landtag | Niedersachsen | `GOVERNMENT` | abgelehnt | [FZV Anlage 2](https://www.gesetze-im-internet.de/fzv_2023/anlage_2.html) |

Die 25 bestätigten geografischen Identifier sind: $(AM BRB C CB ER FT G HST IN J KE KF KO LD LU MA MM NB NW P SC SHL SN SP WE -join ', ').

Die fünf abgelehnten Kandidaten sind keine geografischen Fehlstellen: HEL, LSA, LSN, MVL und NL sind bereits aktiv in german_special_identifiers.json als Landes-/Behördenkennzeichen modelliert. Sie werden deshalb weder doppelt noch als geografische Daten ergänzt.

### P und EF

- P = Potsdam: bestätigt. Die Bundesanzeiger-Festlegung führt P für „Potsdam, Stadt“; in KFZ-D ist es deshalb Brandenburg, INDEPENDENT_CITY, Quelle BAnz AT 15.04.2016 B4. Der Eintrag wurde ergänzt.
- EF = Erfurt: EF ist auf Kennzeichenheld **vorhanden**. Es liegt damit keine Kennzeichenheld-Auslassung vor; EF bleibt unverändert als gültiger geografischer Eintrag Erfurt / Thüringen bestehen.

### Website-Datenqualität und False Positives

- Kennzeichenheld vermischt fünf aktive Landes-/Behördenkennzeichen (HEL, LSA, LSN, MVL, NL) in seine geografisch wirkende Liste. Die aktuelle FZV-Anlage 2 weist sie als besondere Kennzeichen aus.
- Die Seite lässt 27 in KFZ-D vorhandene geografische Identifier aus, darunter aktuelle Ergänzungen wie BEL, JB, LE, LUK, MU und ZS; ihr Identifierbestand ist daher keine Quelle zum Entfernen von Produktionsdaten.
- Die Zuordnungsfelder der Website sind nicht verlässlich: beispielsweise erscheint AA dort mit Bundesland Bayern statt Baden-Württemberg. Daher wurden weder Behörden- noch Bundeslanddaten aus Kennzeichenheld übernommen.
- Die 788 Tabellenzeilen sind keine 788 Identifier: wiederholte Zuordnungszeilen ergeben nur 694 eindeutige Identifier.

## Produktionsänderung und Tests

german_plate_identifiers.json ergänzt die 25 bestätigten Identifier mit ACTIVE, spezifischem geografischem Behördentyp und der jeweils dokumentierten Primärquelle. Der geografische Bestand beträgt danach **716**. Sonder-, Diplomaten- und Behördenseriendaten wurden nicht geändert.

KennzeichenheldCompletenessAuditTest enthält die statische, aus dieser Prüfung abgeleitete geografische Referenzmenge von 689 Identifiern (694 Website-Identifier abzüglich der fünf Sonderkennzeichen) und prüft deren Erreichbarkeit in den Produktionsdaten. Zusätzlich prüft er:

- exakten Lookup, Zeichen-für-Zeichen-Eingabe sowie Potsdam/Brandenburg für P;
- jeden der 25 neu ergänzten Identifier einschließlich Behörde, Bundesland, Typ und Quelle;
- alle ein-, zwei- und dreibuchstabigen geografischen Identifier durch die reale Eingabezulassung;
- Dublettenfreiheit und Erreichbarkeit sämtlicher geografischer Produktions-Identifier;
- die fünf korrekt anders kategorisierten Website-False-Positives.

Keine F-Droid-Metadaten wurden geändert.