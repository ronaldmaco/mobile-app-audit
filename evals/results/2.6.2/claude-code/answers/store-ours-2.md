I'd fix four things before submitting. Each one alone is likely to get the app rejected. I reviewed all 13 files in the repo; it doesn't include the Xcode project, so I couldn't build or run it, and nothing was tested on a device or against the backend. Everything below comes from the code. The guideline numbers and wording are from memory and from a rules list dated 2026-11-17, so check them against Apple's current pages.

## Fix first: likely rejection

**1. Scanning a book code will crash the app.** `ScannerView.swift:15` opens the camera, but `Info.plist` has no `NSCameraUsageDescription`. Without that entry iOS kills the app as soon as it touches the camera. Reviewers tap every button, so this is almost certain to be found.
- **Fix:** add the key with a specific sentence, e.g. "Scan the code printed in your StoryTime book to unlock its audio story."

**2. The reviewer can't sign in.** `docs/review-notes.md` says sign-in needs an SMS code that your support team forwards by hand, 9–17 CET only. Review often happens outside those hours. Apple requires a demo account that works without help (2.1), and a failed sign-in means nothing else gets reviewed.
- **Fix:** have the server skip the SMS step for the demo account, or give it a fixed code and put that code in the notes.

**3. There's no way to delete an account.** The app lets people create accounts (`WelcomeView.swift:18` → `POST /accounts`), but Settings only has "Sign out" (`SettingsView.swift:9`). Apple requires account deletion inside the app (5.1.1(v)), and disabling the account doesn't count.
- **Fix:** add a "Delete account" option in Settings, backed by a server call that actually removes the data.

**4. Unlocking stories with a QR code may break the in-app purchase rule (3.1.1).** As I remember it, the guideline says apps can't use their own unlock methods "such as license keys, augmented reality markers, QR codes". The scanner unlocks digital audio (`API.swift:19`).
- **Decide before submitting:** either explain in the review notes that the code comes with a printed book the customer already bought, or be ready for this to be challenged. Read the current 3.1.1 and 3.1.3 wording first.

## Fix before submitting: often rejected

**5. The paywall (`PaywallView.swift`):**
- It shows "Only $4.99!" but never says the price is per month. Subscription screens must state what you get, for how long and at what price (3.1.2).
- It has no links to the Terms of Use or the privacy policy.
- There's no **Restore Purchases** button. Add one that calls `AppStore.sync()`.
- If the product doesn't load, it shows a made-up "$4.99" (line 11), and "Start now" then does nothing and closes the sheet.
- "Start now" also closes the paywall even when the purchase fails or is cancelled (line 13), with no message.

**6. Unfinished content:** "Bedtime tips" is Lorem ipsum, "Contact us" says "Coming soon", and the stories in the Library are plain text that can't be opened. "Open the full library" does nothing for subscribers. Reviewers reject placeholder content (2.1). It's possible playback just wasn't included in this export; if so, ignore the last part.

**7. Privacy declarations:**
- `PrivacyInfo.xcprivacy` declares no data collected. The app collects email, password, the child's first name, purchases, and a phone number on the server. Make the privacy manifest and the App Store Connect privacy label match.
- The app uses `UserDefaults` (`SessionStore.swift`), which Apple requires you to give a reason for. The manifest doesn't declare it; add the `NSPrivacyAccessedAPICategoryUserDefaults` entry with reason `CA92.1`.
- There's no privacy policy link in the app or in the notes. You need one, and for an app aimed at parents that collects a child's name, the reviewer will look closely. If you plan to list it in the Kids category, the stricter Kids rules apply too.

**8. The microphone permission is for a feature that isn't in the code.** `NSMicrophoneUsageDescription` mentions recording your voice, but nothing in the code uses the microphone. Remove the entry, or make sure the feature is in the build.

## Lower priority: bugs a reviewer may hit

- **Errors are silently ignored.**
  - "Sign in" moves to the code screen even if the login request failed (`WelcomeView.swift:23`).
  - Failed account creation and failed code checks show nothing.
  - The scanner ignores whether the unlock worked and just closes (`ScannerView.swift:31`).
  - If camera access is denied, the scanner is a blank screen.
- **Server address:** `API.swift:5` points at `https://api.example.com/v2`. If that isn't just a stand-in for this export, no network feature works.
- **Security:** the login token is stored in `UserDefaults`; it belongs in the Keychain. This won't cause a rejection.
- **Small things:**
  - "Version 1.0 (14)" is typed into Settings by hand and will go out of date.
  - The app doesn't listen for `Transaction.updates`, so renewals and refunds only show up after the next launch.

## Before you press submit
- Check that build 14 is higher than any build already in TestFlight for 1.0.
- Make sure the backend is reachable from the US.
- Check that your screenshots show the app in use, not just the login screen.

Items 1, 2, 3, 5, 7 and 8 are small code or config changes I can make now; tell me which ones. Item 2 also needs a server change, and item 4 is a business decision for you.
