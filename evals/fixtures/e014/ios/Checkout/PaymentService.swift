import Foundation

struct Receipt: Decodable, Equatable {
    let id: String
    let amountCents: Int
    let status: String?

    enum CodingKeys: String, CodingKey {
        case id
        case amountCents = "amount_cents"
        case status
    }
}

enum PaymentError: Error, Equatable {
    case server(statusCode: Int)
    case missingReceipt
    case invalidReceipt
    case notCaptured(status: String)
}

enum PaymentResult: Equatable {
    case success(Receipt)
    case failure(PaymentError)
}

final class PaymentService {
    private let decoder = JSONDecoder()

    /// The server answers 2xx while a payment is still open; only a settled payment is a success.
    private static let unsettledStatuses: Set<String> = ["pending", "authorized", "requires_action", "declined", "failed"]

    func handleResponse(statusCode: Int, body: Data?) -> PaymentResult {
        guard (200...299).contains(statusCode) else {
            return .failure(.server(statusCode: statusCode))
        }
        guard let body, !body.isEmpty else {
            return .failure(.missingReceipt)
        }
        guard let receipt = try? decoder.decode(Receipt.self, from: body) else {
            return .failure(.invalidReceipt)
        }
        let id = receipt.id.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !id.isEmpty, receipt.amountCents > 0 else {
            return .failure(.invalidReceipt)
        }
        if let status = receipt.status, Self.unsettledStatuses.contains(status) {
            return .failure(.notCaptured(status: status))
        }
        return .success(receipt)
    }
}
