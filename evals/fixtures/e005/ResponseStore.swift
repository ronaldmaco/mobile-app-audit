enum ResponseStore {
    static let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0].appendingPathComponent("responses")
    static func save(_ data: Data, for request: URLRequest) {   // called for every 2xx response, any method
        try? data.write(to: dir.appendingPathComponent(key(request)))
    }
}
