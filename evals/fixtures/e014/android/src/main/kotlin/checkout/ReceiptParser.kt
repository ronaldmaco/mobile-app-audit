package checkout

class ReceiptParser {

    private val field = Regex("\"(\\w+)\"\\s*:\\s*(\"([^\"]*)\"|[^,}\\s]+)")

    fun parse(body: String?): Receipt {
        val values = readFields(body.orEmpty())
        return Receipt(
            id = values["id"] ?: "",
            amountCents = values["amount_cents"]?.toLongOrNull() ?: 0L,
        )
    }

    private fun readFields(json: String): Map<String, String> =
        field.findAll(json).associate { match ->
            val quoted = match.groups[3]?.value
            match.groupValues[1] to (quoted ?: match.groupValues[2])
        }
}
