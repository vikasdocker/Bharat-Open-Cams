# Bharat Open Cams v0.1.0 — Draft Release Notes

This pre-release packages the initial project foundation for development and review.

## Included
- Responsive web directory with search and city/category filters.
- Native Android Kotlin + Jetpack Compose screen starter.
- JSON feed catalog with one link-only Chennai weather-webcam page; schema validates source metadata, authorization status and permitted viewing method.
- CI checks for catalog format, authorization rules and JavaScript syntax.
- Android debug-build workflow and a manual GitHub Pages deployment workflow.
- Contribution, security, feed authorization, privacy and terms documentation.

## Explicit limitations
- The catalog includes one approved link-only weather webcam page in Chennai, but zero approved road/traffic CCTV feeds. No media is copied, embedded, proxied or recorded.
- Android loads the public catalog over HTTPS and opens each approved publisher page externally; the MVP does not embed or play camera streams.
- The website is not considered deployed until GitHub Pages is enabled and the deployment workflow succeeds.
- The project has not been submitted to the Google Play Store.
- A debug APK is not a signed production Android App Bundle.
- All source permissions, terms, attribution, removal processes and legal/platform reviews must be completed before a public production launch.

This is a development pre-release, not a production launch.