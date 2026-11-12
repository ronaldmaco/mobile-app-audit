import Foundation

struct UserProfile: Equatable {
    let userId: String
    let displayName: String
    let email: String?

    var initials: String {
        displayName.split(separator: " ").prefix(2).compactMap { $0.first.map { String($0).uppercased() } }.joined()
    }
}
