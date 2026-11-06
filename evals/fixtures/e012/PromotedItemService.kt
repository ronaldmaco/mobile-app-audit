class PromotedItemService(private val catalog: Catalog, private val owned: OwnedItems) {
    val promotedItem: Item
        get() = catalog.promotable.filterNot { owned.contains(it.id) }.random()
}

class HomeFragment : Fragment() {
    fun bindPromoted() {
        promotedCard.title.text = service.promotedItem.title
        promotedCard.setOnClickListener { openItem(service.promotedItem.id) }
    }
}
