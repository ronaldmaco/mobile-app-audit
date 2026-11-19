import SwiftUI

struct LibraryView: View {
    @EnvironmentObject var store: SubscriptionStore
    @State private var showPaywall = false
    @State private var showScanner = false

    var body: some View {
        NavigationStack {
            List {
                Section("Free stories") {
                    Text("The Sleepy Owl")
                    Text("Moon Boat")
                }
                Section("StoryTime Plus") {
                    Button(store.isPlus ? "Open the full library" : "Unlock 300+ stories") { showPaywall = !store.isPlus }
                }
                Section {
                    Button("Scan a book code") { showScanner = true }
                    NavigationLink("Settings") { SettingsView() }
                }
            }
            .navigationTitle("Library")
            .sheet(isPresented: $showPaywall) { PaywallView() }
            .sheet(isPresented: $showScanner) { ScannerView() }
            .task { await store.load() }
        }
    }
}
