# Bharat Open Cams

A city-first directory for **officially authorized public camera feeds** in India, with Android and web clients.

> **Privacy and authorization first:** a camera being reachable on the internet does not mean its feed is authorized for public redistribution. This project will only list streams whose publisher explicitly permits public viewing and whose terms allow the intended linking, embedding, or redistribution.

## Project status

**Stage:** Project foundation — no live feeds or production app are included yet.

Planned clients:
- **Android:** Kotlin, Jetpack Compose, city/area browsing, map/list, favorites and clear source attribution.
- **Web:** responsive directory for desktop users, searchable by city and camera category.
- **Backend/catalog:** curated feed registry, provenance and permission tracking, availability monitoring, moderation/removal workflow.

## What can be listed

A feed must have a verifiable official publisher and documented permission/terms suitable for the way it will be displayed. Prefer official municipal traffic feeds and public tourism/weather webcams. Keep source URL, publisher, permission/licence notes, attribution, geographic scope, last verification date, and status alongside every entry.

Do **not** discover, scan, scrape, or aggregate arbitrary exposed IP cameras; do not attempt default credentials, bypass authentication, or publish residential/neighborhood/private feeds. Do not treat internet accessibility as consent. Unclear feeds stay out until authorization is verified.

## Repository guide

- [Project plan](docs/PROJECT_PLAN.md)
- [Feed authorization and privacy policy](docs/FEED_POLICY.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Contribution workflow](CONTRIBUTING.md)
- [Security policy](SECURITY.md)
- [Feed registry schema](data/feeds.schema.json)
- [Empty example registry](data/feeds.example.json)

## Development roadmap

1. Define feed permission and review process.
2. Evaluate official candidate sources and record evidence.
3. Build a curated catalog, responsive web directory and Android client.
4. Add tests, CI, accessibility, resilience and performance checks.
5. Complete legal, privacy, platform-policy and operational reviews before public launch.

## Disclaimer

This repository is a project starter, not an endorsement of any individual camera stream. Each source must be independently reviewed and removed promptly if authorization is withdrawn or uncertain. This project does not guarantee that any camera is live or available.

## License

No software license has been selected yet. Until a license is added, do not assume the code is licensed for reuse.