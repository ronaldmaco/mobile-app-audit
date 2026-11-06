final class FeedLoader {
    func load(prefs: Preferences, completion: @escaping (FeedState) -> Void) {
        api.events(venues: prefs.venueIds, categories: prefs.categoryIds) { result in
            switch result {
            case .success(let events) where !events.isEmpty:
                completion(.loaded(events))
            default:
                completion(.error("Something went wrong"))   // also used for an empty list
            }
        }
    }
}
