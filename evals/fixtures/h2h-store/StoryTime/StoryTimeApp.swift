import SwiftUI

@main
struct StoryTimeApp: App {
    @StateObject private var session = SessionStore()
    @StateObject private var store = SubscriptionStore()

    var body: some Scene {
        WindowGroup {
            if session.isSignedIn {
                LibraryView()
                    .environmentObject(session)
                    .environmentObject(store)
            } else {
                WelcomeView()
                    .environmentObject(session)
            }
        }
    }
}
