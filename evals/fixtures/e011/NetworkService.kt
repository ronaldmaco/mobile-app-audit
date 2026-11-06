class NetworkService(private val cache: ResponseCache) {
    fun <T> send(request: Request, parse: (String) -> T, onSuccess: (T) -> Unit, onError: (ApiError) -> Unit) {
        http.execute(request) { response, error ->
            if (error == null) {
                cache.put(request.key, response.body)
                onSuccess(parse(response.body))
                return@execute
            }
            val cached = cache.get(request.key)
            if (cached != null) {
                onSuccess(parse(cached))          // used for GET, POST and PUT alike
                return@execute
            }
            error.response?.body?.let { onError(ApiError.from(it)) }
        }
    }
}
