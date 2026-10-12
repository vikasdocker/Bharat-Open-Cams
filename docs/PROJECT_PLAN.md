# Project Plan

## Product objective

Create a free-to-use, India-focused catalog of authorized public camera feeds, accessible from Android and desktop web. Help people find legitimately published traffic, tourism, weather, and other public-interest live views by city.

## MVP scope

### Web
- Responsive homepage and city selector.
- Browse/search by city and approved camera category.
- Camera detail with publisher attribution, source link, permission status, last verification date and availability status.
- Player/link behavior determined by the source's documented embedding and reuse terms.
- Clear unavailable, removed and unsupported-player states.
- Report/remove request path.

### Android
- Kotlin + Jetpack Compose.
- City/category search and camera list.
- Detail screen with source attribution and terms.
- Favorites stored locally.
- Accessible controls, loading/error/offline states and low-bandwidth behavior.
- No background microphone, location or other unnecessary permissions.

### Backend/catalog
- Versioned schema for sources, camera metadata, authorization evidence and verification timestamps.
- Only manually reviewed/approved feeds enter the public catalog.
- Health checks are limited to approved publisher endpoints and must respect documented rate limits.
- Audit trail for changes and removals.
- No open-camera discovery or credential testing.

## Non-goals
- Finding arbitrary CCTV endpoints on the internet.
- Republishing residential, private, or otherwise unauthorized feeds.
- Bypassing logins, access controls, geo restrictions, tokens or technical safeguards.
- Face recognition, person tracking, identifying residents, or storing/replaying surveillance footage.
- Claiming nationwide coverage before individual source permissions and technical support are verified.

## Acceptance criteria for a feed

Every public catalog entry must have:
1. Named and verifiable publisher.
2. Public-facing source page or explicit written permission reference.
3. Usage scope recorded: a human-reviewed canonical public-page entry may be approved for an outbound link only; embedding, caching, proxying or redistribution require publisher terms, an explicit public licence, or written permission covering that use.
4. A human-readable `terms_review_note` and attribution requirements are recorded.
5. City/area and category grounded in publisher information.
6. Date of authorization/terms verification and review process.
7. A working approved viewing method; no credentials or bypass required.
8. A removal contact or publisher reporting path.

Missing or ambiguous evidence means **do not publish**.

## Milestones
- **M0 — Foundation:** docs, data contract, contribution/security rules.
- **M1 — Source research:** evaluate official candidate sources and record evidence.
- **M2 — Catalog MVP:** validation, review and availability metadata.
- **M3 — Web MVP:** city browsing and accessible camera detail pages.
- **M4 — Android MVP:** browse, detail, favorites and approved playback.
- **M5 — Release readiness:** tests, privacy/legal/platform review, observability, incident and removal process.

**M6 — Viewer access and anti-abuse controls:** configure Google identity, validate sessions server-side, enforce approved feed scopes at a backend, add rate limits and leak deterrence, and test native Android screen-capture restrictions. Do not claim screenshot prevention for ordinary browsers or external publisher pages. See [Viewer Access and Anti-Abuse Architecture](SECURITY_ARCHITECTURE.md).

## Definition of done
- Inspect repository and project docs before changes.
- Work in focused feature branches and review pull requests where possible.
- Run relevant schema checks, tests, lint/build and security checks, or document blockers.
- Review diff for secrets, unsafe URLs and authorization-policy regressions.
- No merge, deployment, Play Store submission, or production release without explicit approval.
- Release notes identify known limitations and do not imply all Indian CCTV cameras are included.