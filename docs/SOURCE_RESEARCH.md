# India Source Research Register

**Last researched:** 12 October 2026  
**Public approved CCTV feeds found in this review:** 0  
**Important:** the entries below are research leads only. They are not approved camera feeds and must never be copied into `data/feeds.json` as live cameras without fresh review.

## Findings

### Mumbai Traffic Police — CCTV programme page

Official page: https://trafficpolicemumbai.maharashtra.gov.in/cctv/  
Additional programme description: https://trafficpolicemumbai.maharashtra.gov.in/en/initiative/

The official page describes CCTV integration with public/private surveillance networks for traffic management. The reviewed page does not expose a clear public live-video player or terms authorizing third-party republication. A page about CCTV infrastructure is not proof that camera feeds are public. **Status: contact the department for an official public feed and written usage scope.**

### Delhi Traffic Police — public interface

Official page: https://www.traffic.delhipolice.gov.in/en/public-interface

The page provides citizen traffic services and RLVD/OSVD camera information, but that is not evidence of a public live video stream. **Status: not a usable live camera feed.**

### Kashi Integrated Command and Control Centre — Varanasi

Official project page: https://kashi.gov.in/project-details/kashi-integrated-command-control-center

The official description says live surveillance feeds are displayed on a video wall for city officials. It does not present those feeds as a public streaming service. **Status: do not publish or attempt to access the control-centre feeds.**

### Utsav — Live Darshan

Official directory: https://utsav.gov.in/live-darshan  
Official website policies: https://utsav.gov.in/website-policies

This is an official Ministry of Tourism initiative with public live-darshan streams, but these are tourism/spiritual broadcasts, not road, lane, or street CCTV. The portal's policy says material reproduction requires permission and third-party material has separate rights holders; its hyperlinking policy allows links to official pages and disallows loading its pages in frames. **Status: out of scope for the CCTV MVP. At most, consider a separate outbound-link category after reviewing the source terms; no stream embedding or redistribution.**

### NIC Government Webcast

Official portal: https://webcast.gov.in/  
Service details: https://webcast.gov.in/services?lang=en

NIC provides live government channels and event webcasts. These are intentional public broadcasts, but not traffic/neighborhood CCTV. **Status: not an MVP CCTV source.**

## Research conclusion

I did not find a source in this review that met all criteria for an approved public road/traffic CCTV feed with a verified public viewing method and permission to reuse it in this app. This is not evidence that no such feeds exist; it means permission and technical access have not yet been verified.

The next legitimate route is source-owner outreach:
1. Ask official traffic/municipal authorities whether they publish public traffic webcams.
2. Request the canonical public viewing URL, allowed integration method, usage/licence terms, attribution text, rate limits, and removal/contact path.
3. Record written permission without placing confidential correspondence in this public repository.
4. Add an item to `data/feeds.json` only after a maintainer verifies all acceptance criteria in `docs/FEED_POLICY.md`.

## Research references

- Mumbai Traffic Police CCTV page: https://trafficpolicemumbai.maharashtra.gov.in/cctv/
- Mumbai Traffic Police initiative: https://trafficpolicemumbai.maharashtra.gov.in/en/initiative/
- Delhi Traffic Police Public Interface: https://www.traffic.delhipolice.gov.in/en/public-interface
- Kashi ICCC: https://kashi.gov.in/project-details/kashi-integrated-command-control-center
- Utsav Live Darshan: https://utsav.gov.in/live-darshan
- Utsav Website Policies: https://utsav.gov.in/website-policies
- NIC Webcast Services: https://webcast.gov.in/services?lang=en
