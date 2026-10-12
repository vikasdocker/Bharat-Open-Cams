# Public Feed Authorization and Privacy Policy

## Core rule

**Publicly reachable is not the same as authorized for public redistribution.** The project must not search for or collect arbitrary exposed CCTV streams.

## Acceptable candidates

Candidate categories—not automatic approval:
- Official municipal or traffic authority public-view pages.
- Government-published public-interest feeds with clear terms.
- Tourism boards and public venues that explicitly offer a live public webcam.
- Weather/environmental webcams published for public viewing.

Approval must be based on the actual publisher and the exact viewing method. The project distinguishes an outbound link to a publisher's own public page from embedding, copying, proxying or redistributing the underlying media.

### Link-only public webcam pages

The catalog may use authorization type `publisher-public-page` for a **human-reviewed outbound link only** when all of the following are true:
- The named operator intentionally presents the webcam on a canonical public HTTPS page.
- The submitted listing points to that page, not to an underlying image/stream endpoint extracted from the page.
- The reviewer finds no stated prohibition against ordinary linking and records a review note explaining the scope.
- Publisher attribution and a removal/contact path are recorded.
- The listing does not copy thumbnails or video, embed the third-party player, proxy the stream, cache/re-host images, record footage or expose stream URLs.

This is a narrow directory-link exception, **not a licence to reuse the picture or video**. If the terms are unclear, the source owner objects, or the source page/terms change, suspend the entry and obtain confirmation or remove it.

For embedding, direct stream integration, caching or redistribution, a public page alone is not enough. The publisher's terms, an explicit public licence, or written permission must cover that specific use. Direct streams require explicit redistribution permission.

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
- `terms_url` where applicable, `reuse_scope`, `attribution_text`, and a human-readable `terms_review_note` explaining the approved use
- `verified_at`, `review_due_at`, `reviewer`
- `status` (`pending`, `approved`, `suspended`, `removed`)
- `removal_contact` or publisher reporting URL

Do not place private contact details or confidential permission documents in a public repository.

## Display and technical safeguards
- Prefer linking to the official source/player when embedding is not expressly allowed. For link-only public-page approval, open the canonical page externally and never extract or re-host its media.
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