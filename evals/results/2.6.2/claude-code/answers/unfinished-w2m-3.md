> Note: one plain-HTTP URL fragment in this answer was shortened to `http` for the anonymisation check; nothing else changed.

The app is roughly 40% done. You can browse plants and add them to a cart, but nobody can place an order, and the build can't produce a store binary yet.

I checked this by reading the code only. No dependencies are installed and there's no lockfile, so I didn't run lint, typecheck, tests or a build. The full findings are in `docs/mobile-resume/2026-11-18-mobile-completion-plan.md`.

**Stack:** Expo SDK 53, React Native 0.79, expo-router with tabs, TypeScript.

### Done
- **Shop tab:** lists products and opens a product's detail page.
- **Cart storage:** items are saved on the device and the total is calculated.
- **Login and sign-out:** both call the backend.

### Partial
- **Product detail:** out-of-stock isn't handled (the contractor left a TODO for it), and there's no confirmation when you tap "Add to cart". If loading the product fails, the page shows "Loading..." forever.
- **Cart:** the Checkout button does nothing (`app/(tabs)/cart.tsx:35`), and you can't remove items or change quantities.
- **Login:** nothing opens it when the app starts, so users never see it. "Forgot password?" goes to a page that doesn't exist.
- **Profile:** the camera photo isn't saved or uploaded. No user info is shown.
- **API calls:** HTTP errors aren't checked, so a 401 or 500 response is treated as product data.

### Missing
- Checkout and payment, the Orders tab (it's a "Coming soon" placeholder), forgot-password, and sign-up.
- Secure token storage (the login token is stored unencrypted) and handling of expired sessions.
- Tests: there are none, and no test tooling is installed.
- Build setup: no `eas.json`, no iOS bundle identifier, and no app icon or splash image (`assets/` is empty).

### What to do first
1. **Get a build working.** Install dependencies and commit the lockfile. Add the icon and splash images, `ios.bundleIdentifier` and `eas.json`. Add the camera permission text for the profile photo; without it, iOS will reject the build or crash when the camera opens. Point the app at a production **HTTPS** URL chosen per environment. Right now it's hard-coded to an `http` staging URL address, which release builds on both platforms will block.
2. **Fix the basics that every flow depends on:** check for HTTP errors in API calls, show login when the user isn't signed in, and move the token to `expo-secure-store`.
3. **Build the purchase path:** cart editing, checkout, then the Orders tab. This is what the stores will actually review.
4. **Before you submit:** create a demo account for Apple's reviewers. If users can create accounts, Apple also requires a way to delete the account in the app. Add at least a smoke test of the purchase flow. Since you're selling physical goods, you can use Stripe or Apple Pay rather than Apple's in-app purchase.

One thing I need from you: does the backend already have checkout and orders endpoints? The app only calls products and login, so this decides how big step 3 is.

The next step is turning this into a prioritized task plan with acceptance criteria (the `mobile-completion-plan` skill). Want me to do that?
