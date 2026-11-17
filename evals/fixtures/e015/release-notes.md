# Submission notes for 3.1

- Last submission (3.1, build 42) was rejected under guideline 2.1: "We were unable to sign in or reach any content."
- We fixed a crash on the map screen since then. Version and build in Info.plist are unchanged.
- TestFlight already shows 3.1 (42).
- Demo account for App Review: reviewer@example.com / password in the review notes. On login the app sends a one-time code by SMS to the phone number of the account (a team member's phone).

## Edge rules for api.example.com (from the CDN dashboard)
```yaml
geo_access:
  mode: allow_list
  countries: [PE, CL, MX]
  action_for_others: block_403
```
