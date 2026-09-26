# ROUTEMSK v1.0 RC — 2026-09-26

## Scope and acceptance
Gemini corrections and DeepSeek release QA briefs retained unchanged and used as acceptance criteria. No claim of an independent Gemini or DeepSeek model review. Production launch remains NO-GO pending blockers below. RC is prepared for user preview.

- Updated homepage, three unique zone pages, working /check/, two explicitly draft legal pages, branded 404, robots.txt and five-URL sitemap.
- Kept existing v2.1 graphite/gold layout and road/truck SVG fallback. Pill controls; improved muted text contrast/focus. No fixed bottom CTA, and no sticky mobile checker header.
- Removed checker placeholder and unverified commercial claims (discount, fleet size, experience date, included temporary permit and free reapplication). Core supplied prices retained and labelled as accompaniment, not state fees.
- Canonical, OG and truthful WebPage/BreadcrumbList JSON-LD. No invented identity/logo/address. Draft legal pages noindex.
- Checker: Moscow calendar dates, cancellation precedence, latest active endDate, latest remaining record, multiple-active notice; future/unknown handled conservatively. Issued without dates is not active. UI also displays day/night type if supplied.
- Native submit/Enter, duplicate suppression, 25-second timeout, safe textContent, no raw technical errors, contact fallback.
- Analytics hooks transmit only allowlisted event names. No Metrika script/Webvisor loaded; no PII params or API payloads.
- RC backend adds global 10/minute limit, 60-second bounded memory cache and concurrent deduplication, origin rejection, null-body handling and sanitized logs/errors. Existing 20 KB body limit retained. These changes are in DEV only; live Render deployment not confirmed. This is not persistent monthly quota enforcement.

## Verification before publishing
- Chromium: 8 pages × 1440, 768, 390×844, 360×800 = 32 view checks; no horizontal overflow, no page JavaScript errors. Screenshots visually inspected.
- 13 browser checker cases with controlled API responses: active, expired, cancelled, future, issued/no-dates, multiple, not_found, malformed, HTTP 503, network failure, invalid input, duplicate Enter, 25-second timeout. Mocks are test-only and are not shipped.
- Moscow midnight, end-day inclusion, invalid dates and selection regression tests. Backend tests cover origins, invalid/oversize bodies, caching, deduplication, safe provider failure and rate limiting.
- Analytics spy confirms only event names. Menu Escape/focus return verified. Reduced 360×400 viewport checked for fixed-overlay interference; physical mobile keyboard is not emulated by desktop Chromium.
- Preview copy: 32 view checks, HTTP 200, no missing assets, noindex, production canonicals, all internal paths under /preview-release/; actual navigation through zone/check/legal returns to preview.
- robots.txt parsed; no Host or legal Disallow. XML sitemap parsed with exactly five requested production URLs. CNAME unchanged.
- Real API request from routemsk.ru origin returned HTTP 200 with an expired permit. Active/not_found/error UI coverage uses deterministic fixtures; no claim that a real active permit was supplied.
- Fact copy follows the supplied corrections brief. Fresh fetch of the official transport page timed out; no new legal fact-check certification is claimed. No specific fines published.

## Release blockers / discrepancies
| ID | Severity | Component / actual | Required action and regression |
|---|---|---|---|
| LEGAL-01 | P0 production | Operator identity, actual legal details, privacy terms and lawful processing basis absent; pages explicitly draft/noindex | Supply real operator data, approve policy/consent and processing arrangements; verify collection flow before production |
| API-01 | P1 before traffic | Live Render rate limit/cache/logging fixes not deployment-verified | Deploy reviewed backend changes through approved Render workflow; verify 429/cache/CORS/error sanitization and provider quota plan |
| METRIKA-01 | P1 before traffic | No counter ID; analytics not collecting | Supply counter ID, verify privacy settings and PII-free events; keep Webvisor off until verified |
| HERO-01 | P1 user acceptance | Existing SVG fallback, no final hero photo | Approve night Moscow/road/truck photo and verify responsive contrast/crop |
| PAGES-404 | Preview limitation | main root must stay unchanged; nested 404.html is a viewable design, not Pages global error handler | After production approval install RC root 404.html and verify a genuinely missing URL returns branded HTTP 404 |

## Preview isolation
scripts/build-preview.cjs copies an explicit static allowlist, excluding server, CNAME, root sitemap/robots and credentials. All preview HTML has noindex,nofollow,noarchive; canonical targets intended production URL; preview schema removed. Existing main files and PR #10 must remain untouched. Publish only main/preview-release/ in its own commit.

## Factual contradictions / SEO cannibalization / checker edge cases
No contradictions found against supplied baseline; MKAD focuses on RMM/route/RNIS, TTK on payload/time restrictions, SK on central delivery/address validation. No extra landing pages. Multiple records, cancelled/expired/future dates and malformed responses covered. Date-only validity uses Moscow day boundaries. Unknown dates are never promoted from “Выдан” to active.

## Live publication result
- Live URL: https://routemsk.ru/preview-release/
- RC commit: 362495e8219923c529af90a97016147b62ed8e87 (redesign-v1).
- Preview commit: 32ee515b4f8a44b282397100bbe959b8c40a043b (main).
- Live Chromium QA: all 32 page/viewport checks passed (1440, 768, 390×844, 360×800). No page errors or resource errors; navigation remained preview-only; every HTML page noindex and correct canonical.
- Live preview checker called real Render API and displayed the expired record correctly; no horizontal overflow. Other status scenarios tested with controlled browser responses as listed above.
- Real missing preview URL returns HTTP 404 with existing GitHub Pages error page, not the nested branded template; expected isolation limitation described above.
- Git diff from original main 815d5d04658307e9b1996fb64cb2ddebb80e947f contains only preview-release/ paths. Production index.html blob before/after: b6332b4e989e247c2e55a0a4d1508ae91a3e9912. All other existing main files unchanged.
- PR #10 remains open, draft, unmerged; no PR metadata changed.

GO: live preview for user review. NO-GO: production until P0/P1 acceptance items are resolved.
