# Android app

Native Android client starter built with Kotlin and Jetpack Compose.

## Open in Android Studio

Open the repository root as a Gradle project after generating/checking in the Gradle wrapper for the configured Gradle 9.6.0 distribution. The repository stores the wrapper distribution configuration, but the wrapper JAR and launcher scripts are not yet checked in through this GitHub connector workflow.

Required local tooling:
- JDK 17+
- Android SDK Platform 37 and compatible Build Tools
- Android Studio version compatible with Android Gradle Plugin 9.4.0
- Gradle 9.6.0 if generating the wrapper manually

From a machine with Gradle 9.6.0 installed, generate wrapper launchers from the repository root with:

    gradle wrapper --gradle-version 9.6.0

Then launch an Android Studio Gradle sync and assemble a debug build. Do not publish a Play Store build until playback source permissions, privacy disclosures, legal review and the store listing are complete.

## Current implementation

The app downloads the public data/feeds.json catalog from the repository's main branch over HTTPS. Only items whose feed and authorization states are approved, whose publisher/source/terms links are HTTPS, and whose viewing method matches the declared permission scope are displayed. Search, city selection and category chips filter the catalog. Opening a feed launches its official source page in an external browser; the current MVP does not proxy, record, or embed video.

The catalog is currently empty while camera source permissions are researched. The INTERNET permission is used for the catalog request; camera, location, microphone, contacts and storage permissions are not requested.