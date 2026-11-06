Events API (from the backend docs):
- `venue_ids`: array of venue ids. Empty array = no venue filter.
- `category_ids`: array of category ids. Empty array = no category filter.

The venues endpoint today returns a few venues with `"id": 0` (no venue page in the CMS).
The app uses id 0 for the option "No favourite venue".
