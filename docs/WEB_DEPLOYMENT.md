# Web Directory Deployment

The website is a static, dependency-light frontend at the repository root. Its camera catalog is loaded from data/feeds.json, and only entries with both catalog status and authorization status approved are rendered.

## Enable GitHub Pages once

1. Open the repository Settings.
2. Open Pages under Code and automation.
3. Under Build and deployment, set Source to GitHub Actions.
4. Merge an approved change into main. The Deploy web directory workflow will package only the website assets and catalog and publish them.

The workflow intentionally excludes the Android source, CI configuration, development scripts and repository documentation from the Pages artifact. Feed data is public website data; never place private credentials, confidential permission documents or non-public streams into the catalog.

## Before launch

- Ensure the workflow's latest run completed successfully.
- Open the generated Pages URL on mobile and desktop.
- Verify keyboard focus, reduced-motion behavior, search/filter empty states and source links.
- Confirm the privacy policy, terms, contact/removal channel and official-source permission review are ready.
- Keep the catalog empty until each listing is reviewed and approved.