import network.ApiResult
import network.Backend
import network.Profile

class FakeBackend : Backend {
    private val users = mapOf(
        "ana@example.com" to Pair("u1", "Ana"),
        "ben@example.com" to Pair("u2", "Ben"),
    )
    val favs = mutableMapOf("u1" to listOf("r10", "r11"), "u2" to listOf("r20"))
    private val sessions = mutableMapOf<String, String>()
    private var next = 0

    override fun login(email: String, password: String): String? {
        val (id, _) = users[email] ?: return null
        if (password != "secret") return null
        return "t${next++}".also { sessions[it] = id }
    }

    fun revokeAll(userId: String) { sessions.values.removeAll { it == userId } }

    override fun favorites(token: String): ApiResult<List<String>> =
        sessions[token]?.let { ApiResult.Ok(favs.getValue(it)) } ?: ApiResult.Unauthorized

    override fun profile(token: String): ApiResult<Profile> =
        sessions[token]?.let { id -> ApiResult.Ok(Profile(id, users.values.first { it.first == id }.second)) }
            ?: ApiResult.Unauthorized
}
