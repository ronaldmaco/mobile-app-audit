package app

import favorites.FavoritesRepository
import network.ApiClient
import network.Backend
import profile.ProfileRepository
import searches.RecentSearchesStore
import session.SessionManager
import session.TokenStore

/** Object graph created once in Application.onCreate. */
class AppGraph(backend: Backend) {
    private val tokens = TokenStore()
    val session = SessionManager(backend, tokens)
    private val api = ApiClient(backend, tokens)
    val favorites = FavoritesRepository(api)
    val searches = RecentSearchesStore()
    val profile = ProfileRepository(api)

    init {
        api.onUnauthorized = { session.sessionExpired() }
        session.onSignedOut = {
            favorites.clear()
            searches.clear()
        }
    }
}
