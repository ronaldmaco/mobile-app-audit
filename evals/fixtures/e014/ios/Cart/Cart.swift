import Foundation

struct CartItem: Equatable {
    let sku: String
    let name: String
    let unitPriceCents: Int
    var quantity: Int

    var totalCents: Int { unitPriceCents * quantity }
}

final class Cart {
    private(set) var items: [CartItem] = []

    func add(_ item: CartItem) {
        if let index = items.firstIndex(where: { $0.sku == item.sku }) {
            items[index].quantity += item.quantity
        } else {
            items.append(item)
        }
    }

    func remove(sku: String) {
        items.removeAll { $0.sku == sku }
    }

    var subtotalCents: Int { items.reduce(0) { $0 + $1.totalCents } }
}
