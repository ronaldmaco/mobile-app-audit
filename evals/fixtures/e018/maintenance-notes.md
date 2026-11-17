# Maintenance notes (collected by the team, 2026-11)

- The app is updated about twice a year. Last release: 5.4.0.
- `PromoBanner.bannerText()` is called from the Home screen's `onResume`.
- chartkit: the library's repository was archived by its owner; last release 2.3.1 in 2021; the README says it is no longer maintained.
- paylib: the vendor's support page says major version 3 receives fixes until 2027-06-30. Version 4 requires minSdk 24.
- Server certificate for api.example.com (from `openssl x509 -noout -enddate`): notAfter=Feb  2 2027 GMT. The hosting provider renews certificates automatically and may issue a new key.
