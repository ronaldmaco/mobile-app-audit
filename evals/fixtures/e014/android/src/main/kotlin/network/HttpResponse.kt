package network

data class HttpResponse(val statusCode: Int, val body: String?, val headers: Map<String, String> = emptyMap()) {
    val isSuccessful: Boolean get() = statusCode in 200..299
}
