# App Store review check: StoryTime

**Verdict: HIGH RISK.** As it stands, StoryTime would almost certainly be rejected. Three problems are each enough on their own: the scanner crashes, there's no way to delete an account in the app, and the reviewer can't sign in. All of them are fixable.

I checked every source file against the October 16, 2026 App Store Review Guidelines. I couldn't inspect a built app because the `.xcodeproj` isn't in this export.

## Fix these first

**1. Scanning a book code crashes the app (Guidelines 2.1, 5.1.1)**
`ScannerView.swift:15-16` opens the camera, but `Info.plist` has no `NSCameraUsageDescription`. iOS kills any app that opens the camera without that entry, so the first reviewer who taps "Scan a book code" sees a crash.
- **Fix:** add `NSCameraUsageDescription` with text like "Scan the code printed in your StoryTime book to unlock its audio story."
- `NSMicrophoneUsageDescription` (`Info.plist:12-13`) describes a voice-recording feature that doesn't exist. Remove it unless you're shipping that feature.

**2. You can create an account but not delete it (Guideline 5.1.1(v))**
`WelcomeView.swift:18` creates accounts. `SettingsView.swift:9` only offers "Sign out", and I found nothing anywhere that deletes an account. Apple rejects this very consistently.
- **Fix:** add "Delete account" in Settings with a confirmation step. It needs a server endpoint that actually deletes the account, then signs the user out locally.

**3. The reviewer can't sign in (Guideline 2.1)**
`docs/review-notes.md:5-6` says the sign-in code is sent by SMS to your support phone and forwarded by staff from 9 to 17 CET. Reviewers mostly work US hours and won't wait, so this gets rejected as "unable to sign in".
- **Fix:** let `reviewer@example.com` skip the SMS step on the server, or give it a fixed code, and say so in the notes.
- **Related bug:** signing up (`API.swift:8`) never asks for a phone number, but sign-in always sends an SMS code (`API.swift:11-17`). Anyone who signs up and later signs out has no way back in. The code also hides errors (`try?` in `WelcomeView.swift:18,21,23`) and moves to the "enter code" screen even when the request failed.

## Also critical, fix before submitting

**4. Paid subscribers get nothing (Guidelines 2.1, 3.1.2)**
- Once someone subscribes, "Open the full library" sets `showPaywall = false` and does nothing else (`LibraryView.swift:16`).
- No paid stories are ever listed; the library only shows the two free ones.
- The paywall closes whether or not the purchase worked (`PaywallView.swift:13`).
- Reviewers buy the subscription in the test environment and check that they got what they paid for.

**5. The paywall is missing what Apple requires (Guidelines 3.1.1, 3.1.2(c))**
- **No Restore Purchases button.** `refresh()` exists but nothing calls it. 3.1.1 requires a way to restore purchases.
- **No billing period.** "Only $4.99!" (`PaywallView.swift:11`) doesn't say per month. Show `displayPrice` plus "/month" and that it renews automatically.
- **No legal links.** Add Terms of Use (EULA) and Privacy Policy links next to the purchase button.
- **Fallback price:** don't fall back to a hard-coded "$4.99". Disable the button until the product has loaded.

**6. No privacy policy inside the app (Guideline 5.1.1(i))**
The app collects email, password and a child's first name, and has no privacy policy link. Add one in Settings, and on the paywall per item 5.

**7. Privacy manifest declares nothing (5.1.1, upload error ITMS-91053)**
- `SessionStore.swift:6` uses `UserDefaults`, which Apple requires you to declare with a reason. `PrivacyInfo.xcprivacy:8-9` lists none.
- **Fix:** add `NSPrivacyAccessedAPICategoryUserDefaults` with reason `CA92.1`.
- `NSPrivacyCollectedDataTypes` is also empty but should list email, name and purchase history. Those must match your privacy label in App Store Connect.

**8. Placeholder content (Guidelines 2.1, 1.5)**
- "Bedtime tips" shows Lorem ipsum (`SettingsView.swift:12`).
- "Contact us" says "Coming soon" (`SettingsView.swift:13`). It needs real support contact details.

## Warnings

- **Unlocking stories with QR codes (Guideline 3.1.1).** The guidelines name QR codes as a forbidden way to unlock content. Your case is defensible because the audio comes with a printed book the customer already bought. Explain that in the review notes, include a sample QR code (Apple asks for one), and expect questions.
- **Scanner gives no feedback.** The unlock result is thrown away and failures are silent (`ScannerView.swift:31`). Unlocked stories never show up anywhere.
- **Children's data.** You collect a child's name. Choose the age rating and category carefully. If you list in the Kids category, 1.3 and 5.1.4 add stricter rules and a privacy policy that covers minors.
- **Token storage (not a rejection risk).** The login token is stored in UserDefaults. Moving it to the Keychain is better practice.

## Things to check in App Store Connect
- [ ] The demo account works without staff help (item 3), and the notes mention the sample QR code.
- [ ] Privacy policy URL and support URL are entered and both load.
- [ ] The `plus.monthly` subscription is in "Ready to Submit" and attached to this version.
- [ ] Privacy label declares email, name (child), purchases and user ID.
- [ ] Age rating fits a children's story app; screenshots show no prices or claims you can't back up.
- [ ] After archiving, check the built app for privacy-manifest upload errors (ITMS-91053/91061).

**Order of work:** add the camera permission text, add account deletion, and fix the demo login. Then make the subscription deliver content and add Restore, terms and the privacy policy. Fill in the privacy manifest and remove the placeholder screens last; those are quick.

_This is a static check against the public guidelines and can't guarantee approval. I didn't inspect a build because there's no Xcode project in this export._

I haven't changed any files. The mechanical fixes (camera permission text, removing the unused microphone entry, the privacy-manifest entries) are safe for me to apply now. I'll ask before building the bigger ones: account deletion, the paywall changes, and the server-side bypass for the demo account.
