I read all 13 files in the repo and found at least five things that will very likely get StoryTime rejected. Everything below comes from reading the code: I didn't build the app, run it on a device, or call your backend (the code points at a placeholder, `api.example.com`). I also didn't re-open Apple's guideline pages today, so check the current wording of each guideline number before you rely on it.

## Fix first: reviewers commonly reject for these

**1. The app crashes when someone taps "Scan a book code".**
`ScannerView.swift:15` turns on the camera, but `Info.plist` has no `NSCameraUsageDescription` (the message iOS shows when asking for camera access). Without it, iOS closes the app the moment the camera is requested. Reviewers count crashes as an incomplete app (guideline 2.1).
*Fix:* add the key with a specific sentence, e.g. "Scan the code printed inside your StoryTime book to unlock its audio story."

**2. The reviewer can't sign in.**
Signing in needs a code sent by SMS. Your `docs/review-notes.md` says support forwards that code by hand, 9:00–17:00 CET. Review often happens outside those hours, and the reviewer won't wait for a code. This also falls under 2.1.
*Fix:* make the demo account skip SMS on the server, or give it a fixed code that never expires, and put that in the notes. Then test the demo login yourself from a US connection.
Something else to check: sign-up (`API.swift:7`) never asks for a phone number, but sign-in sends the code "to the phone number on the account". If that's how the server works, new users can't sign back in after signing out. I can't confirm this without the backend.

**3. Buying the subscription unlocks nothing, and purchases can't be restored.**
- After someone subscribes, "Open the full library" (`LibraryView.swift:16`) only sets `showPaywall = false`. It doesn't open anything, and there's no subscriber-only content in the code. A reviewer who pays in the test environment gets nothing (2.1 and 3.1.1).
- There's no "Restore Purchases" button anywhere (3.1.1 requires a way to restore).
- The paywall (`PaywallView.swift:11`) shows "Only $4.99!" without saying it's monthly. If the price fails to load, it shows a hard-coded $4.99, which may not match the real price. There are also no links to the Terms of Use or the Privacy Policy (3.1.2).
- "Start now" closes the paywall even when the purchase fails or is cancelled, with no message.

**4. Users can't delete their account.**
The app lets people create accounts (`WelcomeView.swift:18`), but Settings only has "Sign out". Apple requires a way to start account deletion inside the app (5.1.1(v)), and it has to delete the data on your server, not just disable the account.
*Fix:* add a "Delete account" option backed by a server endpoint that really deletes the data.

**5. Placeholder content is still in the app.**
- "Bedtime tips" (`SettingsView.swift:12`) shows Lorem ipsum text.
- "Contact us" (`SettingsView.swift:13`) just says "Coming soon".
- The two free stories (`LibraryView.swift:12-13`) are plain text rows that can't be tapped. As far as the code shows, nothing in the app plays a story.

Reviewers treat placeholders as an unfinished app (2.1). With no working stories, they may also say the app doesn't do enough (4.2).

## Fix before upload: privacy and declarations

**6. The privacy file (`PrivacyInfo.xcprivacy`) is incomplete.**
- The app uses `UserDefaults` (`SessionStore.swift:6`). That's on Apple's list of APIs you must declare with a reason, but the file declares none. Apple can reject the upload over this.
*Fix:* declare `NSPrivacyAccessedAPICategoryUserDefaults` with reason `CA92.1`.
- The file says the app collects no data, but it collects email, password, the child's first name, a phone number (through the SMS login) and purchases. This file and the privacy answers you give in App Store Connect need to match what the app actually collects.
- The app has no privacy policy link. Since you collect a child's name, also decide whether you're submitting to the Kids category, because that brings stricter rules.

**7. The microphone permission is for a feature that doesn't exist.**
The microphone message in `Info.plist` describes recording your voice, but there's no recording code. Remove the key until the feature ships.

## Risk to settle before you submit

**8. Unlocking audio by scanning a QR code.**
Guideline 3.1.1 generally doesn't allow apps to unlock content with their own codes, and it names QR codes as an example. Unlocking audio that comes with a printed book you sold may be allowed, but read the current wording of 3.1.1 and 3.1.3. Explain the scanner in the review notes so the reviewer doesn't read it as a way around in-app purchase.

## Smaller issues (unlikely to cause rejection on their own)
- Sign-in, sign-up and scanning errors are silently ignored (`try?`). For example, "Sign in" moves to the code screen even when the request failed. Showing an error message would help the reviewer, and your users.
- The login token is stored in `UserDefaults`. The Keychain is the secure place for it.
- The version text in Settings is hard-coded as "1.0 (14)". Also make sure build 14 is higher than any build you've already uploaded to TestFlight.

**Order I'd work in:** 1 and 2 (crash and reviewer sign-in) → 3 (subscription) → 4 (account deletion) → 6 (privacy file) → 5 (placeholders) → 7 → 8.

Fixes 1, 6 and 7 are small edits I can make now. 3, 4 and 5 need product decisions and server work. Want me to start with the small ones?
