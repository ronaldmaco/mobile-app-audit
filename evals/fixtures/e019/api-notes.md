# Notes API (backend team)

## POST /notes
Creates a note and returns it (`201`, body `{ "id", "title", "body" }`).

Optional header `Idempotency-Key: <string>`. If a request arrives with a key already used by the same user in the last 24 hours, no new note is created: the original note is returned with `200`. Without the header, every request creates a new note.

## GET /notes
Returns `{ "notes": [ ... ] }`, newest first.

## Timing
Under load, POST /notes can take up to 40 s on slow mobile networks. Clients use a 60 s read timeout.
