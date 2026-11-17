import SwiftUI

struct SettingsView: View {
    @Environment(\.openURL) var openURL

    var body: some View {
        List {
            Button("Edit profile") { /* ... */ }
            Button("Log out") { Session.shared.logout() }
            Section(footer: Text("To close your account, email our support team.")) {
                Button("Delete account") {
                    openURL(URL(string: "https://help.example.com/contact")!)
                }
            }
        }
    }
}
