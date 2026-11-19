> Note: one plain-HTTP URL fragment in this answer was shortened to `http` for the anonymisation check; nothing else changed.

I read all 18 files in the repo. I didn't install dependencies, build, or run the app, so everything below comes from reading the code. The app has a working shop, product page and cart, all wired to a staging API. The parts that make it a store, such as checkout, orders and login, are stubbed or broken, and the app can't be built for the stores yet.

## Done (works as far as the code goes)
- **Navigation**: Expo Router with four tabs (Shop, Cart, Orders, Profile), plus Login and Product detail screens.
- **Product list** (`app/(tabs)/index.tsx`): fetches `/products` and shows image, name and price, with loading and empty states.
- **Product detail** (`app/product/[id].tsx`): fetches one product and has an "Add to cart" button.
- **Cart storage** (`lib/cart.ts`): the cart is saved on the device, adding the same plant twice increases its quantity, and the total is calculated.
- **Login and logout** (`lib/auth.ts`): posts to `/login`, saves the token, and sends it with API requests.

## Partial (exists but is wrong or incomplete)
- **Login can't be reached.** The app opens straight into the tabs, and the only way to the login screen is pressing "Sign out". No screen checks whether the user is logged in.
- **API errors are hidden.** `lib/api.ts:11` doesn't check the response status, and errors are swallowed. A server error shows as "No plants available". A 401 returns an error object instead of a list, which will probably break the list screen.
- **Product detail can hang.** If the request fails, `getProduct` returns `null` and the screen shows "Loading..." forever.
- **Out of stock isn't handled.** `stock` is in the data but unused (there's a TODO at `[id].tsx:18`), so out-of-stock plants can be added to the cart. Adding an item also gives the user no confirmation.
- **The cart is read-only.** You can't remove items or change quantities, and signing out doesn't clear it.
- **The profile photo is lost.** It isn't saved or uploaded, so it disappears on restart.
- **The API address is a hardcoded staging URL over plain `http`** (`lib/config.ts`). iOS and Android both block plain-http requests by default, so on a real phone the shop will probably show nothing.
- **The login token is kept in AsyncStorage**, which isn't encrypted. `expo-secure-store` is the right place for it.
- **The `test` script runs Jest, but Jest isn't installed** and there are no tests. `lint` has no config.

## Missing
- **Checkout**: the button does nothing (`cart.tsx:35`). There's no payment, address or order submission.
- **Orders**: the tab just says "Coming soon".
- **Forgot password**: the login screen links to `/forgot-password`, which doesn't exist.
- **Sign-up and account deletion**: Apple rejects apps that let you have an account but not delete it from inside the app.
- **Store build setup:**
  - `assets/` is empty, but `app.json` points to `./assets/icon.png`, so builds will fail. There's also no splash screen or Android adaptive icon.
  - There's no iOS `bundleIdentifier`, `buildNumber` or `versionCode`.
  - There's no `eas.json` (the build config for Expo's cloud build service).
  - There's no camera permission text. iOS requires it, and without it the app will be rejected or crash when the camera opens. It's set through the `expo-image-picker` plugin.
- **Store listing items**: privacy policy URL, App Store privacy answers and Play data-safety form, screenshots and store text.

## What to do first
1. **Confirm the backend.** Find out whether there is a production API on `https`, and whether it already has endpoints for orders, payments, sign-up, password reset and account deletion. Most of the missing work depends on this, and the contractor may have left the backend half-done as well.
2. **Make the app buildable.** Upgrade Expo: the app is on SDK 53, which is now well out of date, and both stores have since raised their minimum OS/SDK versions (check the current numbers). Then add the icon and splash images, the bundle ID and version numbers, the camera permission text, `eas.json`, and a staging/production API setting. Get an internal test build onto real phones through TestFlight and Play internal testing. This flushes out store-config problems early, and the plain-http problem will show up on device straight away.
3. **Fix the basics**: require login on start, handle API errors and the stuck loading screen, block out-of-stock items, let users edit the cart, and move the token to secure storage.
4. **Build the missing features**: checkout with payment (Stripe or similar; Apple's in-app purchase isn't needed for physical goods), the orders list, forgot password, sign-up and account deletion.
5. **Prepare the store listings**: privacy policy, data disclosures, screenshots, and a demo account for Apple's review.

Checkout and payments are the largest piece of work. Steps 2–3 are roughly a few days of work, and step 4 depends mostly on what the backend already provides.
