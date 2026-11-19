I read all 13 files in the repo. I didn't build the app; the Xcode project isn't in this export. Several things here would get it rejected. In order of what to fix first:

## Blockers: these will almost certainly get it rejected

1. **The scanner will crash the app.** `ScannerView.swift:15` opens the camera, but `Info.plist` has no `NSCameraUsageDescription`. iOS kills the app as soon as it asks for camera access, and a crash during review is an automatic rejection (guideline 2.1). Add a camera purpose string. Also remove `NSMicrophoneUsageDescription` (`Info.plist:12`): nothing in the app records audio, and a permission string for a feature that isn't there can be flagged under 5.1.1.

2. **The reviewer can't sign in.** `docs/review-notes.md` says the SMS code is forwarded by your support team from 9 to 17 CET. Reviewers work around the clock, mostly in US time zones, and they won't wait for a person. Give the demo account a fixed code or let it skip SMS on the server. This is one of the most common reasons first submissions are rejected (2.1).

3. **There's no way to delete an account.** Users can create an account (`WelcomeView.swift:18`), but Settings only offers "Sign out". Apps that create accounts must let users delete them from inside the app (5.1.1(v)). Add "Delete account" to Settings, backed by a server endpoint that actually deletes the data.

4. **The paywall is missing things the subscription rules require** (`PaywallView.swift`, guidelines 3.1.1 and 3.1.2):
   - There's no **Restore Purchases** button. Call `AppStore.sync()` and then `refresh()`.
   - The price doesn't say how often it's charged. "Only $4.99!" needs to read like "$4.99 / month, auto-renews until cancelled".
   - It needs links to your **Terms of Use (EULA)** and **Privacy Policy**.
   - If the product doesn't load, the screen still shows a made-up "$4.99" and "Start now" silently does nothing. Show the real price or a loading/error state, never a hardcoded one.

5. **The privacy manifest is wrong and incomplete** (`PrivacyInfo.xcprivacy`):
   - The app uses `UserDefaults`, which Apple requires you to justify in the manifest. Without that entry, App Store Connect rejects the upload. Add `NSPrivacyAccessedAPICategoryUserDefaults` with reason `CA92.1`.
   - The manifest says no data is collected, but the app collects email, password, the child's first name and (for SMS) a phone number. Declare these, and make your App Privacy answers in App Store Connect match. Collecting a child's name will get extra scrutiny.

6. **The app shows placeholder content.** "Bedtime tips" is Lorem ipsum and "Contact us" says "Coming soon" (`SettingsView.swift:12-13`). Reviewers reject unfinished content (2.1/2.3). Replace them with real content or remove them, and put a working support contact and a Privacy Policy link in Settings.

## High risk: likely to be questioned or rejected

7. **Unlocking audio with a QR code.** Guideline 3.1.1 says apps may not unlock content with their own mechanisms such as QR codes; content is supposed to be sold through In-App Purchase. Companion content for a physical book you've already bought is usually allowed, but explain it up front in the review notes and include a test QR code image, since the reviewer won't have a book. The scanner also gives no feedback at all: errors are ignored and it just closes (`ScannerView.swift:31`).

8. **There's not much a reviewer can actually do in the app.** In the code I have:
   - The free stories are plain text labels with no playback.
   - "Open the full library" does nothing for subscribers (`LibraryView.swift:16`).
   - Every error is silently ignored (`try?` everywhere).
   - "Sign in" moves to the code screen even when login fails (`WelcomeView.swift:23`).

   If this is how the real build behaves, it risks rejection for being incomplete or too thin (2.1/4.2). Make sure the free stories play and that errors show a message.

9. **Everything is behind a login.** Even the two free stories need an account. Guideline 5.1.1(v) discourages requiring sign-up for features that don't need one. Consider letting people try the free stories before registering.

10. **The server address is a placeholder.** `API.swift:5` points to `https://api.example.com/v2`. If the release build really uses that, nothing works. Confirm the production URL is set.

## Worth checking (not visible in this export)
- If the app also runs on iPad, portrait-only orientation without `UIRequiresFullScreen` fails validation at upload.
- If you plan to list it in the **Kids** category, guideline 1.3 adds stricter rules: parental gates in front of purchases and external links, and limits on data collection.
- You don't need Sign in with Apple, since you only use your own accounts.

## Not rejection issues, but worth fixing
- The login token is stored in `UserDefaults`; Keychain is the safer place.
- `isPlus` is never set back to false when a subscription lapses.
- The version shown in Settings is typed in by hand instead of read from the bundle.

**Suggested order:** 1 and 5 first (crash and upload rejection), then 2 (demo login), then 3 and 4 (account deletion and paywall), then 6 to 10. I haven't changed anything. I can make the code and plist fixes for items 1, 3 (app side), 4, 5 and 6 if you want.
