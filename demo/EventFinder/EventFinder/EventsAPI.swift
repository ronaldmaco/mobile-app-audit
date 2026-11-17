import Foundation

// A stand-in for the backend, so the demo runs offline.
// Like many real APIs, an empty venue filter means "any venue".

struct Venue: Identifiable, Hashable {
    let id: Int
    let name: String
}

struct Event: Identifiable {
    let id: Int
    let title: String
    let venueID: Int
}

enum EventsAPI {
    // The CMS gave the newest venue id 0.
    static let venues = [
        Venue(id: 12, name: "Jazz Club"),
        Venue(id: 7, name: "Open Air Stage"),
        Venue(id: 0, name: "Rooftop"),
    ]

    private static let allEvents = [
        Event(id: 1, title: "Late Night Trio", venueID: 12),
        Event(id: 2, title: "Big Band Friday", venueID: 12),
        Event(id: 3, title: "Summer Folk Fest", venueID: 7),
        Event(id: 4, title: "Electro Picnic", venueID: 7),
    ]

    /// GET /events?venue_ids=...  (empty list = every venue)
    static func events(venueIDs: [Int]) -> [Event] {
        guard !venueIDs.isEmpty else { return allEvents }
        return allEvents.filter { venueIDs.contains($0.venueID) }
    }
}
