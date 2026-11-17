class RedeemCode(private val db: FirebaseFirestore, private val userId: String) {

    suspend fun redeem(code: String): Result<Int> {
        val couponRef = db.collection("coupons").document(code)
        val coupon = couponRef.get().await()
        if (!coupon.exists() || coupon.getBoolean("used") == true) {
            return Result.failure(IllegalStateException("Invalid code"))
        }
        val credits = coupon.getLong("credits")?.toInt() ?: 0
        couponRef.update("used", true).await()
        db.collection("users").document(userId)
            .update("credits", FieldValue.increment(credits.toLong())).await()
        return Result.success(credits)
    }
}
