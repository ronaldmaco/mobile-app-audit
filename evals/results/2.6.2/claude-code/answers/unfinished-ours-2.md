# PlantShop (Expo) status audit

**This app is not ready for the stores yet.** About half of the shopping flow works. There is no checkout, and several store blockers are in the config, not the screens. I read all 13 source files and the config. I did not build or run the app: there are no `node_modules` and no lockfile. So everything below comes from reading the code. Nothing has been reproduced on a device, and I haven't called the API.

## Done (works as written)
- **Navigation:** four tabs (Shop, Cart, Orders, Profile), plus a product detail screen and a login screen, all using expo-router.
- **Product list** (`app/(tabs)/index.tsx`): loads products from `/products`, with a loading state and an empty-list message.
- **Product detail and "Add to cart"** (`app/product/[id].tsx`): the cart is stored on the phone and merges repeat items.
- **Cart screen** (`app/(tabs)/cart.tsx`): shows each item and the total, and refreshes when you open the tab.
- **Login and logout** (`lib/auth.ts`): login sends email and password and saves the token. The token is sent with every API call.

## Partial (exists, but broken or unfinished)
| Where | Problem |
|---|---|
| `lib/api.ts:11` | It never checks whether the request succeeded. A 401 or 500 error reply is passed to the screens as if it were the product list. The list may crash or show nonsense. |
| `lib/api.ts:17`, `:25` | Network errors are swallowed. If you're offline, the Shop tab says "No plants available right now". The product page shows **"Loading..." forever** (`[id].tsx:16`). There's no error message and no retry. |
| `app/login.tsx:13` | A network failure isn't handled at all, so nothing happens on screen and the error goes nowhere. There's no loading indicator, so the button can be tapped twice. |
| `app/login.tsx:24` | "Forgot password?" goes to `/forgot-password`, but that screen doesn't exist, so it opens a "not found" page. |
| `[id].tsx:18` | There's a TODO for out of stock. You can add a product with `stock: 0`, and there's no confirmation after adding. |
| Cart | You can't change quantities or remove items. **The Checkout button does nothing** (`cart.tsx:35`). |
| Profile | The camera avatar isn't saved, so it's lost on restart. The app never asks for camera permission before opening the camera. On Android that probably fails (not confirmed). |
| Auth | The token is stored unencrypted in AsyncStorage; `expo-secure-store` is the standard choice. There's no login check: the app opens on the tabs, and the login screen is only reachable after signing out. |

## Missing
- **Checkout, payment and order creation.** Plants are physical goods, so a normal payment provider (e.g. Stripe) is fine; Apple/Google in-app purchase isn't required.
- **Orders tab:** it's just "Coming soon". Apple's review guideline 2.1 (app completeness) rejects placeholder screens.
- **Sign-up, forgot password, and in-app account deletion.** Deletion is required by both stores if users can create accounts.
- **Production setup:**
  - `API_URL` is hard-coded to `http://staging.example.com` (`lib/config.ts:1`). Release builds on iOS block plain `http`, and Android blocks it by default. A store build would probably show an empty shop.
  - There's no prod/staging environment setting.
- **App identity and build setup:**
  - `assets/icon.png` is referenced but doesn't exist; the folder only has `.keep`. There's no splash screen or Android adaptive icon either.
  - There's no `ios.bundleIdentifier`, no `eas.json`, and no build-number scheme.
  - There's no camera permission text (via the `expo-image-picker` plugin in `app.json`). Apple guideline 5.1.1 requires a specific explanation.
- **Quality checks:**
  - `npm test` runs `jest`, but Jest isn't installed and there are no tests.
  - `npm run lint` has no ESLint setup.
  - There's no lockfile, so installs can't be repeated reliably.
- **Store paperwork:**
  - privacy policy
  - Play Data safety form and Apple privacy labels
  - a demo account for reviewers that works from the US
  - screenshots
- **Expo SDK 53 (from May 2025) is old by now.** Check it against the current Apple Xcode/SDK minimum and Google's target API level before planning a build. I haven't verified those requirements against the official pages today.

## What to do first, in order
1. **Make it build:**
   - install dependencies and commit a lockfile
   - run `npx expo-doctor` and `tsc`
   - add an icon, a splash screen, a bundle ID and `eas.json`
   - make an internal EAS build for both platforms

   This tells you whether it compiles at all, and whether you should upgrade the SDK *before* adding features (I'd expect yes).
2. **Point it at a real HTTPS production API**, with the URL set per environment. Then confirm with the backend owner that `/products`, `/products/:id` and `/login` exist and what their replies look like.
3. **Decide the scope for version 1.** Either build checkout and orders, or remove the Orders tab and the Checkout button for a browse-only release. Placeholder screens get rejected.
4. **Fix the error handling:** check whether each request succeeded, show error and retry states instead of "empty" or endless loading, handle login failures, and request camera permission.
5. **Accounts:** add the forgot-password screen, sign-up (if the backend supports it), account deletion, a secure token store, and a login check.
6. **Store prep:** privacy policy, Data safety form and privacy labels, a demo account, then TestFlight and Play internal testing.

The backend matters most and I can't see it from here. The checkout, sign-up and account-deletion features are only possible if the API supports them. Ask the previous contractor or the backend team which endpoints exist.

I can start on step 1 (install, build checks, `app.json`/`eas.json`) if you want.
