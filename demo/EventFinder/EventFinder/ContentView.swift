import SwiftUI

struct ContentView: View {
    @State private var favourites: Set<Venue> = []

    private var events: [Event] {
        // Skip invalid venue ids before calling the API.
        let ids = favourites.map(\.id).filter { $0 > 0 }
        return EventsAPI.events(venueIDs: ids)
    }

    var body: some View {
        NavigationStack {
            List {
                Section("Your venues") {
                    ForEach(EventsAPI.venues) { venue in
                        Toggle(venue.name, isOn: binding(for: venue))
                            .accessibilityIdentifier("venue-\(venue.name)")
                    }
                }
                Section(favourites.isEmpty ? "All events" : "Events at your venues") {
                    if events.isEmpty {
                        Text("No upcoming events at your venues.")
                            .foregroundStyle(.secondary)
                    }
                    ForEach(events) { event in
                        VStack(alignment: .leading) {
                            Text(event.title).font(.headline)
                            Text(EventsAPI.venues.first { $0.id == event.venueID }?.name ?? "")
                                .font(.subheadline).foregroundStyle(.secondary)
                        }
                    }
                }
            }
            .navigationTitle("Event Finder")
        }
    }

    private func binding(for venue: Venue) -> Binding<Bool> {
        Binding(
            get: { favourites.contains(venue) },
            set: { isOn in
                if isOn { favourites.insert(venue) } else { favourites.remove(venue) }
            }
        )
    }
}
