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

The current screen is a UI starter: category chips, search field, empty verified-feed state, and a link to submit official sources. It does not yet load the web catalog or play video. No camera, location, microphone, or storage permissions are requested.