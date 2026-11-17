import java.time.LocalDate

object PromoBanner {
    private val PROMO_END: LocalDate = LocalDate.of(2026, 12, 31)

    fun bannerText(today: LocalDate = LocalDate.now()): String {
        if (today.isAfter(PROMO_END)) {
            throw IllegalStateException("Promo config missing for $today")
        }
        return "Winter offer: 30% off until ${PROMO_END}"
    }
}
