func send<T: Decodable>(_ req: URLRequest, completion: @escaping (Result<T, Error>) -> Void) {
    URLSession.shared.dataTask(with: req) { data, response, error in
        if let error = error { print("request failed", error); return }
        guard let data = data, let value = try? JSONDecoder().decode(T.self, from: data) else {
            print("decode failed"); return
        }
        DispatchQueue.main.async { completion(.success(value)) }   // status code never checked
    }.resume()
}
