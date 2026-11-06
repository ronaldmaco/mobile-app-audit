# Case 004: the app did not launch when built with the new SDK

**Stack:** native iOS (UIKit), third-party social login SDK. **Mode:** E (store readiness, after a rejection).

> Real case, anonymised.

## Report
App Review rejected the build: the app does not open.

## What happened
The app had no `UIApplicationSceneManifest` and no scene delegate. Builds made with the newest iOS SDK require the UIScene life cycle and stop at launch ("UIScene life cycle is required for apps built with this SDK"). The same code built with the previous Xcode ran fine, so nobody saw it before submitting.

## How to prove it
- Build and launch with the Xcode/SDK used for submission on the newest simulator: crash at launch, message in the device log.
- Info.plist has `UIMainStoryboardFile` and no scene manifest.

## Fix
- Scene manifest in Info.plist (replacing `UIMainStoryboardFile`) and a scene delegate that shares the window with the app delegate, picks the first screen as before and forwards login callback URLs to the social login SDK (URL handling moves to the scene).
- No visual change.

## Skill coverage
| Gap | Added in 2.2 |
|---|---|
| Store rules mention UIScene, but nothing said "build and launch with the **submission** toolchain on the newest OS before submitting" | Store readiness: toolchain launch check |
| URL callbacks (social login, deep links) can silently break when moving to scenes | Checklist: after a lifecycle migration, retest every URL entry point cold and warm |
