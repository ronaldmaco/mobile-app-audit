import Foundation

struct Receipt: Decodable, Equatable {
    let id: String
    let amountCents: Int

    enum CodingKeys: String, CodingKey {
        case id
        case amountCents = "amount_cents"
    }
}

enum PaymentError: Error, Equatable {
    case server(statusCode: Int)
    case missingReceipt
    case invalidReceipt
}

enum PaymentResult: Equatable {
    case success(Receipt)
    case failure(PaymentError)
}

final class PaymentService {
    private let decoder = JSONDecoder()

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
        return .success(receipt)
    }
}
