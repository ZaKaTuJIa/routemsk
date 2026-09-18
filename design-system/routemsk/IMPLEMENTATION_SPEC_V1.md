# ROUTEMSK — Visual Implementation Specification v1.0

Status: implementation contract for PR #1.
Branch: `codex/v0.8-seo-foundation`.
Method: `design-system/routemsk/MASTER.md` + UI/UX Pro Max methodology.
Purpose: turn the existing page into a visibly new premium B2B service landing. This is NOT a cosmetic pass.

## 1. Non-negotiable result
Within 2 seconds the page must look materially different from the current version. A pass that only changes colors, shadows, borders, radii or price emphasis FAILS.

Preserve: current factual copy unless explicitly rearranged; canonical/OG/schema/SEO; Metrika semantics; file validation and local-only STS behavior; native FAQ; anchors; accessibility; reduced motion; working messenger/phone links.

Never invent: ratings, testimonials, review counts, guarantees, legal requisites, prices, terms, trust claims or live application/status data.

## 2. Visual language
Direction: premium, restrained Moscow B2B transport/service interface.
Palette: ink #08111F; graphite #0F172A; blue #2563EB; blue-dark #1D4ED8; functional green #16A34A only for verified/positive markers; soft #F4F7FB; line #DCE4EE; white #FFFFFF.
Typography: system grotesk stack; no external font dependency. H1 56–64 desktop / 36–40 mobile, 0.98–1.04 line height. H2 40–48 desktop / 30–34 mobile. Body 16–18. Use weight/space, not decorative effects.
Radius: 16–24 for major surfaces, 10–14 controls. Shadows restrained. No glassmorphism, neon, fake dashboards, stock truck hero, decorative gold, or AI gradients.
Spacing: 8px rhythm. Desktop section padding 88–104; mobile 56–72.

## 3. Header
Desktop: 72px sticky white/near-white. Logo left. Compact nav center/right. Primary compact CTA right.
Mobile: 60px. Logo MUST remain left and a compact visible CTA MUST remain right. Do not hide all conversion actions. Mobile label: “Начать” or “СТС”; target #check. Minimum 44px touch target.
Header should feel like a product/service bar, not a generic template.

## 4. Hero — mandatory recomposition
Desktop >=1024: 12-column composition, approximately 55/45.
LEFT:
- eyebrow: “Грузовые пропуска · Москва”
- H1 remains semantically one H1: “Пропуск на МКАД, ТТК и СК — 8 000 ₽ под ключ”
- price may be a separate visual line/chip inside H1, but not an isolated recolor gimmick.
- lead: current post-payment copy.
- three current factual benefits.
- add a clear primary CTA to #check and a secondary messenger action using existing links. Do not add new claims.
- compact factual proof rail under actions using ONLY existing facts: “С 2014 года”, “Постоплата”, “10 рабочих дней” (wording must match current context).

RIGHT:
Replace the generic upload card with a dominant “Маршрут заявки” / STS conversion panel.
Top row: label “Первичная проверка” + a non-live neutral state such as “Старт с СТС”. Never say “проверено”, “одобрено”, “онлайн”, or imply a submitted application.
Main zone: existing accessible file input, but visually designed as a premium document slot. Keep JPG/PNG/PDF and 10 MB text.
Below it: a static 3-step explanatory route:
01 СТС → 02 Проверка → 03 Подача
This is an explanation, not current live status.
Then messenger actions.
Explicit local-file privacy sentence remains visible.

Hero background: subtle structural geometry/route line/grid is allowed, but low contrast and CSS-only. Remove the giant pale “ROUTE” word currently behind H1.
Hero and right panel must form one deliberate composition.

Mobile <=700:
single column; offer first, CTA immediately visible, STS panel second. No horizontal overflow. H1 should not consume entire first screen. Trust/proof rail can horizontally scroll only if keyboard/touch safe; prefer wrapped compact chips.

## 5. Trust/status band
Do not keep the current four plain columns with blue vertical bars.
Create a single integrated proof/status rail after hero: 4 compact cells/cards with existing facts:
- С 2014 года
- Постоплата
- Парк 100 авто
- −10% от 4 автомобилей
Use small labels and strong values. On mobile 2x2. No fake icons/badges.

## 6. Pricing
The pricing section must be compositionally new.
Desktop: featured annual plan approximately 60% width and temporary plan 40%.
Featured annual card: dark ink/graphite surface OR strongly differentiated blue-accent surface; “8 000 ₽” dominant; current inclusions; CTA.
Temporary: light card, “1 500 ₽”, current terms.
Fleet 4+ offer should be integrated as a compact strip connected visually to pricing, not a generic black banner.
Mobile: annual first, both full width, CTA full width.
No new prices/discounts.

## 7. Process
Replace the generic six equal cards look with a route/timeline.
Desktop: horizontal route line with six numbered stops; alternating/compact explanatory blocks are acceptable.
Mobile: either vertical timeline (preferred) or deliberate swipe track with visible affordance. If swipe is used, keyboard focus and reduced motion must remain.
Use current six stages and wording; no live status implication.

## 8. Support / guarantees
Keep current three factual cases but visually reduce “three generic cards”.
Use a dark full-width band with one strong heading and three structured rows/columns separated by subtle lines.
Functional green may mark positive service actions, not decorative status.

## 9. Fleet
Keep two-column desktop composition but make it look like a B2B operations module.
Left: heading, current facts, CTA.
Right: static “пример этапов оформления” route board. Clearly retain “пример”; never look like a real client dashboard.
Mobile stacks cleanly.

## 10. Documents
Make “СТС” the dominant document card. Secondary documents become a clean checklist/tag matrix.
No fake document previews containing personal data.
CTA returns to #check.

## 11. Proof
Only current factual proof. Do not create testimonials.
Use two editorial proof blocks or one asymmetric evidence panel. Remove generic hover-card feel.
If owner confirmation later changes claims, content can be updated separately; this design pass must not fabricate anything.

## 12. FAQ
Keep native details/summary and six current questions.
Visually: left intro/contact module, right accordion. Strong open/focus states. No custom JS accordion.
Mobile: intro, contact CTA, then FAQ.

## 13. Final CTA
Must not be the current generic blue gradient strip.
Create a high-contrast closing module, preferably ink/dark surface with a contained blue action area or blue CTA. Copy remains: primary check starts from one STS; no registration/long form. One primary CTA #check and optional existing messenger secondary.
No giant decorative /MSK text that competes with content.

## 14. Footer
Keep navigation/contact/legal-pending facts. Tighten hierarchy. Do not add legal claims. Draft/legal pending remains until owner data is implemented.

## 15. Interaction states
Buttons: hover, active, focus-visible.
STS: idle; valid local file selected; invalid file. Existing JS semantics remain. Selected file is NOT “uploaded”.
FAQ native open.
Respect prefers-reduced-motion.
No animation required for acceptance.

## 16. Responsive acceptance
Explicitly inspect 375, 768, 1024, 1440.
At 375: no horizontal document overflow; header CTA visible; primary hero CTA visible without hunting; STS control usable; messenger actions full-width or cleanly wrapped.
At 768: no awkward desktop leftovers.
At 1024/1440: hero composition clearly 2-column and intentional.
Touch targets >=44px.

## 17. Technical constraints
Work only on PR #1 branch. Never main, never merge.
Fetch fresh SHA before every write.
Do not replace working JS/SEO wholesale. Prefer CSS + targeted markup changes.
Preserve IDs referenced by JS/tests: especially #stsFile, #uploadTitle, #uploadStatus, #check and existing section anchors.
Do not change analytics goal semantics. sts_upload_start only after valid file selection; no sts_upload_success.
Run repository checks after implementation.
Do not claim visual/device checks that were not actually performed.

## 18. Definition of Done
Implementation is DONE only when ALL are true:
1. Hero markup/composition materially changed, not just CSS polish.
2. Mobile header retains conversion CTA.
3. STS block is redesigned as the dominant application-route panel while preserving privacy semantics.
4. Trust rail, pricing, process and final CTA are compositionally redesigned.
5. Current content/SEO/analytics/FAQ behavior preserved.
6. Repo checks pass.
7. Diff reviewed for broken anchors/resources and false success/status language.
8. Agent reports exact commit SHA(s), files changed, checks run and unresolved blockers.
9. Before→after summary names structural changes, not adjectives.
10. If the result still resembles the old layout at first glance, it FAILS and must not be reported as complete.

## 19. Implementation order
Batch A: header + hero + STS + trust rail.
Run checks.
Batch B: pricing + process.
Run checks.
Batch C: support + fleet + documents + proof + FAQ + final CTA/footer polish.
Run checks and diff review.
Do not spend a run only writing recommendations: implement the next safe batch.

This file is the binding visual implementation contract for ROUTEMSK v1.0.