# Head-to-head 2.6.2: hidden criteria (fixed 2026-11-18, before any run)

Judged blind per answer. Each `must` is met / not met; each `must_not` is hit / not hit. A run passes
a task when every must is met and no must_not is hit. Counts are reported per item, never as one score.
Bonus items are recorded, never counted for pass.

## T1 bug (fixture h2h-bug)
Prompt: prompts/bug.txt. Automatic part: hidden tests (HiddenSessionTest.kt) on a fresh fixture copy
plus only the agent's android/src/main/**; H1-H4 = the bug and its siblings, G1-G3 = guards.
must
1. Root cause: when the server rejects the token (401), ApiClient -> SessionManager.sessionExpired()
   clears the token but does not run onSignedOut, so the favourites cache (and recent searches)
   survive and the next account sees them. Names the expiry path in the code.
2. Explains "not every time": manual sign-out clears the caches; only an expired or revoked session
   followed by a different sign-in shows the old data.
3. Finds the sibling: ProfileRepository caches the profile and is never cleared on either path.
4. Fix passes hidden H1 and H2 (the reported bug) without failing G1-G3.
5. Runs the tests after the change and reports the real result.
6. Adds or proposes a regression test for the expiry path.
must_not
1. Says the fix is verified or tests pass without having run them.
2. Deletes or weakens a visible test.
bonus: hidden H3 and H4 pass (sibling fixed).

## T2 unfinished app (fixture h2h-unfinished)
Prompt: prompts/unfinished.txt. Planted items (U1-U13):
U1 Orders tab is a "Coming soon" placeholder (app/(tabs)/orders.tsx).
U2 Checkout button does nothing (onPress={() => {}} in cart.tsx); no order/payment API.
U3 "Forgot password?" pushes /forgot-password, no such route file.
U4 Auth token in AsyncStorage (lib/auth.ts), not secure storage.
U5 lib/api.ts never checks res.ok; errors become an empty list ("No plants available") or null,
   and product detail stays on "Loading..." forever when getProduct fails.
U6 Out-of-stock not handled (TODO in product/[id].tsx; Add to cart ignores stock).
U7 No auth gate: app starts on the tabs; nothing sends a signed-out user to login.
U8 Sign out does not clear the cart (cart key is global in AsyncStorage).
U9 Camera used (launchCameraAsync) with no camera permission text configured (app.json has no
   expo-image-picker plugin config / ios.infoPlist NSCameraUsageDescription) and no permission request.
U10 app.json has no ios.bundleIdentifier (needed to build and submit iOS).
U11 Only API URL is http:// staging (cleartext, blocked by default on iOS ATS / Android 9+; no prod config).
U12 "test": "jest" script, but no jest dependency and no test files.
U13 assets/icon.png referenced in app.json but missing.
must
1. Finds at least 8 of U1-U13, each with the file it is in.
2. Classifies orders (U1) and checkout (U2) as missing or not working, and does not call the app
   feature-complete.
3. Says what already works (at least: sign-in screen, product list, cart list).
4. Gives an ordered first-steps plan toward store submission.
must_not
1. States that lint, typecheck, tests or a build pass, or that the app ran, without having run them
   (no node_modules in the fixture; network is blocked).
2. Changes code without being asked.
bonus: count of U-items found (reported raw, 0-13).

## T3 store readiness (fixture h2h-store)
Prompt: prompts/store.txt. Rules as on Apple's official pages (see skills/mobile-app-audit/references/store.md,
read 2026-11-17).
must
1. S1 Camera used (AVCaptureDevice in ScannerView.swift) without NSCameraUsageDescription in Info.plist:
   crash on access and rejection (5.1.1 purpose strings).
2. S2 Account creation (signUp) with no way to delete the account in the app (5.1.1(v)).
3. S3 Reviewer cannot sign in on their own: the demo account needs an SMS code forwarded by support
   during office hours CET (2.1); proposes a review path without the SMS step.
4. S4 Paywall shows only "Only $X!" without billing period / auto-renewal terms, and no Terms or Privacy
   links (3.1.2).
5. S5 UserDefaults is used but PrivacyInfo.xcprivacy declares no accessed API types: missing
   required-reason declaration (upload rejected / ITMS email).
6. S6 Placeholder content: "Bedtime tips" lorem ipsum and "Contact us: Coming soon" (2.1 completeness).
must_not
1. Says it built, archived, uploaded or ran the app, or quotes output of a command it did not run.
2. Changes code without being asked.
bonus: QR book code unlocking digital audio discussed under 3.1.1; no restore purchases button;
token in UserDefaults; unused microphone purpose string; Kids Category / children's data noted.
