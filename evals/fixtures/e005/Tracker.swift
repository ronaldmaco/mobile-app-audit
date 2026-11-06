enum Tracker {
    static func identify(_ user: User) { AnalyticsSDK.shared.identify(user.email) }
    static func event(_ name: String, _ props: [String: Any] = [:]) { AnalyticsSDK.shared.track(name, props) }
}
