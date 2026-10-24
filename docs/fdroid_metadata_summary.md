# F-Droid-Metadatenzusammenfassung

## Anwendung

- Name: KFZ-D
- Paketname: `de.herk.kfzd`
- Repository laut Metadaten: https://github.com/HerkDev/KFZ-D.git
- Version: 1.0.6
- VersionCode: 7
- Lizenz: MIT License

Das lokale Projektverzeichnis heißt `KFZ-D-Public`. Es ist ungeklärt, ob
dies lediglich die öffentliche Arbeitskopie des in der Metadatei genannten
Repositories ist. Die Adressen wurden deshalb nicht eigenmächtig geändert.

## Metadaten

Die Fastlane-Metadaten liegen für `de-DE` und `en-US` unter
`fastlane/metadata/android/`. Beide Sprachen enthalten Titel,
Kurzbeschreibung, vollständige Beschreibung, Changelogs, Icon und
Screenshot. Der Changelog für VersionCode 6 beschreibt ausschließlich das
neue Headerbild und die überarbeitete Deutschlandfarben-Welle.

Der neue Changelog für VersionCode 7 beschreibt ausschließlich das neue
Headerbild, die überarbeitete Deutschlandfarben-Welle und das responsive
Layout für unterschiedliche Bildschirmgrößen.

Der vorhandene Git-Tag `v1.0.5` zeigt nach der lokalen Prüfung auf den
Commit `86aea91084e67e2d231494524c3048f792535186`. Der separat genannte
Commit `84486f6212dff53a75aaa31a675777bd1f41996e` ist nicht der Ziel-Commit
dieses Tags. Diese Abweichung muss vor einer historischen F-Droid-Zuordnung
geklärt werden.

## Ressourcen und Daten

Das Headerbild liegt unter
`app/src/main/res/drawable-nodpi/german_landscape_header.png`. Die Datei
wurde ausschließlich umbenannt; es erfolgte keine Bildkonvertierung.
Die vier vorhandenen JSON-Datenbestände wurden nicht geändert.

## Build-Konfiguration

- minSdk: 26
- targetSdk: 36
- Java-Quell-/Zielkompatibilität: 21
- Gradle Wrapper: 8.13
- Release: unsigniertes APK, keine lokalen Signatur-Credentials

## Offene Nachweise

- Exakter Source-Commit oder Release-Tag für 1.0.6/7
- Repository-Konsistenz zwischen `KFZ-D-Public` und `HerkDev/KFZ-D`
- Signaturprüfung mit dem vorgesehenen Release-Zertifikat
- Reproduzierbarkeit zweier Clean-Releases
- F-Droid-Buildserver-Prüfung

Frühere Build-Hashes und die bisherige Versionsdokumentation beziehen sich
auf ältere Projektstände und gelten nicht als Nachweis für Version 1.0.6.
