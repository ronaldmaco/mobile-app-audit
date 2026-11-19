I read every file in the repo (it's small: 7 Swift files, Info.plist, the privacy manifest, and the review notes). I didn't build or run anything because the `.xcodeproj` isn't in this export.

**Short answer: as it stands, this will almost certainly be rejected.** Several problems would each cause a rejection on their own. Here they are in the order I'd fix them.

## Fix first: each of these alone would cause a rejection

1. **The scanner crashes when it opens the camera.** `ScannerView.swift` uses the camera, but `Info.plist` has no `NSCameraUsageDescription`, so iOS kills the app as soon as the camera is used. Reviewers will tap "Scan a book code" (rejected under 2.1, app completeness).
   *Fix:* add a camera usage string, and handle the case where the user denies camera access (right now the screen just goes black).

2. **App Review can't sign in.** `docs/review-notes.md` sets up the demo account so that someone on your support team has to forward an SMS code between 9:00 and 17:00 CET. Reviewers work around the clock, mostly in US time zones, and won't wait for a forwarded code (2.1).
   *Fix:* make the demo account skip SMS verification, or give it a fixed code that always works and put that code in the notes.

3. **There's no way to delete an account.** The app lets people create accounts (`WelcomeView`), but Settings only has "Sign out". Apple requires in-app account deletion (5.1.1(v)).
   *Fix:* add "Delete account" in Settings, backed by a server endpoint that actually deletes the account.

4. **The paywall is missing things Apple requires (3.1.1 / 3.1.2).** In `PaywallView.swift`:
   - There's no **Restore Purchases** button.
   - It doesn't say the plan is monthly or that it renews automatically.
   - There are no links to the **Terms of Use (EULA)** or **Privacy Policy**.
   - If the product doesn't load, the screen still shows a made-up price ("$4.99"), and "Start now" then does nothing.

5. **Unlocking audio with a book's QR code breaks the in-app purchase rules.** Guideline 3.1.1 explicitly bans using QR codes to unlock content or features. Of everything on this list, this is the one most likely to need a product decision rather than a code change. Options:
   - Make the audio for each book free to everyone, and use the QR code only as a shortcut to it.
   - Sell the book audio through in-app purchase.
   - Remove the scanner for 1.0.

   I'd expect a rejection if you submit it as it is.

6. **The privacy manifest is wrong.** `PrivacyInfo.xcprivacy` declares no API usage, but the app uses `UserDefaults`. That's one of the APIs Apple requires a declared reason for (code `CA92.1`), and uploads without it get flagged. The manifest also says no data is collected, but the app collects email addresses and children's first names. That data must be declared in both the manifest and the App Store privacy labels.

7. **A paying subscriber gets nothing.** For subscribers, "Open the full library" does nothing, and the two free stories are plain text you can't tap. A reviewer who buys a test subscription will find no content (2.1 and 3.1.1).

## Fix next: likely to be flagged

8. **Placeholder content.** The "Bedtime tips" page is lorem ipsum, "Contact us" says "Coming soon", and the API address is `https://api.example.com/v2`. Check that the real address is set in your release build, or nothing will work. Rejected under 2.1 / 2.3.

9. **Microphone permission for a feature that doesn't exist.** `Info.plist` asks for microphone access to record your own voice reading, but the app has no recording feature. Remove the entry (5.1.1).

10. **An account is needed even for the free stories.** Apple pushes back on mandatory login when the main features don't depend on an account (5.1.1(v)). Letting people play the free stories without signing up lowers that risk.

11. **Children's data.** The app asks for a child's first name. If you list it in the Kids category, the stricter Kids rules apply (1.3), including a parental gate before purchases and external links. Either way, the privacy policy needs to cover children's data.

## Not rejection reasons, but worth fixing

- Errors are silently ignored everywhere. For example, "Sign in" shows the SMS code field even when the login request failed, so a reviewer can get stuck with no message.
- `SubscriptionStore` never listens for `Transaction.updates`, and `isPlus` never goes back to false when a subscription expires.
- The login token is stored in `UserDefaults`; it should go in the Keychain.
- After a scan, `dismiss(animated:)` probably won't close the SwiftUI sheet.

**Suggested order:** 1, 2, 6 and 9 are quick changes. Decide on 5 now, because it may change what 1.0 includes. Then do 3, 4 and 7, which are the real feature work, then the placeholder content and remaining items. I can make the code changes for 1, 4, 6, 9 and the account-deletion screen if you want. Items 2, 3 (the server side) and 5 need decisions or backend work from your team.
