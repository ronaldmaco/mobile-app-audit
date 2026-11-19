I read every file in the export. I found several issues that would get StoryTime rejected or crash it during review, so it isn't ready to submit yet. They're listed in the order I'd fix them.

## Will be rejected or crash: fix before submitting

1. **The scanner will crash the app.** `ScannerView.swift` turns on the camera, but `Info.plist` has no `NSCameraUsageDescription`. iOS closes any app that does this, so the reviewer's first tap on "Scan a book code" crashes StoryTime. Add a line explaining the camera use, e.g. "Scan the code inside your StoryTime book to unlock its audio."

2. **Reviewers probably can't sign in.** Under `docs/review-notes.md:5-6`, sign-in needs an SMS code that your support team forwards by hand, 9–17 CET only. App Review works across many time zones and won't wait for a person to reply, so this usually ends in a rejection for "unable to sign in." Have the server skip SMS for the demo account, or give it a fixed code and put that code in the notes. Also check that `reviewer@example.com` and `https://api.example.com/v2` (`API.swift:5`) are placeholders in this export only. If the build actually points at example.com, nothing in the app works.

3. **There's no way to delete an account (guideline 5.1.1(v)).** People can create an account in `WelcomeView.swift:18`, so Apple requires a way to delete it from inside the app. Settings only has "Sign out." Add "Delete account," backed by a server endpoint that really deletes the data.

4. **The paywall is missing things Apple requires for subscriptions (3.1.1 and 3.1.2).**
   - There's no **Restore Purchases** button. Add one that calls `AppStore.sync()`.
   - It never says the plan **renews monthly**, and it has no links to the **Privacy Policy** or **Terms of Use (EULA)**. All of these must appear on the paywall.
   - `PaywallView.swift:11` shows a hard-coded "$4.99" if the price fails to load. Never show a price that didn't come from StoreKit, and disable "Start now" while there's no product.
   - Subscribers can't do anything with the library: `LibraryView.swift:16` sets `showPaywall = false` and the button goes nowhere, so a reviewer who subscribes gets nothing. That reads as an incomplete feature (2.1).

5. **Unfinished content.** Settings has "Lorem ipsum" in "Bedtime tips" and "Coming soon" in "Contact us" (`SettingsView.swift:12-13`). The free stories are plain text rows with no playback. Placeholder content and features that don't work are a common 2.1 rejection. Fill these in or remove them, and add a working support contact.

6. **The privacy manifest is incomplete.** `PrivacyInfo.xcprivacy` says the app collects no data and uses no required-reason APIs. But:
   - The app uses `UserDefaults`, so it must declare `NSPrivacyAccessedAPICategoryUserDefaults` (reason `CA92.1`). Without it, App Store Connect sends a missing-reason warning (ITMS-91053) or blocks the upload.
   - The app collects email, a child's name, a phone number (for SMS) and purchases. List these in the manifest, and make sure your App Store privacy answers match.
   - Add a **Privacy Policy link inside the app** (Settings) as well as in App Store Connect. This matters even more because the app collects a child's name.

## High risk: decide before submitting

7. **Unlocking stories with a QR code (3.1.1).** Apple's rules specifically forbid unlocking content with QR codes or similar. Companion audio for a physical book you sold is sometimes accepted, but only if you explain it. In the review notes, say the codes come only in printed books bought outside the app, and give the reviewer a sample code or image to scan.

8. **Microphone permission for a feature that isn't there.** `NSMicrophoneUsageDescription` promises "Record your own voice…", but no code records anything. Remove it until the feature ships, or reviewers may flag it as misleading.

9. **Failures happen silently, which reviewers notice.** Every `try?` in `WelcomeView` throws the error away. "Sign in" switches to the code screen even if the request failed, and a wrong code just does nothing. The scanner has no Close button, shows a black screen if camera access is denied, and closes without saying whether the unlock worked. Show error messages and a denied-permission state.

10. **If you choose the Kids category,** you'll also need a parental gate in front of the purchase and any outside links (1.3). Since the README says the app is for parents, the simpler route is to stay out of the Kids category.

## Worth fixing, but they won't block review
- The login token is stored in `UserDefaults`. Move it to the Keychain.
- `SubscriptionStore` never listens to `Transaction.updates` and never sets `isPlus` back to false. Renewals, refunds, family approvals ("Ask to Buy") and expired subscriptions won't be reflected.
- The demo account password is committed in `docs/review-notes.md`.

**Suggested order:** do 1, 2 and 3 first. They're quick, and each one alone gets the build rejected. Then 4 and 6 (paywall and privacy), then 5 and 7. I haven't changed anything. I can start on any of these if you want, and the camera key, privacy manifest and Restore button are each only a few minutes' work.
