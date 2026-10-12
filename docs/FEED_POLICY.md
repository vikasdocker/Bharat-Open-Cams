# Public Feed Authorization and Privacy Policy

## Core rule

**Publicly reachable is not the same as authorized for public redistribution.** The project must not search for or collect arbitrary exposed CCTV streams.

## Acceptable candidates

Candidate categories—not automatic approval:
- Official municipal or traffic authority public-view pages.
- Government-published public-interest feeds with clear terms.
- Tourism boards and public venues that explicitly offer a live public webcam.
- Weather/environmental webcams published for public viewing.

Approval must be based on the actual publisher and terms for the intended display method.

## Not acceptable
- Residential buildings, private homes, apartment corridors, neighborhood/security cameras or personal doorbells without explicit authorization for public publication.
- Feeds found by IP-range scanning, port scanning, search-engine dorking, exposed-device databases, or scraping lists of unsecured cameras.
- Cameras with unknown ownership or unclear permission.
- Streams requiring credentials, leaked tokens, guessed/default passwords, or bypassing controls.
- Republishing, proxying, caching or recording a feed when terms do not permit that behavior.
- Feeds intended for person tracking, identity inference or targeting individuals.

## Evidence record

Keep, at minimum:
- `publisher_name`, `publisher_url`, `source_page_url`
- `authorization_type` and a concise evidence reference (do not store private correspondence publicly without permission)
- `terms_url`, `reuse_scope`, `attribution_text`
- `verified_at`, `review_due_at`, `reviewer`
- `status` (`pending`, `approved`, `suspended`, `removed`)
- `removal_contact` or publisher reporting URL

Do not place private contact details or confidential permission documents in a public repository.

## Display and technical safeguards
- Prefer linking to the official source/player when embedding is not expressly allowed.
- Respect publisher rate limits, access rules and terms. Do not defeat anti-embedding controls.
- Do not cache or record video unless explicitly permitted.
- Do not add face recognition, identity matching or person tracking.
- Collect only data necessary for directory operation. Do not collect viewers' precise location by default.
- Provide a clear report/remove path and suspend feeds quickly when permission is challenged.
- Keep logs free of credentials, access tokens and unnecessary viewing history.

## Review and withdrawal

A human review is required before publication and at each review deadline. Any credible authorization complaint, terms change, or uncertainty immediately changes the feed to `suspended` pending review. Remove the entry when permission is withdrawn or cannot be substantiated.

## Legal review

Before launch, obtain qualified advice on Indian privacy, intermediary/content, copyright/database, contractual, and Google Play requirements as applicable. This policy is an engineering safeguard, not legal advice.