package checkout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PaymentServiceTest {

    private val service = PaymentService()

    @Test
    fun validResponseReturnsReceipt() {
        val result = service.handleResponse(200, """{"id":"rcp_1001","amount_cents":2599}""")

        val success = assertIs<PaymentResult.Success>(result)
        assertEquals(Receipt("rcp_1001", 2599), success.receipt)
    }

    @Test
    fun serverErrorFails() {
        val result = service.handleResponse(500, """{"error":"internal"}""")

        assertIs<PaymentResult.Failure>(result)
    }
}
