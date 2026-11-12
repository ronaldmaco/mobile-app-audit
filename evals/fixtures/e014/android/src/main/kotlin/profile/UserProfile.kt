package profile

data class UserProfile(val userId: String, val displayName: String, val email: String?) {
    val initials: String
        get() = displayName.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
}
