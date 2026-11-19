## PlantShop: state of the app at 0.4.0

The app is an early prototype and can't be submitted yet. A user can browse plants, open one, add it to a local cart and sign in or out. They can't buy anything: there's no checkout, no order history and no sign-up. The build config also isn't ready for the stores.

I only read the code. I didn't install packages, build, type-check or run it on a device, because there's no `node_modules`, lockfile or `eas.json`. So every point below comes from reading the code, not from running it.

### ✅ Done (works as far as the code shows)
- **Navigation:** Expo Router with a root stack, four tabs (Shop, Cart, Orders, Profile), a login screen and a product page.
- **Product list:** loads `/products` and shows image, name and price, with loading and empty states (`app/(tabs)/index.tsx`).
- **Product page:** loads `/products/:id` and has a working "Add to cart" button.
- **Cart:** stored on the phone. It merges repeat items, shows lines and a total, and reloads when the tab opens (`lib/cart.ts`, `app/(tabs)/cart.tsx`).
- **Login and logout:** posts to `/login`, saves the token and sends it on API calls. Wrong passwords show an error.

### 🟡 Partial (there, but incomplete or buggy)
| Area | Problem |
|---|---|
| Error handling (`lib/api.ts:11`) | `res.ok` is never checked. A 401 or 500 error body is treated as real data, so the Shop list can break. |
| Product page (`app/product/[id].tsx:13,16`) | If the request fails, the page stays on "Loading..." forever. |
| Out of stock (`[id].tsx:18`) | Left as a TODO, so items with no stock can be added to the cart. |
| Login (`app/login.tsx:13`) | With no network, `fetch` fails and the error isn't caught. The user sees nothing and there's no loading indicator. |
| Cart | You can't remove items or change quantities. Adding an item gives no confirmation. The price is saved when added and never updated. |
| Profile photo (`profile.tsx`) | Only kept in memory: not uploaded, lost when the app restarts. The camera permission isn't requested first. |
| Session | The app opens on the tabs whether or not you're signed in. There's no login gate, and an expired token isn't handled. |
| Tooling | `npm test` and `npm run lint` exist, but Jest and ESLint aren't installed and there are no tests. There's no lockfile. |

### ❌ Missing
- **Checkout:** the button does nothing (`cart.tsx:35`). There's no payment, address or order creation.
- **Orders:** the tab just says "Coming soon".
- **Forgot password:** the button opens `/forgot-password`, which doesn't exist, so the user lands on a "not found" screen.
- **Sign-up**, and **in-app account deletion**. Both stores require deletion inside the app if accounts can be created in it. If accounts are only created elsewhere, check Apple's rule against your setup.
- **Production setup:** the API address is hard-coded to `http://staging.example.com/v1` (`lib/config.ts:1`), with no way to switch between staging and production.
- **Build and store config:**
  - `assets/icon.png` is referenced but `assets/` is empty, so the build should fail.
  - No splash screen or Android adaptive icon.
  - No `ios.bundleIdentifier` or `eas.json`.
  - No camera permission text or `expo-image-picker` plugin entry in `app.json`. iOS will probably crash when the camera opens.
- **Store listing:** privacy policy, App Store privacy labels and Google Play's Data safety form, a demo account for reviewers, and screenshots. `supportsTablet: true` also means Apple reviews on iPad and needs iPad screenshots.

### ⚠️ Problems that block release
1. **The API uses plain HTTP.** Passwords and tokens travel unencrypted. iOS (App Transport Security) and Android 9+ block plain HTTP in release builds by default, so the store build probably won't reach the server at all.
2. **The login token is stored unencrypted** in AsyncStorage. It should move to `expo-secure-store`.
3. **Expo SDK 53 is out of date.** It came out in spring 2025. Google Play raises the minimum required Android version (target API level) every August, and Apple raises the minimum Xcode/iOS SDK it accepts. SDK 53 probably no longer meets current requirements, so check the official pages before planning around it.
4. **Payments rule (good news):** these are physical goods, so Apple and Google allow a normal card payment provider like Stripe. You don't need in-app purchase.

### What to do first
1. **Decide the release scope.** The smallest version the stores would accept as a complete shop is: browse, cart with edit and remove, working checkout, order history, sign-up or a clear sign-in path, account deletion and password reset. Submitting with "Coming soon" tabs and a dead Checkout button is likely to be rejected for being incomplete (Apple guideline 2.1 or 4.2).
2. **Fix the base before adding features:**
   - Upgrade Expo to the current SDK.
   - Add a lockfile, `eas.json` and bundle IDs.
   - Add a real icon and splash screen.
   - Use an HTTPS production API set per environment.
   - Move the token to secure storage.
   - Get a first EAS build onto TestFlight and Play's internal testing track. This shows quickly whether anything else blocks a build.
3. **Make the existing screens reliable:**
   - Check API responses for errors.
   - Show error and retry states.
   - Add a login gate and handle expired tokens.
   - Handle out-of-stock items.
   - Request camera permission and add its description text.
   - Add Jest with a few tests on the cart logic.
4. **Build the missing features:** checkout, then orders, then sign-up, forgot password and account deletion. These need backend endpoints, so confirm with the backend team what already exists.
5. **Prepare the store submission:** privacy policy, privacy labels and Data safety form, a demo account that works from the US, screenshots, and a full run-through of the release build on real devices.

Step 2 is about a day or two of work and settles what the rest can be built on. I can start there, beginning with the Expo upgrade and the HTTPS/environment setup, if you want.
