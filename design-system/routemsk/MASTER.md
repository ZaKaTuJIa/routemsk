# ROUTEMSK Design System v1

Source method: UI/UX Pro Max v2 workflow. Scope: conversion-focused Moscow truck permit service landing page.

## Product pattern
Hero-centric service landing + trust + transparent pricing + process + documents + FAQ + repeated conversion CTA.

## Visual direction
Professional service / transport / government-adjacent. Clean, restrained, high-trust. Avoid decorative glassmorphism, neon/AI gradients, excessive animation, fake badges and visual noise.

## Conversion hierarchy
1. H1: what service is provided and service area.
2. Price/payment proposition.
3. One primary action: start with STS / contact.
4. Explicitly explain that file selection is local until real server upload exists.
5. Trust evidence and process.
6. Pricing.
7. Documents / FAQ.
8. Repeated CTA.

## Tokens
Keep the existing ROUTEMSK brand palette unless contrast testing requires adjustment. One primary accent for actionable controls; neutral surfaces; high-contrast body text.
Use system font stack for performance. Strong H1/H2 hierarchy, readable 16px+ body copy, compact supporting copy.
Spacing should follow an 8px rhythm. Minimum touch target 44x44px.

## Components
Buttons: one dominant primary style, one secondary style; consistent radius/padding/focus.
Cards: consistent padding, radius, border/shadow language.
Trust items: factual evidence only; no invented ratings/reviews/requisites.
FAQ: native details/summary.
STS selector: never visually imply successful upload before server confirmation.

## Responsive
Required review widths: 375, 768, 1024, 1440 px.
No horizontal document overflow. Text must reflow without clipping. CTA and STS controls must remain usable one-handed on mobile. Horizontal process region must remain keyboard/touch operable.

## Accessibility
Visible keyboard focus. WCAG AA text contrast target >= 4.5:1. Respect prefers-reduced-motion. Native semantics before custom JS. Meaning must not rely on color alone.

## Analytics/CRO
Clicks are clicks, not leads. sts_upload_start must not fire merely because the OS file picker opened; cancellation is not an upload start. sts_upload_success is forbidden until real server-confirmed delivery exists.

## Release gate
Before merge: repository checks pass; no broken anchors/resources; responsive review; fresh HTTPS/HTTP/www health check; owner confirms commercial/legal claims; real-device smoke check; Metrica/Webmaster owner-only checks recorded separately.

## Implementation rule
No full-file blind rewrites. Apply small reviewable diffs, run checks after each change, then QA before the next batch.
