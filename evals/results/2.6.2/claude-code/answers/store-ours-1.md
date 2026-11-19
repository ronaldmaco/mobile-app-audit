I reviewed StoryTime against the App Store rules in my checklist, which were last read on Apple's official pages on 2026-11-17. This was a code-only review: I didn't build the app or run it on a device, and I couldn't test your server. Below, **Verified** means the problem is plainly in the code or docs. **Risk** means it depends on how a reviewer reads a guideline.

## Fix before submitting (each one is likely to cause a rejection)

**1. The book scanner will crash the app (Verified).**
`Info.plist` has no `NSCameraUsageDescription`, but `ScannerView.swift:15` opens the camera. Without that entry iOS kills the app as soon as the camera is used, so "Scan a book code" crashes every time. That fails guideline 2.1 (the app must work) and 5.1.1 (a purpose string is required).
→ Add a camera purpose string, for example "Scan the code printed in your StoryTime book to unlock its audio story."

**2. The reviewer can't sign in (Verified, `docs/review-notes.md:5-6`).**
Signing in needs an SMS code, and your support team forwards it by hand between 9 and 17 CET. Reviewers often work in US time zones and won't wait. That fails guideline 2.1.
→ Turn off the SMS step for the demo account on the server (or give it a fixed code) and say so in the review notes. Make sure the account works from a US network.

**3. There is no way to delete an account (Verified).**
People can create an account in the app (`WelcomeView.swift:18`), but Settings only has "Sign out". Guideline 5.1.1(v) requires that deletion can be started in the app and actually removes the account from the server; disabling it isn't enough.
→ Add "Delete account" in Settings, backed by a server call that really deletes the account and its data.

**4. There is no "Restore purchases" button (Verified).**
`SubscriptionStore` has no restore option and the paywall has no button for it, which guideline 3.1.1 requires.
→ Add a Restore button that calls `AppStore.sync()` and then `refresh()`.

**5. The paywall doesn't say enough about the subscription (Verified, `PaywallView.swift:11`).**
- It says "Only $4.99!" with no billing period. Guideline 3.1.2 expects the price and what the user gets to be clear.
- If the product doesn't load, it shows a hard-coded "$4.99", which can be the wrong price or the wrong currency.
- There are no Terms of Use or Privacy Policy links. The app has no privacy policy link anywhere, which 5.1.1(i) requires.

→ Show `displayPrice` plus "/ month", don't show a price at all if the product failed to load, and add Terms and Privacy links on the paywall and in Settings.

**6. The privacy manifest is wrong (Verified, `PrivacyInfo.xcprivacy`).**
- The app uses `UserDefaults` (`SessionStore.swift:6`), which is on Apple's list of APIs that need a declared reason. `NSPrivacyAccessedAPITypes` is empty, so the upload will be flagged.
  → Declare `NSPrivacyAccessedAPICategoryUserDefaults` with reason `CA92.1`.
- `NSPrivacyCollectedDataTypes` is empty, but the app sends an email address, a phone-linked login and the child's first name to your server.
  → List them in the manifest and make sure the App Privacy answers in App Store Connect match.

**7. Unfinished and broken screens (Verified).**
Reviewers reject unfinished apps under guideline 2.1.
- "Bedtime tips" is lorem ipsum, and "Contact us" says "Coming soon" (`SettingsView.swift:12-13`).
  → Replace both with real content and a real contact (email or link).
- "Open the full library" does nothing for subscribers (`LibraryView.swift:16`).
- The free stories are plain text and can't be opened (`LibraryView.swift:12-13`).
- A successful scan unlocks the story on the server, but the result is thrown away, so nothing happens on screen (`ScannerView.swift:31`).

If these screens are wired up in code that isn't in this export, check them on the build you're submitting.

## Things that may draw questions

**8. Unlocking audio with a QR code (Risk).**
Guideline 3.1.1 names QR codes among the ways apps may not use to unlock content. Your case is companion audio for a printed book the customer bought, which reviewers sometimes accept.
→ Explain this in the review notes, with a sample code and a photo of the book page.

**9. Microphone permission with no matching feature (Risk).**
`NSMicrophoneUsageDescription` describes recording your own voice, but nothing in the code uses the microphone.
→ Remove the entry until that feature ships.

**10. Errors are silently ignored (Verified).**
Every `try?` in the sign-in, sign-up, verify, scan and purchase flows hides failures:
- A wrong password still moves the user to the "Code from SMS" screen (`WelcomeView.swift:23`).
- The paywall closes even when the purchase fails.

A reviewer who runs into either will see an app that seems broken.
→ Show a simple error message instead.

**11. The child's name.**
If you put the app in the Kids category, the stricter Kids rules apply (1.3 and 5.1.4). As a parents' app, the main thing is that the privacy label discloses that you collect the child's name.

Not a rejection reason, but worth fixing: the login token is stored in `UserDefaults` rather than the Keychain, and the subscription status never resets to "not subscribed" when a subscription lapses.

## What I couldn't check
- That your server is reachable from the US.
- That `plus.monthly` is set up and approved in App Store Connect.
- The build number compared with what's already in TestFlight.
- The Xcode/SDK version used for the upload.
- Your store screenshots.

## Suggested order
1. Do 2 and 3 first: the demo account login and account deletion need server work.
2. Then the quick fixes: 1, 6 and 9.
3. Then the visible app changes: 4, 5, 7 and 10, plus the review note for 8.

I haven't changed any files. Should I start on the app-side fixes (1, 4, 5, 6, 7, 9, 10)?
