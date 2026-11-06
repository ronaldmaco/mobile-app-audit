# Case 007: endless spinners, 4xx treated as success, late images in reused cells

**Stack:** native iOS (UIKit). Two sibling apps. **Mode:** D (full audit).

> Real case, anonymised.

## Report
None. Found during a full audit of loading, error and image code.

## What happened
1. Several list screens and onboarding only printed failures, so the spinner never stopped. One loader never called its completion.
2. Requests with a non-2xx status were treated as success when the error body happened to decode into the model (fixed in one sibling app, missing in the other).
3. Images were downloaded with `Data(contentsOf:)` on background threads, cached only in memory, and assigned when they arrived. A reused cell could show an image meant for another item.

## How to prove it
- Offline and slow-network runs of every list (device conditions table): the spinner never stops.
- Diff of the two apps' network layers (sibling check): the status check exists in only one.
- Fast scrolling on a slow network: a cell shows an image for another item.

## Fix
- Every failure reaches the UI: friendly error with Try again; cancelled requests ignored.
- Non-2xx is an error regardless of the body.
- `URLSession` with a disk `URLCache`; a late answer for a URL the view no longer waits for is dropped; offline falls back to the disk copy.

## Skill coverage
| Gap | Added in 2.2 |
|---|---|
| "Every failure reaches the UI" existed and caught (1) | none |
| Status code vs decodable body not explicit | Networking: "non-2xx is an error even if the body decodes" |
| Cell reuse race not in the checklist | Pattern "late async result applied to a reused view" |
