// Android app. Image loading uses a library that cancels the previous request when a view is reused.
object VenueFilter {
    fun venueIdsForRequest(selected: List<Venue>): List<Int> =
        selected.map { it.id }.filter { it != 0 }   // drop "No favourite venue"
}

class FeedRepository(private val api: EventsApi) {
    fun loadHome(prefs: Preferences, onResult: (List<Event>) -> Unit) {
        api.search(venueIds = VenueFilter.venueIdsForRequest(prefs.venues), categoryIds = prefs.categoryIds) { events ->
            onResult(events)            // empty list renders "No results" in every section
        }
    }
}
