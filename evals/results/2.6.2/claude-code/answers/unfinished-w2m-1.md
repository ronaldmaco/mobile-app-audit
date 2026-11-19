The app is about half built. Browsing works, adding to the cart works and signing in works. It cannot take an order, and as configured it cannot load real data or pass App Store review. I read all 17 files. I did not run lint, typecheck or a build, because no dependencies are installed and there is no lockfile, so none of this has been run on a device.

Stack: Expo SDK 53, React Native 0.79, expo-router, TypeScript.

**Done**
- **Shop list:** fetches products and opens the product page.
- **Login:** works when the server answers; the token is saved on the device.
- **Cart storage:** items are saved locally and the total is correct.
- **Navigation:** four tabs (Shop, Cart, Orders, Profile) plus the login and product pages.

**Partial**
- **Product page:** out-of-stock isn't handled (there's a TODO at line 18). If loading fails, the page shows "Loading..." forever.
- **Cart:** you can't remove items or change quantities, and the **Checkout button does nothing** (`app/(tabs)/cart.tsx:35`).
- **Profile:** the camera photo is never saved or uploaded, and the app never asks for camera permission.
- **Login:** a network error crashes the sign-in instead of showing a message.
- **API errors:** failures are hidden. A server error looks the same as "no plants available".

**Missing**
- **Orders tab:** just says "Coming soon".
- **Forgot password:** the button opens a page that doesn't exist.
- **Sign-up and checkout:** neither exists, and there's nothing for checkout or orders on the API side either.
- **Login check:** the app opens on the Shop tab without checking whether anyone is signed in. You only see the login screen after signing out.
- **Tests:** none, and Jest isn't installed, so `npm test` won't run.

**Problems that stop a store release**
1. **The server address is a placeholder.** `lib/config.ts` has `API_URL` set to `http://staging.example.com/v1`, and it uses plain HTTP. iOS and Android block plain HTTP in release builds, so a store build would load nothing.
2. **The app icon file is missing.** `app.json` points to `assets/icon.png`, which doesn't exist, so the build will fail. There's also no splash screen or Android icon.
3. **There's no camera permission text.** iOS requires a message explaining camera use. Without it, tapping "Take profile photo" crashes the app, and Apple rejects it.
4. **iOS build setup is missing.** There's no `eas.json` (the build-service config), no iOS bundle ID, and no build or version numbers.
5. **Apple will likely reject it as unfinished** because of the "Coming soon" tab and the Checkout button that does nothing.

**What to do first**
1. **Get the app to build:** install dependencies and commit a lockfile, add the icon and splash, add `eas.json`, the iOS bundle ID and the camera permission text, then confirm TypeScript passes.
2. **Make it connect safely:** put the real HTTPS server address in a config value per environment, show errors properly, and store the login token in encrypted storage instead of AsyncStorage.
3. **Require sign-in at launch and fix the dead ends:** add the login check, then either build forgot-password or remove the button for now.
4. **Decide how small the first release is.** Checkout is the biggest piece. You need to choose a payment provider, and Stripe is fine for physical goods (Apple's in-app purchase isn't required). For a first release, either build checkout and orders properly, or hide the Orders tab and the Checkout button. Shipping them as placeholders will get rejected.

Point 4 is the decision that sets the timeline, so it needs an answer from you. One more thing to check with the backend team: if you add sign-up, Apple also requires a way to delete the account from inside the app.

The full findings, with file and line references, are in `docs/mobile-resume/2026-11-18-mobile-completion-plan.md`. The next step is a prioritised task plan with acceptance criteria for each item (the `mobile-completion-plan` step). Tell me how you want to handle checkout and I'll write it.
