I read every file (16 in total; the app is about 300 lines) and checked it against the App Store and Google Play rules. **Nothing has been built, installed or run**: there's no `node_modules`, no build setup, no backend I can reach, and no tests. Unless I say otherwise, each finding below comes from reading the code, with the file and line cited.

## Summary

This is a **prototype of a plant shop that can't be submitted yet**. Browsing products and adding them to a cart are mostly built. **Checkout, order history, a real backend address and the build setup** are not. The biggest unknown isn't in the code: it points at `http://staging.example.com/v1` (`lib/config.ts:1`). `example.com` is a placeholder domain, so **there may be no real backend at all**.

## Done (basically works, needs polish)

| Area | Where | Notes |
|---|---|---|
| Navigation: 4 tabs plus a product screen and a login screen | `app/_layout.tsx`, `app/(tabs)/_layout.tsx` | Fine. |
| Product list | `app/(tabs)/index.tsx` | Has loading and empty states. |
| Product detail and "Add to cart" | `app/product/[id].tsx` | Works if the API answers. |
| Cart stored on the phone | `lib/cart.ts`, `app/(tabs)/cart.tsx` | Lists items, adds up the total, reloads when you open the tab. |
| Sign in and sign out | `lib/auth.ts`, `app/login.tsx`, `profile.tsx:15` | The login form works and shows an error message. |

## Partial

- **Sign-in is never asked for.** The app opens straight into the tabs, and nothing leads to the login screen except "Sign out". A new user never sees it.
- **Network errors are hidden.**
  - `lib/api.ts:11` never checks whether the request succeeded, so a 401 or 500 error response is treated as product data. On the product screen that can crash at `price.toFixed` (`components/PriceTag.tsx:4`).
  - A lost connection on the shop tab shows "No plants available right now." instead of an error with a retry.
  - A failed product load leaves "Loading..." on screen forever (`product/[id].tsx:16`).
- **Out of stock isn't handled.** The API sends a `stock` field but the app never uses it, and there's a leftover TODO for it at `product/[id].tsx:18`. Out-of-stock plants can still be added to the cart.
- **The cart can't be edited.** You can't change quantities or remove items, and "Add to cart" gives no feedback. Prices are saved when the item is added, so they go stale. Signing out doesn't clear the cart.
- **The profile photo will fail.**
  - `profile.tsx:11` opens the camera without asking for camera permission first, which expo-image-picker requires. The failure isn't caught, so the button will most likely do nothing or throw. That needs confirming on a device.
  - No camera-permission explanation is set up in `app.json`, and Apple requires one.
  - The photo is never saved or uploaded, so it disappears on restart.
- **The login token is stored unencrypted** on the phone (AsyncStorage). It should go in the encrypted store (`expo-secure-store`).
- **Prices always show `$`** and are calculated with ordinary decimal numbers, which can be off by a cent. That's fine only if the shop is US-only.

## Missing

- **Checkout:** the button does nothing (`cart.tsx:35`). There's no address entry, no payment and no order creation. Physical goods may use Stripe or similar; Apple and Google in-app purchase is not required.
- **Order history:** the tab just says "Coming soon" (`orders.tsx:6`). Apple rejects placeholder screens under its "app completeness" rule (2.1).
- **Forgot password:** the button goes to `/forgot-password`, a screen that doesn't exist.
- **Account creation and deletion:** if customers can create accounts anywhere, both stores require a way to delete the account from inside the app.
- **A real backend address:** it should use HTTPS and differ between test and production builds. As written, iOS (and current Android) block plain `http` in release builds, so a store build would probably load no products at all.
- **Build and store setup:**
  - No `eas.json` (Expo's build configuration).
  - No iOS app identifier (`ios.bundleIdentifier`), and no build numbers for either platform.
  - `assets/` is empty, but `app.json` points to `./assets/icon.png`, so the build would fail. There's also no splash screen or Android icon.
  - No lockfile, and no `.gitignore`.
- **Tests and checks:** `npm test` runs jest, which isn't installed, and there are no tests. Linting isn't set up.
- **Store listing and paperwork:** privacy policy URL, Apple's privacy answers, Google's Data safety form, a demo account for reviewers, and screenshots. iPad screenshots are needed too, because `supportsTablet: true` is set.
- **Probably needed:** an Expo upgrade. The app is on Expo SDK 53 (React Native 0.79), released in spring 2025. Apple and Google both raise the minimum build tools and Android target version every year, so SDK 53 is probably below what they accept in November 2026. I haven't checked the current minimums on their official pages; that's the first thing to look up.

## What to do first, in order

1. **Confirm the backend exists.** Get the real production and staging addresses (HTTPS) and a test account. Then check the endpoints the app calls: `/products`, `/products/:id`, `/login`. If there's no backend, that's the critical path and most of the work.
2. **Upgrade Expo now, before writing more code.** Move to the current SDK that meets this year's store minimums, then fix the build setup: iOS identifier, `eas.json`, icon and splash. Get an internal test build onto real phones: TestFlight on iOS and internal testing on Play. Every later fix gets tested on that build.
3. **Agree the version 1 scope.** Checkout is the one essential gap. For Orders, Forgot password and the profile photo, either build each one or remove it. Removing is a valid way to ship sooner, and it beats a placeholder that gets rejected.
4. **Fix the basics** (small): require sign-in where it's needed, check for failed requests and show errors with a retry, handle out-of-stock items, move the token to the encrypted store, and either set up the camera properly or drop it.
5. **Store paperwork:** privacy policy, privacy answers and Data safety form, reviewer demo account, in-app account deletion if accounts can be created, and screenshots.

Things only you can answer: is there a real backend, and who owns it? Can customers sign up (in the app or on a website)? Which countries and currency? Which payment provider? Once the backend is known I can do step 2 and start step 4.
