package cart

class Cart {
    private val items = linkedMapOf<String, CartItem>()

    fun add(item: CartItem) {
        val existing = items[item.sku]
        items[item.sku] = if (existing == null) item else existing.copy(quantity = existing.quantity + item.quantity)
    }

    fun remove(sku: String) {
        items.remove(sku)
    }

    fun lines(): List<CartItem> = items.values.toList()

    fun subtotalCents(): Long = items.values.sumOf { it.totalCents }

    fun isEmpty(): Boolean = items.isEmpty()
}
