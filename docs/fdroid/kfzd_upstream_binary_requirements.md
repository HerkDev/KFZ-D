# Vorbereitung des Upstream-Binaries für F-Droid

## Zielversion

- Version: `1.0.6`
- VersionCode: `7`
- Exakter Source-Commit: noch offen
- Erforderlicher Release-Tag: noch offen
- APK-Dateiname: nach dem tatsächlichen Release-Schema festlegen

Es werden bewusst keine Commit-Hashes oder Release-Tags erfunden. Der
F-Droid-Build-Eintrag für Version 1.0.6 enthält deshalb zunächst keinen
Commit. Vor der Einreichung muss er auf den geprüften Upstream-Commit oder
Tag zeigen.

## Repository-Konsistenz

Die F-Droid-Metadatei verweist auf:

`https://github.com/HerkDev/KFZ-D.git`

Das lokale Projektverzeichnis heißt `KFZ-D-Public`. Ob dies ein Alias, eine
Arbeitskopie oder ein separates Veröffentlichungsrepository ist, muss vor
der Einreichung bestätigt werden. SourceCode-, IssueTracker-, Repo- und
Binaries-Adressen wurden daher nicht automatisch geändert.

Der vorhandene lokale Tag `v1.0.5` zeigt auf den Commit
`86aea91084e67e2d231494524c3048f792535186`. Der separat angegebene Commit
`84486f6212dff53a75aaa31a675777bd1f41996e` ist davon abweichend und muss
vor einer historischen F-Droid-Zuordnung geklärt werden.

## Signatur und Reproduzierbarkeit

Der Release-Build muss mit dem vorgesehenen dauerhaft reproduzierbaren
Release-Schlüssel geprüft werden. Eine Debug- oder lokal neu erzeugte
Keystore-Datei darf nicht verwendet werden; der private Schlüssel gehört
nicht in das Repository.

Noch offen:

- Signaturzertifikat gegen `AllowedAPKSigningKeys` prüfen
- versionierte signierte Referenz-APK bereitstellen
- zwei Clean-Releases bauen und SHA-256 vergleichen
- F-Droid-Buildserver-Prüfung durchführen

Die bisher dokumentierten Nachweise bezogen sich auf ältere Versionen und
werden nicht als Nachweis für 1.0.6/7 übernommen.
