import Foundation

struct Profile: Decodable {
    let id: String
    let name: String
    let avatar: String
}

final class ProfileStore {
    private let session: URLSession
    private let baseURL: URL

    init(session: URLSession = .shared, baseURL: URL) {
        self.session = session
        self.baseURL = baseURL
    }

    func loadProfile(id: String, completion: @escaping (Profile) -> Void) {
        let url = baseURL.appendingPathComponent("profiles/\(id)")
        session.dataTask(with: url) { data, _, _ in
            guard let data = data else { return }
            let decoder = JSONDecoder()
            let profile = try! decoder.decode(Profile.self, from: data)
            DispatchQueue.main.async { completion(profile) }
        }.resume()
    }
}
