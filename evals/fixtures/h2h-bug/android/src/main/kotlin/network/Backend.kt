package network

data class Profile(val userId: String, val displayName: String)

sealed class ApiResult<out T> {
    data class Ok<T>(val value: T) : ApiResult<T>()
    object Unauthorized : ApiResult<Nothing>()
    data class Failure(val code: Int) : ApiResult<Nothing>()
}

/** Server API. Tokens are opaque strings issued by [login]. */
interface Backend {
    fun login(email: String, password: String): String?
    fun favorites(token: String): ApiResult<List<String>>
    fun profile(token: String): ApiResult<Profile>
}
