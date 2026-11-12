import Foundation

struct HTTPResponse {
    let statusCode: Int
    let body: Data?
    let headers: [String: String]

    var isSuccessful: Bool { (200...299).contains(statusCode) }
}
