# Proposed Architecture

This is a technology direction, not an implementation claim. Decisions should be validated with a small prototype before committing to deployment infrastructure.

## Components

### Feed catalog
A version-controlled catalog is the initial source of truth. Every entry contains source metadata plus authorization evidence/status. A future API may serve only approved entries.

### Web client
- Responsive frontend with keyboard support and accessible status labels.
- Directory, city/category filters and camera detail.
- Official source attribution and terms surfaced alongside viewing controls.
- The player is selected per source policy: official link, approved embed, or approved stream integration.

### Android client
- Kotlin + Jetpack Compose.
- Repository layer isolates catalog API/data source.
- ViewModels expose UI state.
- Local storage for favorites and non-sensitive preferences.
- Playback handled only by an approved mechanism supported by that source.

### Operations
- Automated checks only against approved, documented endpoints.
- Rate limits, timeouts, bounded retries and clear health status.
- Removal/takedown queue and audit history.
- CI for schema validation, tests, lint and dependency/security checks.

## Initial feed model

See `../data/feeds.schema.json`. Keep authorization evidence as important as playback metadata. Do not store camera passwords, private tokens or unapproved stream URLs in this repository.

## Suggested future stack
- Android: Kotlin, Jetpack Compose, MVVM.
- Web: TypeScript-based responsive frontend (framework to be chosen after initial UX prototype).
- API: choose after catalog size and client needs are understood.
- CI: GitHub Actions, with no secrets required for public schema/documentation tests.

## Reliability and performance
- Never block city browsing while a stream is buffering.
- Show timeouts and unavailable sources clearly.
- Use adaptive/low-bandwidth playback only if source terms and format permit it.
- Avoid downloading video merely to generate previews.
- Respect publisher cache headers and terms.
- Set conservative timeouts, retry caps and health-check intervals.