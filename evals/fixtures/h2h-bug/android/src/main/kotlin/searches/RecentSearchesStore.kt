package searches

/** Last searches typed by the user, newest first, shown under the search field. */
class RecentSearchesStore(private val max: Int = 10) {
    private val items = ArrayDeque<String>()

    fun add(query: String) {
        val q = query.trim()
        if (q.isEmpty()) return
        items.remove(q)
        items.addFirst(q)
        while (items.size > max) items.removeLast()
    }

    fun all(): List<String> = items.toList()

    fun clear() = items.clear()
}
