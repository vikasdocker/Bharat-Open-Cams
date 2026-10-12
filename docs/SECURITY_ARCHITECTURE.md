# Viewer Access and Anti-Abuse Architecture

**Status:** design proposal for the next release. The current website is static GitHub Pages, and Android opens the publisher's source page externally. This document describes intended safeguards; it does not claim that Google login or recording prevention is already deployed.

## 1. Product/security goal

Make city-wise discovery useful while reducing unauthorized copying, abuse and privacy risk. Publish only cameras intentionally offered for public viewing, and only through the method the publisher permits. Authentication cannot turn a non-public or unauthorized camera into an acceptable source.

## 2. Important platform limits

- A normal website cannot reliably detect that a person is about to take an OS screenshot or start an external screen recorder. There is no universal browser event for “user pressed screenshot.” Therefore, **do not promise “Google login only when someone tries a screenshot.”** Require sign-in before protected viewing instead.
- Disabling right-click, print, selected keyboard shortcuts or browser screen-capture APIs is a deterrent, not a security boundary. Another application, OS recorder or camera can still capture visible pixels.
- The website currently opens the publisher's canonical page in the visitor's browser. Once the user leaves our site, Bharat Open Cams cannot impose its screenshot, download, session or rate-limit rules on that publisher page.
- GitHub Pages is a static host. A JavaScript-only login screen is bypassable and cannot secure public JSON files or direct media URLs.

## 3. Recommended architecture before controlled viewing

1. **Public directory, private viewing path:** keep public catalog metadata separate from protected playback. Add only feeds for which the operator permits the exact integration method.
2. **Google sign-in:** use a supported identity provider such as Firebase Authentication with Google. Configure the approved production domains and consent screen. Do not put service-account keys or private secrets in JavaScript or the Android package.
3. **Server-side authorization:** send the identity provider's ID token to a backend API. Verify token signature, issuer, audience, expiry and user status server-side. The API must make the allow/deny decision; hiding a player in frontend code is insufficient.
4. **Controlled playback only:** a backend can issue short-lived, scoped playback sessions/URLs for feeds that are licensed for that use. Never extract stream URLs from public pages, defeat publisher anti-embedding controls, or proxy/record streams without explicit permission. If the source is link-only, keep it an external link and mark that it is outside our capture controls.
5. **Abuse limits:** rate-limit sign-in and viewing-session creation by account and IP; add bot/WAF protection, concurrency limits, server-side revocation, anomaly alerts, and a moderator path for appeals. Avoid storing unnecessary viewing history or precise location.
6. **Leak deterrence:** overlay a per-session, privacy-conscious watermark (for example, a masked account identifier and UTC timestamp) over any authorized in-app player. Prefer server-side watermarking for higher-risk feeds where permitted. A watermark makes leaks attributable; it does not stop capture.
7. **Secure defaults:** HTTPS only, short-lived tokens, no secrets in URLs, no-store/no-cache responses for protected API payloads, restrictive CORS, a Content Security Policy, and security headers. Do not rely on client IP allowlists as the only control.
8. **Removal and governance:** preserve source attribution, permission scope, review dates and a fast takedown route. Suspend a feed immediately when authorization is disputed. Define log retention and access before collecting authentication or abuse telemetry.

## 4. Native Android controls

The Android app can set WindowManager.LayoutParams.FLAG_SECURE on its own activity to ask Android to block ordinary screenshots and non-secure screen capture of that app window. This is a useful additional layer, **not a universal guarantee**. It does not protect the external browser opened for a link-only camera page, and it cannot stop another physical camera from photographing the screen.

## 5. What can be enforced where?

| Control | Website on a visitor-owned browser | Native Android app | Backend / controlled player |
|---|---|---|---|
| Require Google sign-in before viewing | Only as a real gate when protected content is fetched through a validating backend | Yes, with a server-validated session | Yes; this is the authoritative layer |
| Block downloads of our own delivered files | Avoid download endpoints; still cannot prevent copying rendered media | Avoid export/save actions | Can avoid file downloads and use short-lived playback sessions |
| Block screenshots / screen recording | Cannot guarantee | FLAG_SECURE reduces common capture paths in app window | Watermarking, access controls and auditing deter leaks; cannot eliminate every capture path |
| Apply controls to an external publisher page | No | No, once opened externally | No, unless publisher authorizes an integrated viewing method |

## 6. Rollout sequence

- **Phase A — source expansion:** continue city-by-city research against official portals; publish only camera pages intentionally made public and reviewed for linking. Keep unverified cities in the research register, not the live catalogue.
- **Phase B — identity prototype:** choose and configure the Firebase/Google project and production domain; add a backend endpoint that validates ID tokens.
- **Phase C — protected viewer:** implement per-feed authorization scope, expiring viewing sessions, rate limits and watermarks for sources explicitly permitting the controlled player.
- **Phase D — client hardening:** enable Android FLAG_SECURE, clear playback state on sign-out, handle token expiry, and test on physical devices. Use browser-side deterrents only with honest notices and no security claims beyond what they do.
- **Phase E — launch review:** penetration test authentication/session endpoints, test removal/revocation, review privacy and data retention, and obtain qualified legal/platform review.

## 7. Current status

The current MVP is **not** behind Google sign-in and has no protected camera player. The catalog currently contains nine approved link-only public pages across eight city/town labels: one Chennai weather webcam, one New Delhi AQI camera page and seven official public-venue/live-darshan pages. There are still zero verified public road/traffic CCTV feeds. The Android app sets FLAG_SECURE on its own activity; this protects only the app's own window, not the external source page. Production authentication requires an identity-provider configuration plus a backend; no credentials should be committed to this repository.
