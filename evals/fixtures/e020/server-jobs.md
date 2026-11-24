# Server jobs (backend team notes)

## DELETE /me
Deletes the user row, profile photo, posts and chat messages within 24 h. Writes an audit line.
Does not call any vendor. Analytics and attribution data are keyed by device and advertising ID, not by user, so "nothing to delete there".

## Privacy requests
No endpoint. The privacy policy says to write to the support address; support answers "when possible".
People who installed the app but never signed up have no way to ask for deletion of their analytics or attribution data.
