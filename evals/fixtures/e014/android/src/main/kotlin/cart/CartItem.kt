package cart

data class CartItem(val sku: String, val name: String, val unitPriceCents: Long, val quantity: Int) {
    init {
        require(quantity > 0) { "quantity must be positive" }
        require(unitPriceCents >= 0) { "price cannot be negative" }
    }

    val totalCents: Long get() = unitPriceCents * quantity
}
