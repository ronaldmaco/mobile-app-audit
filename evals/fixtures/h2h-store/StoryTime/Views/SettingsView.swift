import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var session: SessionStore

    var body: some View {
        List {
            Section("Account") {
                Button("Sign out", role: .destructive) { session.signOut() }
            }
            Section("Help") {
                NavigationLink("Bedtime tips") { Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit.").padding() }
                NavigationLink("Contact us") { Text("Coming soon") }
            }
            Section {
                Text("Version 1.0 (14)")
            }
        }
        .navigationTitle("Settings")
    }
}
