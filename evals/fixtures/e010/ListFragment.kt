class ListFragment : Fragment() {
    private var isFetching = false
    private var currentQuery: Query = Query.ALL

    fun onTabSelected(tab: Tab) {
        currentQuery = if (tab == Tab.SAVED) Query.SAVED else Query.ALL
        load(currentQuery)
    }

    private fun load(query: Query) {
        if (isFetching) return
        isFetching = true
        repository.fetch(query,
            onSuccess = { items ->
                isFetching = false
                if (query != currentQuery) return@fetch
                adapter.submit(items)
            },
            onError = { showError() })
    }
}
