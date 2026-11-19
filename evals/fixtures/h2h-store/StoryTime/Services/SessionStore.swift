import Foundation

@MainActor
final class SessionStore: ObservableObject {
    @Published private(set) var isSignedIn: Bool
    private let defaults = UserDefaults.standard

    init() {
        isSignedIn = defaults.string(forKey: "accessToken") != nil
    }

    func signUp(email: String, password: String, childName: String) async throws {
        let token = try await API.shared.register(email: email, password: password, childName: childName)
        defaults.set(token, forKey: "accessToken")
        isSignedIn = true
    }

    func requestCode(email: String, password: String) async throws {
        try await API.shared.startLogin(email: email, password: password)
    }

    func verify(code: String) async throws {
        let token = try await API.shared.verifySMS(code: code)
        defaults.set(token, forKey: "accessToken")
        isSignedIn = true
    }

    func signOut() {
        defaults.removeObject(forKey: "accessToken")
        isSignedIn = false
    }
}
