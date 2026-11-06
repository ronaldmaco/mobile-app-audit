// Builds the body for POST /events/search.
// Previous fix (see git log): "Don't send id 0 to the events API".
struct EventsQuery {
    let favouriteVenues: [Venue]   // what the user picked
    let categories: [Category]

    func body() -> [String: Any] {
        let venueIds = favouriteVenues.map(\.id).filter { $0 != 0 }
        return [
            "venue_ids": venueIds,
            "category_ids": categories.map(\.id)
        ]
    }
}
