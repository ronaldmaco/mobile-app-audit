package profile

import network.ApiClient
import network.ApiResult
import network.Profile

/** Profile shown in the drawer header and the Account screen. */
class ProfileRepository(private val api: ApiClient) {
    private var cached: Profile? = null

    fun profile(): Profile? {
        cached?.let { return it }
        return when (val r = api.call { b, t -> b.profile(t) }) {
            is ApiResult.Ok -> r.value.also { cached = it }
            else -> null
        }
    }
}
