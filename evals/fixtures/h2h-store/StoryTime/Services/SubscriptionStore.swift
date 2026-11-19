import StoreKit

@MainActor
final class SubscriptionStore: ObservableObject {
    @Published private(set) var product: Product?
    @Published private(set) var isPlus = false

    func load() async {
        product = try? await Product.products(for: ["plus.monthly"]).first
        await refresh()
    }

    func buy() async {
        guard let product else { return }
        if case .success(.verified(let tx)) = try? await product.purchase() {
            await tx.finish()
            isPlus = true
        }
    }

    func refresh() async {
        for await result in Transaction.currentEntitlements {
            if case .verified(let tx) = result, tx.productID == "plus.monthly" { isPlus = true }
        }
    }
}
