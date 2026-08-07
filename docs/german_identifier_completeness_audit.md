# Audit der Vollständigkeit geografischer deutscher Kfz-Unterscheidungszeichen

Stand der Recherche: 06.08.2026. Die Produktionsdaten wurden nach Abschluss der Vergleichsphase korrigiert: `EF` wurde ergänzt und `MU` berichtigt.

## Quellen und Methode

Als amtliche Referenz wurden die aktuelle Kennzeichenübersicht des Bundesministeriums für Verkehr sowie die einschlägigen Bekanntmachungen im Bundesanzeiger verwendet. Die BMV-Übersicht führt insbesondere `EF` für Erfurt und `LK` für Lübbecke; die FZV definiert das Unterscheidungszeichen als ein bis drei Buchstaben für den Verwaltungsbezirk. Ergänzende Primärquellen sind die jeweiligen Bundesanzeiger-Bekanntmachungen für neue oder wiedereingeführte Zeichen.

Relevante Primärquellen:

- BMV, „KFZ-KENNZEICHEN IN DEUTSCHLAND“, veröffentlichte Übersicht, https://www.bmv.de/blaetterkatalog/catalogs/122810/pdf/complete_print.pdf (enthält `EF = Erfurt`, `LK = Lübbecke` und die geografische Referenzliste).
- BAnz AT 07.01.2025 B2, Bescheid 16.12.2024: `JB`, `LUK`, `ZS = Teltow-Fläming`, https://www.bundesanzeiger.de/pub/publication/vWAf4UiEAyjGqQYrBGk.
- BAnz AT 08.07.2025 B3, Bescheid 11.06.2025: `MU = Landkreis München`, https://www.bundesanzeiger.de/pub/publication/CmGMXStb9yg3pNmBDmq.
- BAnz AT 06.10.2025 B2, Bescheid 10.09.2025: `BEL = Potsdam-Mittelmark`, https://www.bundesanzeiger.de/pub/publication/0u8GOXcEJwvkZk2NcDw.
- BAnz AT 04.05.2026 B3, Bescheid 13.04.2026: `DT`, `LE = Lippe`, https://www.bundesanzeiger.de/pub/publication/f3Kk9pI19sEw8ShV1dx.
- FZV § 9 und Anlage 1, https://www.gesetze-im-internet.de/fzv_2023/BJNR0C70B0023.html.

Die vier JSON-Dateien wurden nach normalisiertem Identifier, Länge, Kategorie, Bundesland, Behördenzuordnung, Duplikaten und Quellenfeldern verglichen. Historische Zeichen wurden nur dann als aktuell gültig gewertet, wenn die BMV-/Bundesanzeiger-Quelle eine erneute Zuteilung oder die aktuelle Übersicht ihre Verfügbarkeit bestätigt.

## Vergleich

| Bestand | Anzahl |
|---|---:|
| Geografische Produktions-Identifier vor Fix | 690 |
| Offizielle geografische Referenz-Identifier | 691 |
| Fehlende Identifier | 1 |
| Geografische Produktions-Identifier nach Fix | 691 |


### Fixstatus

- Vorheriger geografischer Bestand: **690**
- Finaler geografischer Bestand: **691**
- `EF` wurde als `Erfurt` in `Thüringen` ergänzt (`INDEPENDENT_CITY`).
- `MU` wurde von `München` auf `Landkreis München` korrigiert.
- Es bestehen keine ungeklärten fehlenden geografischen Identifier.
### Fehlender Identifier vor dem Fix

| Identifier | Verwaltungsbezirk | Bundesland | Status | Quelle |
|---|---|---|---|---|
| `EF` | Erfurt | Thüringen | aktuell gültig | BMV-Kennzeichenübersicht, veröffentlichte Fassung, 09.01.2026 |

`EF` war der einzige bestätigte fehlende aktuell vergebbare geografische Identifier in dieser Referenzabgrenzung und ist mit dem Fix behoben.

### Explizite Prüfungen

Die Produktionszuordnungen für `BEL = Potsdam-Mittelmark`, `DT = Lippe`, `LE = Lippe`, `JB = Teltow-Fläming`, `LUK = Teltow-Fläming` und `ZS = Teltow-Fläming` stimmen mit den amtlichen Bekanntmachungen überein. `MU` gehört zu `Landkreis München`; die bisherige Modellierung „München“ war daher eine Zuordnungsabweichung und wurde korrigiert. `EF` wurde als kreisfreie Stadt Erfurt in Thüringen ergänzt.

### Obsolete, falsche und doppelte Einträge

- Obsolete aktuell angebotene geografische Einträge: keine bestätigt.
- Falsch zugeordnete geografische Einträge: `MU` — Bezeichnung wurde von „München“ auf „Landkreis München“ korrigiert.
- Falsch platzierte geografische Identifier in `german_special_identifiers.json`, `german_diplomatic_identifiers.json` oder `german_authority_series.json`: keine festgestellt.
- Doppelte normalisierte Identifier über die vier Produktionsdateien: keine.
- Mehrfachzuordnungen: bestehende amtliche Mehrfachzuordnungen bleiben als mehrere Behörden im bestehenden Modell erhalten; keine neue fehlerhafte Mehrfachzuordnung festgestellt.

## Fix und Tests

Der Fix ergänzt `EF` in `german_plate_identifiers.json`, korrigiert `MU` und hinterlegt Primärquellen für die geprüften 2025/2026-Zeichen (`BEL`, `JB`, `LUK`, `ZS`, `MU`, `DT`, `LE`). Die F-Droid-Dateien wurden nicht verändert.

Der Vergleichstest prüft den vollständigen geografischen Referenzsatz, EF, die 2025/2026-Regressionen, Duplikatfreiheit, Quellen, exakte Auflösung und die Zeichen-für-Zeichen-Admission. Die vorherige Zwei-/Drei-Buchstaben-Regression bleibt bestehen und deckt auch neu hinzugefügte Identifier ab.

## Abgrenzung und offene Punkte

Die BMV-Übersicht ist die veröffentlichte amtliche Referenzliste; einzelne historische Zeichen, die nur auf bereits zugelassenen Fahrzeugen fortgelten, werden nicht als neu zuteilbar gezählt. Die Veröffentlichungsseite des Bundesanzeigers weist für den 23.07.2026 zusätzlich eine Bekanntmachung des Bundesministeriums für Verkehr aus; ihr Inhalt konnte in der Recherche nicht maschinenlesbar abgerufen werden. Vor einem finalen Release ist diese Veröffentlichung gegen die BMV-Liste zu prüfen, falls sie weitere geografische Zeichen festlegt.
