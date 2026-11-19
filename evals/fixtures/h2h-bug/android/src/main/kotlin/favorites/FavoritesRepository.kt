package favorites

import network.ApiClient
import network.ApiResult

/** Favourite song ids of the signed-in user. Cached in memory to keep the Home tab fast. */
class FavoritesRepository(private val api: ApiClient) {
    private var cache: List<String>? = null

    fun favorites(): List<String> {
        cache?.let { return it }
        return when (val r = api.call { b, t -> b.favorites(t) }) {
            is ApiResult.Ok -> r.value.also { cache = it }
            else -> emptyList()
        }
    }

    fun clear() {
        cache = null
    }
}
