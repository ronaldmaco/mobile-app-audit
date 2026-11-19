PlantShop is a small Expo SDK 53 app (expo-router, 6 screens). You can browse plants and add them to a cart, but you can't buy anything, and it can't produce a store build as it stands. I read every file. I couldn't install dependencies (the npm registry was blocked here), so lint, typecheck and builds were never run and nothing was tested on a device.

**Done (basic but working on paper)**
- Shop list leading to product detail.
- Cart, saved on the device.
- Email/password login and sign out.
- Tab and stack navigation.

**Partial**
- **Product detail:** a TODO says out-of-stock isn't handled, and "Add to cart" gives no feedback.
- **Cart:** you can't remove items or change quantities, and the **Checkout button does nothing** (`onPress={() => {}}`).
- **Login:** a network error isn't caught, and "Forgot password?" goes to a `/forgot-password` screen that doesn't exist.
- **Profile:** you can take a camera photo for the avatar, but it isn't saved or uploaded.

**Missing**
- **Orders:** the screen just says "Coming soon".
- **Checkout and payment:** no code and no API endpoints.
- **Sign-in check at launch:** the app opens straight onto the tabs, so users only reach login after signing out.
- **Secure token storage:** the login token is kept unencrypted (AsyncStorage), not in `expo-secure-store`.
- **Tests:** none, and the `test` script calls jest, which isn't installed.
- **Build setup:** there's no lockfile, and no `eas.json`.

**What blocks a store build right now**
1. **The app icon doesn't exist.** `app.json` points to `assets/icon.png`, but `assets/` is empty, so the build will fail. There's also no iOS bundle identifier, no build numbers and no `eas.json`.
2. **The API address is staging over plain HTTP** (`http://staging.example.com/v1`). iOS and Android block plain HTTP by default in release builds, so the release app won't be able to load anything.
3. **The camera permission text is missing.** The `expo-image-picker` setting that declares camera access isn't in `app.json`, so iOS will crash or Apple will reject the app.
4. **The app will probably crash when the server returns an error.** `lib/api.ts` doesn't check whether a request succeeded, so the product screen tries to show a price on an error response.
5. **You can't complete a purchase**, which Apple rejects as an incomplete app.

**What to do first**
1. **Build setup (about a day):** add the icon and splash assets and an iOS bundle identifier, set up EAS build profiles, commit a lockfile, and get a dev build running on real phones. Until this works, nothing else can be checked.
2. **Make the app hold up:** move the API address to config with HTTPS and separate production and staging values, check for failed requests, add the camera permission setting, move the token to secure storage, add the sign-in check at launch, and fix or remove the forgot-password link.
3. **Build the purchase flow:** checkout with a payment provider like Stripe, then the Orders screen. Physical goods don't need Apple's in-app purchase.
4. **Store requirements:** a privacy policy and the privacy forms both stores ask for. If you add account sign-up, Apple also requires a way to delete the account inside the app.

Before anyone can plan step 3, we need answers from you: is the backend ready for checkout and orders, and which payment provider do you want?

The full findings, with file and line references, are in `docs/mobile-resume/2026-11-18-mobile-completion-plan.md`. The next step is a prioritized completion plan for your approval (`mobile-completion-plan`). Want me to write it?
