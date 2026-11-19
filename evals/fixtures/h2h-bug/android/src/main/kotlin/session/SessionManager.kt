package session

import network.Backend

class SessionManager(
    private val backend: Backend,
    private val tokens: TokenStore,
) {
    /** Called after a user signs out, so per-user data can be dropped. */
    var onSignedOut: () -> Unit = {}

    val isLoggedIn: Boolean get() = tokens.token != null

    fun login(email: String, password: String): Boolean {
        val token = backend.login(email, password) ?: return false
        tokens.token = token
        return true
    }

    fun logout() {
        tokens.token = null
        onSignedOut()
    }

    /** The server rejected the token. The UI observes [isLoggedIn] and shows the login screen. */
    fun sessionExpired() {
        tokens.token = null
    }
}
