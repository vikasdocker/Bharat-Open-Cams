# Bharat Open Cams

A city-first directory for **officially authorized public camera feeds** in India, with Android and desktop web clients.

> **Permission first:** a camera being reachable on the internet does not mean its feed is authorized for public listing or redistribution. We publish only sources whose official publisher and permitted viewing method have been reviewed.

## Current status

**MVP starter in development.** The responsive web directory and initial native Android screen are scaffolded. The feed catalog is deliberately empty, so the product does not yet show live camera feeds. The Android screen is not yet connected to the catalog and does not play video. GitHub Pages and Play Store are not yet published.

## What is included

- Responsive website with city, category and keyword filters; it displays only entries with approved catalog and authorization states.
- Native Android starter using Kotlin and Jetpack Compose.
- Versioned feed registry schema, strict catalog validation and CI.
- A feed submission form that asks for official publisher and permission evidence.
- Privacy, terms, security and contribution docs.
- Separate web deployment and Android debug-build workflows.

## Safety rules

- Prefer official municipal traffic feeds, government public-interest cameras, and tourism/weather webcams only after checking publisher terms.
- Do not scan, scrape, or aggregate arbitrary exposed IP cameras.
- Do not publish residential/private cameras, use credentials, bypass access controls, or republish a stream contrary to its terms.
- Unclear authorization means the source stays out of the catalog.
- No face recognition, identity matching, person tracking, or private video recording.

## Repository guide

- [Project plan](docs/PROJECT_PLAN.md)
- [Feed authorization and privacy policy](docs/FEED_POLICY.md)
- [Privacy notice](docs/PRIVACY.md)
- [Terms of use](docs/TERMS.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Web deployment](docs/WEB_DEPLOYMENT.md)
- [Contribution workflow](CONTRIBUTING.md)
- [Security policy](SECURITY.md)
- [Feed registry schema](data/feeds.schema.json)
- [Empty catalog](data/feeds.json)

## Run the web preview locally

Use a local HTTP server so the browser can load the JSON catalog:

    python -m http.server 8000

Then open http://localhost:8000. Since data/feeds.json currently contains an empty array, the directory shows an empty-state message rather than inventing camera entries.

## Android setup

The Android project is in the repository root with module app/. It is configured for Android SDK API 37, Android Gradle Plugin 9.4.0, and Gradle 9.6.0.

The Gradle distribution properties are checked in, but the standard Gradle wrapper launcher scripts/JAR are not yet present. On a development machine with Gradle 9.6.0 installed, run:

    gradle wrapper --gradle-version 9.6.0

Then open/sync the project in a compatible version of Android Studio and run the app. The Android workflow builds a debug APK on GitHub-hosted runners when its CI checks pass; this does not mean a Play Store release is ready.

## CI and deployment

- Catalog/schema validation runs on pushes and pull requests.
- Android CI attempts a debug APK build and stores a short-retention artifact.
- The Pages workflow packages the website only; one-time repository setup is required under Settings → Pages → Source → GitHub Actions.
- Do not merge or publish until the relevant checks pass and launch reviews are complete.

## Release readiness

Before a public release, complete: approved source research with documented terms, web deployment verification, Android device/accessibility tests, source-specific playback, privacy/legal and platform-policy review, removal/support contact, and a production-ready signed Android build.

## License

No software license has been selected yet. Until a license is added, do not assume this code is licensed for reuse.
