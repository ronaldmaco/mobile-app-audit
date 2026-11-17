class PremiumManager(private val prefs: SharedPreferences) {

    fun isPro(): Boolean = prefs.getBoolean("is_pro", false)

    // Called by the billing client when a purchase completes on the device.
    fun onPurchaseCompleted(purchase: Purchase) {
        prefs.edit().putBoolean("is_pro", true).apply()
    }
}

class LessonRepository(
    private val db: FirebaseFirestore,
    private val premium: PremiumManager,
) {
    suspend fun lessons(): List<Lesson> {
        val free = db.collection("lessons_free").get().await().toObjects(Lesson::class.java)
        if (!premium.isPro()) return free
        val pro = db.collection("lessons_pro").get().await().toObjects(Lesson::class.java)
        return free + pro
    }
}
