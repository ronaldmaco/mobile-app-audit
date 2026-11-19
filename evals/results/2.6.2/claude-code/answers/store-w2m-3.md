I read every file in the repo. I found several problems that will get StoryTime rejected, and two of them will show up the moment the reviewer taps around.

## Fix first: these will get it rejected

1. **The scanner crashes the app.** The Info.plist has no camera permission text (`NSCameraUsageDescription`), and iOS closes any app that opens the camera without it. "Scan a book code" (`ScannerView.swift:15`) will crash during review, and Apple rejects for crashes (guideline 2.1). Add the key. Also handle a denied permission: right now the scanner just shows a blank screen.

2. **The reviewer can't sign in.** `docs/review-notes.md` says the SMS code is forwarded by hand, 9–17 CET. Reviews happen at any hour, mostly in US time zones, so expect a "can't access the app" rejection. Give the demo account a fixed code or let it skip SMS.

3. **There's no way to delete an account.** The app lets people create accounts, so Apple requires a way to delete one inside the app (5.1.1(v)). Settings only has "Sign out". Add "Delete account" backed by a real server endpoint.

4. **The paywall is missing what Apple requires for subscriptions** (`PaywallView.swift`, guidelines 3.1.1 and 3.1.2):
   - There's no "Restore Purchases" button.
   - "Only $4.99!" doesn't say how often it's charged. It needs to say "per month", and it needs links to your Terms of Use and Privacy Policy.
   - If the product fails to load, the screen still shows a made-up `$4.99` and "Start now" does nothing (`SubscriptionStore.swift:14`). A reviewer will see that as a broken purchase.

5. **Upload will be flagged for the privacy manifest.** The app saves to `UserDefaults`, and Apple requires a reason to be declared for that. `PrivacyInfo.xcprivacy` declares none, so you'll get an ITMS-91053 email when you upload. Add `NSPrivacyAccessedAPICategoryUserDefaults` with reason `CA92.1`. The manifest also says no data is collected, but the app collects an email, a password, a child's first name, a phone number (for the SMS) and purchases. Fix the manifest and make the privacy answers in App Store Connect match.

6. **Placeholder content.** "Bedtime tips" is Lorem ipsum and "Contact us" says "Coming soon" (`SettingsView.swift:12-13`). Apple rejects for both (2.1 and 1.5 require working support contact info). There's also no privacy policy link inside the app, which 5.1.1(i) requires.

## Very likely to be flagged

7. **The app has almost nothing to do.** The free stories are plain text with no playback, and "Open the full library" does nothing for subscribers. If the shipping build really looks like this, expect a 2.1/4.2 rejection after the reviewer buys in the sandbox. If the real content is in code that isn't in this repo, ignore this one.

8. **Unlocking content with QR codes.** Guideline 3.1.1 names QR codes as a way you may not unlock content. Unlocking audio for a printed book the customer already bought is a gray area and is often allowed. Describe it that way in the review notes and include a sample QR code so the reviewer can test it.

9. **The microphone permission isn't used.** `NSMicrophoneUsageDescription` promises voice recording, but no recording feature exists. Remove it until the feature ships.

10. **Errors are silently ignored.** Every `try?` in `WelcomeView` and the scanner hides failures. Sign-in moves on to the code screen even when the request failed (`WelcomeView.swift:23`), and a scanned code gives no result. To a reviewer that looks broken, so show error and success messages.

## Worth checking

- **App Store category:** if you plan to list it in the Kids category, you'll also need a parent check before the purchase and before outside links.
- **Login wall:** the free stories sit behind sign-in, and Apple sometimes objects to requiring an account for content that doesn't need one (5.1.1(v)).
- **iPad:** if the app is set to run on iPad, portrait-only can fail upload unless full-screen mode is required. I couldn't confirm because the Xcode project isn't in this export.
- **Not rejection issues, but should be fixed:**
  - The login token is stored in `UserDefaults`; it should go in the Keychain.
  - `isPlus` is never set back to false, and the app doesn't listen for subscription changes, so renewals, refunds and expired subscriptions aren't picked up.

**Suggested order:** 1 and 2 (crash and sign-in), then 3, 4 and 5, then 6. Want me to make the code and plist changes for 1, 3, 4, 5, 6 and 9? Items 2 and 3 also need work on your server.
