package network

import session.TokenStore

/**
 * Adds the current token to every call. A 401 means the session expired on the server
 * (password changed elsewhere, token revoked): the listener sends the user back to login.
 */
class ApiClient(
    private val backend: Backend,
    private val tokens: TokenStore,
) {
    var onUnauthorized: () -> Unit = {}

    fun <T> call(block: (Backend, String) -> ApiResult<T>): ApiResult<T> {
        val token = tokens.token ?: return ApiResult.Unauthorized
        val result = withRetry { block(backend, token) }
        if (result is ApiResult.Unauthorized) onUnauthorized()
        return result
    }

    // TODO: flaky on slow networks, revisit backoff
    private fun <T> withRetry(attempt: () -> ApiResult<T>): ApiResult<T> {
        var last = attempt()
        var tries = 1
        while (last is ApiResult.Failure && (last as ApiResult.Failure).code >= 500 && tries < 3) {
            last = attempt()
            tries++
        }
        return last
    }
}
