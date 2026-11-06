final class AccountRepository {
    private let api: APIClient
    init(api: APIClient) { self.api = api }

    /// Called on launch and every time the account screen appears.
    func refresh(accountId: Int, completion: @escaping (Result<Account, Error>) -> Void) {
        api.request(method: "PUT", path: "/accounts/\(accountId)", body: nil, completion: completion)
    }

    func save(_ account: Account, completion: @escaping (Result<Account, Error>) -> Void) {
        api.request(method: "PUT", path: "/accounts/\(account.id)", body: account.encoded(), completion: completion)
    }
}
