import Foundation

final class API {
    static let shared = API()
    private let base = URL(string: "https://api.example.com/v2")!

    func register(email: String, password: String, childName: String) async throws -> String {
        try await post("/accounts", ["email": email, "password": password, "child_name": childName])["token"] ?? ""
    }

    func startLogin(email: String, password: String) async throws {
        _ = try await post("/login", ["email": email, "password": password])
    }

    func verifySMS(code: String) async throws -> String {
        try await post("/login/verify", ["code": code])["token"] ?? ""
    }

    func unlock(bookCode: String) async throws -> String {
        try await post("/books/unlock", ["code": bookCode])["story_id"] ?? ""
    }

    private func post(_ path: String, _ body: [String: String]) async throws -> [String: String] {
        var req = URLRequest(url: base.appendingPathComponent(path))
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.httpBody = try JSONEncoder().encode(body)
        let (data, response) = try await URLSession.shared.data(for: req)
        guard (response as? HTTPURLResponse)?.statusCode == 200 else { throw URLError(.badServerResponse) }
        return try JSONDecoder().decode([String: String].self, from: data)
    }
}
