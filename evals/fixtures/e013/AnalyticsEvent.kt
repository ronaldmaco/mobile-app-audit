enum class AnalyticsEvent(val key: String) {
    ITEM_OPENED("item_opened"),
    ITEM_SHARED("item_shared");

    var title: String? = null
    var screen: String? = null
}

// ItemDetailFragment.kt
fun onItemOpened(item: Item) = Analytics.log(AnalyticsEvent.ITEM_OPENED.apply { title = item.title; screen = "detail" })

// SearchFragment.kt
fun onResultOpened(item: Item) = Analytics.log(AnalyticsEvent.ITEM_OPENED)

// Analytics.kt
object Analytics {
    fun log(e: AnalyticsEvent) = sdk.track(e.key, mapOf("title" to e.title, "screen" to e.screen))
}
