# App Store review check: StoryTime

**Verdict: high risk.** Submitting this build as it is would very likely get it rejected. Several of the problems below would each be enough on their own.

The project has only 13 files, so I read every one myself and checked each finding against the rule text in the October 16, 2026 guidelines. I didn't inspect a built app file, because there isn't one in the repo.

## Fix these first, in this order

**1. Scanning a book code will crash the app (Guideline 2.1(a), 5.1.1).**
- `ScannerView.swift:15-16` turns on the camera, but `Info.plist` has no camera permission text (`NSCameraUsageDescription`).
- iOS closes any app that opens the camera without that text, so the reviewer will hit a crash as soon as they tap "Scan a book code".
- **Fix:** add something like "StoryTime uses the camera to scan the code printed inside your StoryTime book." Also show a message when camera access is denied; right now `ScannerView.swift:16` just returns and leaves a blank screen.

**2. The reviewer probably can't sign in (Guideline 2.1, "Before You Submit").**
- Your review notes (`docs/review-notes.md:5-6`) say the sign-in code is sent by SMS to your support phone and forwarded "during office hours (9-17 CET)". Reviewers work around the clock and won't wait for a person to send them a code.
- Apple requires either "an active demo account or fully-featured demo mode".
- Sign-in errors are also hidden (`WelcomeView.swift:18,21,23` use `try?`). The "Sign in" button moves to the code screen even when the password was wrong, so a failed login shows nothing.
- **Fix:** let the demo account skip SMS or accept a fixed code, and show error messages.

**3. The subscription can't be restored, isn't described properly, and doesn't unlock anything (Guidelines 3.1.1, 3.1.2(c), 2.1(b)).**
- `PaywallView.swift` has no "Restore Purchases" button. 3.1.1 requires a way to restore purchases.
- It doesn't say "per month" or that the subscription renews automatically.
- It has no links to your Terms of Use or Privacy Policy.
- It falls back to a hard-coded "$4.99" price (`PaywallView.swift:11`), which may not match the reviewer's country.
- After buying, "Open the full library" (`LibraryView.swift:16`) does nothing, and the stories are only text labels that can't be played. A reviewer who buys the subscription gets nothing, which is a 2.1(b) rejection.
- **Fix:**
  - Add a restore button that calls `AppStore.sync()`.
  - Show "$X.XX / month, renews automatically until cancelled".
  - Add the Terms and Privacy links.
  - Hide the buy button until the price has loaded.
  - Make the paid library actually open.

**4. Users can't delete their account (Guideline 5.1.1(v)).**
- People can create accounts (`API.swift:7`, `WelcomeView.swift:18`), but Settings only has "Sign out" (`SettingsView.swift:9`). I searched for "delete" and found nothing.
- **Fix:** add a "Delete account" option in Settings and a matching delete call on your server.

**5. There's no privacy policy in the app, and the privacy file lists nothing (Guidelines 5.1.1(i), 5.1.4; Apple upload error ITMS-91053).**
- The app has no privacy policy link anywhere. That's required for every app, and doubly so here because you collect a child's name (`API.swift:8`).
- `PrivacyInfo.xcprivacy` (Apple's privacy declaration file) lists no collected data, but the app collects an email address, a child's name, and a phone number tied to the account.
- That file also doesn't declare the app's use of `UserDefaults`, so Apple's upload check can reject the build. It needs the entry `NSPrivacyAccessedAPICategoryUserDefaults` with reason `CA92.1`.
- **Fix:** add a privacy policy link in Settings and next to the subscription button, and fill in the privacy file.

**6. Placeholder content is still in the app (Guideline 2.1(a)).**
- "Bedtime tips" shows "Lorem ipsum…" and "Contact us" shows "Coming soon" (`SettingsView.swift:12-13`).
- **Fix:** replace both with real content. Contact us should have a real support email or link.

## Other risks to look at

- **Unlocking audio with the book's QR code (3.1.1), medium risk.** The rule says apps "may not use their own mechanisms to unlock content or functionality, such as … QR codes". Reviewers sometimes accept this for books people have already bought, but it isn't guaranteed. Explain it in the review notes and include a sample code, or be ready to defend it.
- **Login is required just to read the free stories (5.1.1(v)).** If the account isn't essential, let people browse the free stories without signing in.
- **The microphone permission text is for a feature that doesn't exist.** `Info.plist:12-13` promises voice recording, but the app never uses the microphone. Remove that entry.
- **Minor:** the login token is saved in plain settings storage (`SessionStore.swift:14`). The Keychain would be safer.

## To check in App Store Connect (can't be seen in the code)

- [ ] The privacy policy link is filled in, and the privacy answers include email, name, phone and the child's data.
- [ ] Age rating and category are set. If you choose the Kids category, guideline 1.3 applies: purchases and outside links must sit behind a parent check. If you don't, the app name, screenshots and description can't use words like "For Kids" (2.3.8).
- [ ] The `plus.monthly` subscription is set up, attached to this version, and has its own review screenshot.
- [ ] The review notes include a sample book QR code (Apple asks for "a sample QR code") and explain the unlock.
- [ ] After archiving, run `/appstore-review --build path/to/StoryTime.ipa` to check the built app for upload errors.

This only checks the code against Apple's public guidelines, so it can't guarantee approval. The camera fix, the privacy file and removing the microphone entry are small changes. If you want, I can make those now and set up the restore button, account deletion and privacy links.
