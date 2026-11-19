# StoryTime: App Store review check before first submission

**Short version: high risk of rejection.** There are about 9 problems Apple routinely rejects apps for. Two of them would stop the reviewer from even reaching your paywall. All are fixable in a day or two.

I read every file in the repo and checked them against Apple's App Store Review Guidelines (October 16, 2026 version). There is no build in the repo, so I only checked the source code. The project file (`StoryTime.xcodeproj`) isn't in the repo either, so I couldn't see which devices you target or your build settings.

## Fix these first (most urgent at the top)

1. **The reviewer can't sign in (Guideline 2.1).** Sign-in always requires an SMS code. The draft notes for Apple say your support team forwards that code to the reviewer, only 9–17 CET (`docs/review-notes.md:5-6`). Apple reviews at any hour, often in US time zones, and rejects outright when it can't get into the app. **Fix:** let the demo account skip the SMS step, or give it a fixed code. Make sure the backend is live and the demo account works.
   - Related: `API.swift:5` points to `https://api.example.com/v2`. If that's the real build setting and not just a placeholder in this export, nothing in the app will work.

2. **Using the camera will crash the app (Guideline 5.1.1, upload error ITMS-90683).** The book-code scanner turns on the camera (`ScannerView.swift:15`). But `Info.plist` has no camera permission message (`NSCameraUsageDescription`). iOS force-closes an app that uses the camera without one, and App Store Connect will also flag it at upload. **Fix:** add a message such as "StoryTime uses the camera to scan the code inside your StoryTime book."

3. **The subscription doesn't unlock anything (Guidelines 2.1 and 3.1.2(a)).** After buying, the button reads "Open the full library", but it does nothing (`LibraryView.swift:16`). The library only has two free titles, and there are no premium stories anywhere in the app. The scanner also throws away the story it unlocks (`ScannerView.swift:31`). A reviewer will buy the subscription in the test environment and find nothing new. **Fix:** build the premium library screen, and make a scanned code open its story.

4. **No way to delete an account in the app (Guideline 5.1.1(v)).** Users can create an account (`WelcomeView.swift:18`), but Settings only has "Sign out" (`SettingsView.swift:9`). I found no delete call in the app or in `API.swift`. **Fix:** add "Delete account" in Settings, backed by a backend endpoint that deletes the account and the child's data.

5. **The paywall is missing required subscription details and a restore option (Guidelines 3.1.2(c), 3.1.1).**
   - `PaywallView.swift:11` shows "Only $4.99!" with no billing period. If the product fails to load, that price is hardcoded and may be wrong.
   - The paywall never says the subscription is monthly or that it renews automatically.
   - There are no links to Terms of Use or the privacy policy near the "Start now" button.
   - There is no "Restore Purchases" button. Add one that calls `AppStore.sync()`.

6. **No privacy policy link in the app (Guideline 5.1.1(i)).** I found none anywhere. The app collects an email address, a password, a phone number and a child's first name. That last one triggers the stricter rules for data about minors (Guideline 5.1.4). **Fix:** add a privacy policy link in Settings and on the paywall. The policy itself must cover data about children.

7. **Placeholder content (Guideline 2.1(a)).** "Bedtime tips" shows "Lorem ipsum…" and "Contact us" shows "Coming soon" (`SettingsView.swift:12-13`). Replace both with real content, such as a real support email or URL, or remove them.

8. **The privacy manifest is incomplete (ITMS-91053).**
   - The app uses `UserDefaults` (`SessionStore.swift:6`), which Apple requires you to declare with a reason code. `NSPrivacyAccessedAPITypes` is empty. Add the `NSPrivacyAccessedAPICategoryUserDefaults` entry with reason `CA92.1`.
   - `NSPrivacyCollectedDataTypes` is also empty, even though the app collects an email, a name and a phone number. It should match what you declare in App Store Connect's privacy section.

## Also worth fixing

- **Free stories require an account (Guideline 5.1.1(v)).** Apple says apps without significant account-based features should work without login. Consider letting people play the free stories without signing in.
- **Unlocking with a book's QR code (Guidelines 3.1.1 / 3.1.4).** Guideline 3.1.1 bans QR codes as a way to unlock content. Guideline 3.1.4 makes an exception for codes from your own physical product, as long as they're optional and the content can also be bought in the app. Mention this in your review notes and include a sample QR code for the reviewer.
- **The microphone message describes a feature that doesn't exist.** `Info.plist:12-13` says the app records your voice, but nothing in the code uses the microphone. Remove the entry, or the reviewer may go looking for the feature.
- **Errors are ignored silently.** Sign-in moves to the code-entry screen even when the first step fails (`WelcomeView.swift:23`). Failed purchases close the paywall without saying anything. If camera access is denied, the scanner shows a blank screen. Reviewers do run into these.
- **Login token storage.** The token is kept in `UserDefaults`. Apple won't reject for this, but the Keychain is the safer place.

## Check in App Store Connect
- [ ] Age rating and category. If you choose the Kids Category, Guideline 1.3 applies: no outside links or purchases without a parental gate. Don't use "for kids" wording in your listing unless you're in that category (Guideline 2.3.8).
- [ ] The privacy section (Apple's privacy "nutrition label") lists contact info and the child's name, linked to the user.
- [ ] `plus.monthly` is "Ready to Submit" and attached to this version.
- [ ] If the app runs on iPad, it must support all orientations or opt out of multitasking. `Info.plist` allows portrait only, which would fail at upload (ITMS-90474).
- [ ] After you archive, run `/appstore-review --build path/to/StoryTime.ipa` to check the built app for upload errors.

**Order of work:** issues 1–3 block the reviewer outright, so do those first. Then 4–8, which are each small and well-known rejection reasons. This check only covers the code; it can't guarantee approval.

I haven't changed any files. `/appstore-review --fix` can apply the simple config fixes automatically: the camera message, the microphone entry and the privacy manifest. The account deletion, restore button and paywall changes need your go-ahead first.
