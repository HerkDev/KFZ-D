# F-Droid-Metadatenzusammenfassung

## Anwendung

- Name: KFZ-D
- Paketname: `de.herk.kfzd`
- Repository: https://github.com/HerkDev/KFZ-D.git
- Version: 1.0
- VersionCode: 1
- Lizenz: MIT License

## Metadaten

Die Fastlane-Metadaten liegen für `de-DE` und `en-US` unter `fastlane/metadata/android/`.
Beide Sprachen enthalten Titel, Kurzbeschreibung, vollständige Beschreibung, Changelog `1.txt`, Icon und den Screenshot `phoneScreenshots/1.png`.

Screenshotquelle: `docs/screenshots/mainscreen.png`.

Das Icon basiert auf den finalen Launcher-Ressourcen `kfzd_launcher.xml` und `kfzd_launcher_foreground.xml` und liegt als `icon.png` in beiden Sprachverzeichnissen vor.

## Build-Konfiguration

- minSdk: 26
- targetSdk: 36
- Java-Quell-/Zielkompatibilität: 11
- Gradle Wrapper: 8.13

## Verifizierter Build

- JAVA_HOME: `D:\Android Studio\jbr`
- Java: OpenJDK `21.0.8`
- `clean`: erfolgreich
- `:app:testDebugUnitTest`: erfolgreich
- `:app:assembleDebug`: erfolgreich
- `:app:assembleRelease`: erfolgreich
- Release-Artefakt: `app/build/outputs/apk/release/app-release-unsigned.apk`
- SHA-256 bei zwei Clean-Releases identisch: `927EC87E29813E2303C6B2085BDBAA5720F3742858FBE769B925E0992030C454`

Der Release-Build benötigt keine hinterlegten Signatur-Credentials und erzeugt das für F-Droid geeignete unsignierte APK.
