# Bharat Open Cams

A city-first directory for **officially authorized public camera feeds** in India, with Android and desktop web clients.

> **Permission first:** a camera being reachable on the internet does not mean its feed is authorized for public listing or redistribution. We publish only sources whose official publisher and permitted viewing method have been reviewed.

## Current status

**MVP deployed; Live Watch now loads three explicitly embeddable OpenCCTV road-camera viewer wrappers.** The responsive web directory and native Android client read the shared public catalog; Android fetches it from the canonical GitHub raw URL over HTTPS and opens each publisher's official page. The web client now includes a Live Watch wall that auto-plays only provider-hosted embeds explicitly approved for embedding, muted and with lazy-loading safeguards. The current catalog has 41 entries: four nationwide discovery hubs plus 37 individual public source pages. Three individual OpenCCTV road-camera pages (Chennai/Tiruvottiyur, Guwahati, and Chennai/Choolaimedu High Road) are shown in Live Watch through their published iframe wrappers; the remaining 38 entries stay link-only. The hubs link to ARGOS ATLAS, OpenCCTV.org, opencam.live and the Ministry of Tourism's Utsav Live Darshan directory. Individual cards cover public pages across India, including regional traffic/webcam listings and 23 public-venue Live Darshan pages. These links improve coverage, but they are not 41 streams directly integrated into our own player. These embeds load only the OpenCCTV-hosted viewer pages and do not expose or re-host upstream camera URLs. Their feeds can be intermittent or periodically refreshed still images rather than continuous video. The project still has zero direct integrations of upstream road/traffic media URLs: its three road-camera displays are provider-hosted wrappers only. The website never extracts, copies, proxies, caches or records the underlying media. GitHub Pages is deployed; Android is available as a debug build but is not published on Google Play. Google sign-in and backend-enforced protected playback are not yet implemented.

## What is included

- Responsive website with city, category and keyword filters; it displays only entries with approved catalog and authorization states.
- Live Watch wall with muted auto-play for explicitly approved provider-hosted embeds only; it does not embed link-only sources or arbitrary player URLs.
- Native Android client using Kotlin and Jetpack Compose, with HTTPS catalog loading, city/category/search filters, retry/error states, and external opening of the official source page.
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
- [Viewer access and anti-abuse architecture](docs/SECURITY_ARCHITECTURE.md)
- [Source research register](docs/SOURCE_RESEARCH.md)
- [Permission request template](docs/OUTREACH_TEMPLATE.md)
- [Machine-readable source candidates](data/source-candidates.json)
- [Web deployment](docs/WEB_DEPLOYMENT.md)
- [Contribution workflow](CONTRIBUTING.md)
- [Security policy](SECURITY.md)
- [Feed registry schema](data/feeds.schema.json)
- [Public camera catalog](data/feeds.json)

## Run the web preview locally

Use a local HTTP server so the browser can load the JSON catalog:

    python -m http.server 8000

Then open http://localhost:8000. The catalog currently contains 41 link-only entries, including four nationwide discovery hubs and individual public-source pages across India. Individually integrated road/traffic CCTV remains empty until a public viewing route and permitted use are verified. The website reads this same file locally.

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
