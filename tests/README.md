# Tests

The catalog checks run through scripts/validate_catalog.py and the GitHub Actions workflow. They validate the JSON Schema itself, both catalog JSON files, common schema formats, duplicate IDs, and extra publication authorization requirements.

The Android workflow builds a debug APK on a GitHub-hosted Android runner. This checks compilation but does not replace device tests, accessibility review, source-specific playback validation, privacy/legal review, or Play Store release review.