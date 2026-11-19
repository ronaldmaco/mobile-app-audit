I read every file in the repo. This build would almost certainly be rejected, and probably for several reasons at once. Here is what to fix, most urgent first.

## Fix these first (certain or near-certain rejection)

**1. The scanner will crash the app.** `Info.plist` has no `NSCameraUsageDescription`, but `ScannerView.swift:15` opens the camera. iOS kills the app the moment "Scan a book code" is tapped. Add the key with a clear reason, e.g. "Scan the code inside your StoryTime book to unlock its audio."

**2. The reviewer can't sign in.** In `docs/review-notes.md`, sign-in needs an SMS code that your support team forwards by hand, 9–17 CET only. Reviewers work around the clock and won't wait, so they'll reject because they can't get into the app (Guideline 2.1). Set up the demo account to skip the SMS step or to accept a fixed code, and put that in the notes.

**3. Users can create an account but can't delete it (5.1.1(v)).** Settings only has "Sign out". Any app that lets people sign up must also let them start deleting the account inside the app. Add a "Delete account" option backed by a server endpoint.

**4. The subscription screen doesn't meet the rules (3.1.1 / 3.1.2).** In `PaywallView.swift`:
- There is no **Restore Purchases** button. This is required.
- The screen doesn't show the billing period, that it renews automatically, or how to cancel. Links to your **Terms of Use (EULA)** and **Privacy Policy** are also required.
- When the product hasn't loaded, the price falls back to a hard-coded `"$4.99"`. Show the real StoreKit price, or disable the button until it loads.
- "Start now" tells the user nothing. Use something like "Subscribe for $4.99/month".

**5. Unlocking audio with QR codes is a direct conflict with 3.1.1.** The guideline names QR codes as a way apps may **not** unlock content. You can argue it's companion content for a physical book people already bought, and some apps get this through. Explain it plainly in the review notes and expect a possible pushback. Decide now whether you'd rather ship 1.0 without the scanner.

**6. Placeholder and unfinished features (2.1, 2.3).**
- "Bedtime tips" shows Lorem ipsum (`SettingsView.swift:12`).
- "Contact us" says "Coming soon". Guideline 1.5 requires a working way to contact you.
- The free stories (`LibraryView.swift:12-13`) are plain text and can't be played.
- "Open the full library" does nothing for subscribers (line 16 only toggles the paywall).
- A successful scan throws away the returned `story_id`, so nothing happens.

The reviewer will pay for Plus and then reach a button that does nothing. If the real playback code just isn't in this export, ignore this point, but check it on a real build.

## Also likely to be flagged

**7. The privacy manifest is wrong.** `PrivacyInfo.xcprivacy` says you collect no data, but the app collects email, password, the child's name, a phone number (for SMS), and purchases. It also uses `UserDefaults` without declaring the required reason (`NSPrivacyAccessedAPICategoryUserDefaults`, reason `CA92.1`). The missing reason is flagged when you upload. Make the manifest and the App Store Connect privacy label match what you actually collect.

**8. The app requires an account before showing the free stories (5.1.1).** Reviewers often reject apps that force sign-up for content that doesn't depend on an account. Let people browse and play the free stories without signing in, and ask for an account only for Plus.

**9. The microphone permission is for a feature that doesn't exist.** `NSMicrophoneUsageDescription` describes recording your voice, but there's no recording code. Remove it until the feature ships.

**10. Errors are silent, so the reviewer hits dead ends.** Every call uses `try?` and ignores failures. A failed sign-in still moves to the code screen (`WelcomeView.swift:23`). A failed or cancelled purchase just closes the paywall. A denied camera permission leaves a blank screen. Show an error message in each case.

**11. Check the Kids category.** You collect a child's first name. If you list the app in the Kids category, stricter rules apply: you need a parental gate in front of purchases and outside links, and tighter data rules (1.3, 5.1.4). If it's aimed at parents, list it outside Kids, and still say in your privacy policy how you handle the child's name.

## Worth fixing, but won't cause rejection
- The access token is stored in `UserDefaults`. It belongs in the Keychain.
- `SubscriptionStore.refresh()` can only turn Plus on, never off, and nothing listens for `Transaction.updates`. Renewals, refunds and purchases made outside the app won't show up.

**Suggested order:** 1 (crash) → 2 (demo login) → 3 (account deletion) → 4 (paywall) → 6 (placeholders) → 7 (privacy manifest). Decide on 5 (scanner) before you submit. I can make the code-side fixes (1, 3 client side, 4, 6, 7, 9, 10) now if you'd like.
