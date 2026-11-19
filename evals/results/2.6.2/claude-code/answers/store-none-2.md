I read all 13 files. Nothing is built or changed yet. Seven issues will almost certainly get StoryTime rejected or blocked at upload, and a few more are risks. The Xcode project isn't in this export, so I couldn't check build settings, entitlements, the App Store Connect metadata, or how the subscription is set up there.

## Fix first: certain rejection or blocked upload

**1. The scanner crashes the app.** The app opens the camera in `ScannerView.swift:15`, but `Info.plist` has no `NSCameraUsageDescription`. iOS kills any app that opens the camera without one, so the reviewer's first tap on "Scan a book code" crashes it. Meanwhile `NSMicrophoneUsageDescription` describes a voice-recording feature that doesn't exist in the code. Add the camera text and remove the microphone one, or Apple will ask what the microphone is for.

**2. The reviewer can't sign in.** In `docs/review-notes.md`, the demo account needs an SMS code that your support team forwards by hand, 9–17 CET. Review runs around the clock, mostly on US time, so this will come back as "unable to sign in." Let the demo account skip the SMS step on the server, or give it a fixed code and put that code in the notes.

**3. There's no way to delete an account (guideline 5.1.1(v)).** The app lets people create accounts, so it must also let them delete them from inside the app. `SettingsView.swift` only has "Sign out." Add "Delete account" with a confirmation, backed by a server endpoint that actually deletes the data.

**4. The paywall is missing what Apple requires (3.1.1 / 3.1.2).** In `PaywallView.swift`:
- There's no "Restore Purchases" button. Add one that calls `AppStore.sync()`.
- It doesn't say the billing period or that it renews automatically. "Only $4.99!" should say "$4.99/month, renews automatically until cancelled."
- There are no links to your Terms of Use and Privacy Policy. These are needed on the paywall and in the App Store Connect metadata.
- If the price fails to load, it shows a hardcoded `"$4.99"`. Show a loading state or disable the button instead, because the real price differs by country.

**5. Buying the subscription unlocks nothing (2.1, incomplete app).** Once a user subscribes, `LibraryView.swift:16` shows "Open the full library," but tapping it does nothing. The free stories are plain text rows that don't open or play either. The reviewer will test the purchase in the sandbox, so the 300+ stories have to be reachable. If they aren't, the "300+ stories" claim is also misleading.

**6. There's placeholder content (2.1).** "Bedtime tips" is Lorem ipsum and "Contact us" says "Coming soon" (`SettingsView.swift:12-13`). Put in real content or remove them. You also need working support contact details, and an in-app link to your privacy policy is expected.

**7. The privacy manifest is incomplete.** `PrivacyInfo.xcprivacy` declares no API usage, but the app uses `UserDefaults`, which Apple requires you to declare (category `NSPrivacyAccessedAPICategoryUserDefaults`, reason `CA92.1`). Apple's upload checks flag a missing declaration, which can block the build. The manifest also says no data is collected, but the app collects email, a child's first name, a phone number (for the SMS code) and purchases. List those in the manifest, and make your App Store privacy labels match.

## Next: likely flags

- **Login before free content (5.1.1).** The two free stories are behind a mandatory account. Apple pushes back when an account isn't needed for the core feature. Let people browse and play the free stories first, and ask for an account at purchase or when they scan a code.
- **QR codes unlocking content (3.1.1).** The guideline specifically forbids unlocking features with "QR codes" and similar mechanisms instead of in-app purchase. Codes from printed books that unlock companion audio are sometimes accepted, but it's a real risk. Explain it in the review notes ("audio companion included with the physical book; not sold in-app"), and make sure a code can never unlock paid Plus content.
- **Kids category / children's data.** You collect a child's name. If you pick the Kids category, much stricter rules apply (parental gates, no third-party analytics). Even outside it, the child's name needs to be justified in your privacy policy.
- **Errors fail silently.** Every login call uses `try?` (`WelcomeView.swift:18-23`). "Sign in" moves to the code screen even when the password is wrong, and "Create account" does nothing on failure. Reviewers report that kind of dead end as a bug.
- **Scanner dead ends.** It has no close button, and if camera permission is denied the screen stays blank. After a scan it closes with no message saying whether anything was unlocked.

## Not rejection issues, but worth fixing
- The access token is stored in `UserDefaults` instead of the Keychain.
- `SubscriptionStore` never listens to `Transaction.updates`, so renewals, refunds and purchases from another device aren't picked up. `isPlus` is also never set back to false when a subscription expires.

**Suggested order:** 1 → 2 → 3 → 4 → 5, since each of these alone guarantees a rejection. Then 6 and 7, then move the free stories in front of the login. Most of these are code fixes I can make. Item 2 also needs a server change, and the subscription terms and privacy policy URL need setting up in App Store Connect.
