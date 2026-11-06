Fixes merged in the iOS app last week (same backend, same features):
1. Venue options with id 0 stay selected (match by id, or by normalised name when id is 0). If every selected venue has no linked events, show the empty state instead of calling the API without `venue_ids`.
2. Home falls back step by step (venues + category, venues only, no preferences) when a list is empty.
3. A failed save of the tracking switch reverts the switch and logs the event only on success.
4. Images in reused cells: a late image for another row is dropped.
