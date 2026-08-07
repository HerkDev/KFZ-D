# Audit der zwei- und dreibuchstabigen Kennzeichen-Identifier

## Ergebnis

Der Audit wurde am 06.08.2026 mit den vier Produktionsdatenbanken durchgeführt:

- `german_plate_identifiers.json`: 277 zweibuchstabige, 356 dreibuchstabige Identifier
- `german_special_identifiers.json`: 4 zweibuchstabige, 12 dreibuchstabige Identifier
- `german_diplomatic_identifiers.json`: 0 zweibuchstabige, 0 dreibuchstabige Identifier
- `german_authority_series.json`: 0 zweibuchstabige, 1 dreibuchstabiger Identifier

Damit wurden insgesamt **281 zweibuchstabige** und **369 dreibuchstabige** Produktions-Identifier getestet.

Jeder Identifier wurde aus leerem Feld Zeichen für Zeichen über dieselbe Admission-Sequenz wie das Textfeld geprüft. Nach jedem Zeichen wurden Prefix-Akzeptanz, Terminal-/Extendable-Verhalten, vollständige Eingabe und die exakte Repository-Auflösung geprüft. Die relevante `DD-Q`-Normalisierung wurde ebenfalls geprüft.

## Vor dem Fix blockierte Identifier

Es wurde kein gespeicherter zwei- oder dreibuchstabiger Produktions-Identifier blockiert.

- Blockierte zweibuchstabige Identifier: **keine**
- Blockierte dreibuchstabige Identifier: **keine**
- Blockierte Positionen, Datensätze, Kategorien und erwartete Ergebnisse: **keine**

`EF` ist in keinem der vier aktuellen Produktions-Assets enthalten. Die explizite Regression bestätigt daher, dass `EF` nicht fälschlich als gültiges gespeichertes Ergebnis akzeptiert wird. Es gibt keinen vorher blockierten `EF`-Produktions-Identifier und somit auch keinen post-fix zu bestätigenden `EF`-Erfolg.

## Ursache und Änderung

Die geprüfte Admission-Logik erlaubt einen Eingabeschritt nur, wenn der bisherige Wert eine gültige Fortsetzung besitzt; ein exakter Identifier mit gültigen längeren Fortsetzungen bleibt dadurch extendable. Ein echter Daten-Identifier darf nicht über eine Einzel-Ausnahme repariert werden.

Für die zentral wiederverwendbare Admission-Regel wurde `IdentifierInputAdmission` ergänzt. Der Audit-Test nutzt dieselbe Zeichen-für-Zeichen-Entscheidung einschließlich Normalisierung, Terminalbehandlung, Exact-Match, Extendable-Match und Auflösung.

## Regressionen

Enthalten sind Regressionen für:

- `EF` als explizit nicht vorhandenen Produktions-Identifier;
- alle vorhandenen zwei- und dreibuchstabigen Identifier;
- einen echten terminalen Identifier mit ungültiger Verlängerung;
- einen exakten Identifier, der zugleich Prefix längerer Identifier ist;
- `DD-Q`/`DDQ`-Normalisierung und Auflösung.

Alle zwei- und dreibuchstabigen Identifier bleiben nach dem Audit enterbar; es gibt keine unresolved cases.

## Verifikation

- Java: `D:\Android Studio\jbr`
- `./gradlew.bat clean`: erfolgreich
- `./gradlew.bat :app:testDebugUnitTest`: erfolgreich
- `./gradlew.bat :app:assembleDebug`: erfolgreich
- Geänderte/neue Dateien: `app/src/main/java/de/herk/kfzd/data/matcher/IdentifierInputAdmission.kt`, `app/src/test/java/de/herk/kfzd/TwoThreeLetterInputAuditTest.kt`

Installation und visuelle Prüfung waren nicht möglich: `adb devices -l` meldete keine verbundenen Geräte oder Emulatoren; auch `adb reconnect` fand keine. Deshalb wurden `EF`, ein weiterer vorher blockierter Zwei-/Dreibuchstaben-Identifier und ein Terminal-Identifier nicht auf einem Gerät visuell geprüft. Es wurde nichts installiert.

Es wurde weder committed noch gepusht. Die vorhandene unversionierte Änderung `docs/fdroid/` blieb unangetastet.
