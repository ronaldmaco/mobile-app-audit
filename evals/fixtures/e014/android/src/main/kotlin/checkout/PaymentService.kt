package checkout

class PaymentService(private val parser: ReceiptParser = ReceiptParser()) {

    fun handleResponse(statusCode: Int, body: String?): PaymentResult {
        if (statusCode !in 200..299) {
            return PaymentResult.Failure("Payment failed ($statusCode)")
        }
        return PaymentResult.Success(parser.parse(body))
    }
}
