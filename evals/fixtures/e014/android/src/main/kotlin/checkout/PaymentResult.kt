package checkout

data class Receipt(val id: String, val amountCents: Long)

sealed class PaymentResult {
    data class Success(val receipt: Receipt) : PaymentResult()
    data class Failure(val reason: String) : PaymentResult()
}
