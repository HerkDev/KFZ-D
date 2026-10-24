# F-Droid-Release-Checkliste

## Projektstand 1.0.6 / VersionCode 7

- [x] `LICENSE`, `README.md` und `THIRD_PARTY_NOTICES.md` vorhanden
- [x] Anwendung `KFZ-D` und Paketname `de.herk.kfzd` geprüft
- [x] Version `1.0.6` und VersionCode `7` im Gradle-Projekt geprüft
- [x] minSdk `26` und targetSdk `36` geprüft
- [x] Deutsche und englische Fastlane-Metadaten vorhanden
- [x] Historischer Changelog für VersionCode `6` unverändert erhalten
- [x] Changelogs für VersionCode `7` angelegt
- [x] Changelogs für VersionCode `6` angelegt
- [x] Screenshots und Icons in beiden Sprachen vorhanden
- [x] Headerbild als PNG eingebunden und ohne Konvertierung umbenannt
- [x] Unit-Tests erfolgreich
- [x] Android Lint erfolgreich, jedoch mit Warnungen
- [x] Release-Build erfolgreich
- [ ] Exakten Source-Commit oder Release-Tag für Version 1.0.6 festlegen
- [ ] Repository-Adressen gegen das tatsächlich zu veröffentlichende Repository bestätigen
- [ ] Signaturprüfung mit dem vorgesehenen Release-Zertifikat durchführen
- [ ] Reproduzierbarkeit mit zwei Clean-Releases prüfen und dokumentieren
- [ ] F-Droid-Metadaten in den offiziellen Datenbestand übertragen
- [ ] F-Droid-Buildserver-Prüfung abwarten

Zusätzlich ist die Zuordnung des bisherigen Tags zu klären: Der lokale Tag
`v1.0.5` zeigt auf `86aea91084e67e2d231494524c3048f792535186`, während der
separat genannte Commit `84486f6212dff53a75aaa31a675777bd1f41996e` davon
abweicht.

Der aktuelle Build erzeugt ein unsigniertes Release-Artefakt unter
`app/build/outputs/apk/release/app-release-unsigned.apk`.

Die Metadatei verweist auf `https://github.com/HerkDev/KFZ-D`. Das lokale
Projektverzeichnis heißt `KFZ-D-Public`; ob beide Bezeichnungen dasselbe
kanonische Repository meinen, ist vor der Einreichung zu bestätigen.

Die F-Droid-Konfiguration enthält für Version 1.0.6 bewusst noch keinen
Commit oder Tag. Dieser darf erst nach Prüfung des tatsächlichen Upstream-
Stands ergänzt werden.
