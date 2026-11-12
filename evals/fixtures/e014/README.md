# Checkout

Payments module of the shopping app.

- `android/`: Kotlin/JVM code. `PaymentService.handleResponse` turns the server's answer to a payment request into a `PaymentResult`.
- Run the tests with `./gradlew test`.

The public API of `PaymentService` must stay unchanged: `handleResponse(statusCode: Int, body: String?): PaymentResult`, the `PaymentResult` types and `Receipt`. Other screens and modules call it.
