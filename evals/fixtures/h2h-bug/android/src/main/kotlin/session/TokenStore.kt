package session

/** Holds the session token. Backed by encrypted preferences in the app; in memory here. */
class TokenStore {
    var token: String? = null
}
