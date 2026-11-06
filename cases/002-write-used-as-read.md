# Case 002: the profile was "read" with an empty PUT

**Stack:** native iOS, headless CMS with a REST API. **Mode:** D (full audit).

> Real case, anonymised: names and endpoints are changed. The mechanism is the original one.

## Report
None. Found while reading the networking layer during a full audit.

## What happened
On every launch and every time the account screen opened, the app reloaded the profile with a `PUT` to the user resource and no body. It worked by accident: the server treats an empty update as a no-op and returns the user.
- A read needed the **update** permission. If someone tightened the role, the app would stop loading the profile.
- Every launch touched the "last updated" timestamp, so the backend could not tell real edits from app launches.
- A body added later by mistake (e.g. a default value) would overwrite the user's data on launch.

## How to prove it
- Endpoint inventory with method and purpose per call: one "fetch" uses a write method.
- The API's "current user" read endpoint, called with a test account, returns the same fields the app decodes.

## Fix
Use the read endpoint. A two-line change, no visual change.

## Skill coverage
| Gap | Added in 2.2 |
|---|---|
| The inventory recorded the method but nothing asked whether the method matches the intent | Networking checklist: "every read uses a read method" + pattern "write used as a read" |
