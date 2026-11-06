# Store readiness and production telemetry

## Production telemetry
When available (ask for access or exports): Crashlytics/Sentry/Xcode Organizer/Play Console vitals.
- Top crashes and ANRs by users affected, by version and OS; map each to code (symbolicated).
- Error spikes after a release: compare versions.
- Backend logs/APM for 4xx/5xx by endpoint and app version around the reported time.
Production issues become findings with real user impact numbers.

## Apple App Store
- 2.1 App Completeness: reviewers (mostly US) can sign up, sign in with the demo account and reach real content. Test from a US IP; check every host the app uses.
- Demo account works, not expired, in the intended state.
- 5.1.1(v): in-app account deletion that actually deletes.
- No placeholder content, broken links, or hardware-dependent features without Review Notes.
- Current SDK requirements: required Xcode/SDK, UIScene lifecycle where required, no removed APIs, privacy manifest and required-reason APIs, purpose strings, ATT if tracking.
- Sign in with Apple (or an equivalent) when other third-party logins are offered, per guideline 4.8.
- Build and launch with the **exact toolchain used for submission** (Xcode/SDK version) on the newest OS simulator before submitting; new SDKs can make old lifecycles fail at launch (`cases/004`).
- Marketing version matches App Store Connect; build number higher than any build already uploaded for that version (check TestFlight).
- Reply to a rejection in the submission thread with cause and fix; a server-side fix needs no new build.

## Google Play
- Target API level meets the current requirement; Data safety matches SDKs; account deletion in-app and via web link.
- Works for reviewers' region; test account in App access.
- Declared permissions are used; background location, exact alarms, foreground service types justified.
- versionCode higher than any uploaded build.

Verify current store rules on the official guideline pages when a rule matters; they change yearly.
