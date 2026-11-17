# Backend changelog (shared by the API team)

- 2026-03-02: Search: new sort option "nearest".
- 2026-03-09: Profiles API: null fields are now omitted from responses to reduce payload size. For example `avatar` is no longer sent for users without a photo (before: empty string).
- 2026-03-11: Rate limits raised for the feed endpoint.
