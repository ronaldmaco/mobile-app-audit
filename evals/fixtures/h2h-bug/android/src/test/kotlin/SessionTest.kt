import app.AppGraph
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SessionTest {
    @Test
    fun loginLoadsOwnFavorites() {
        val app = AppGraph(FakeBackend())
        assertTrue(app.session.login("ana@example.com", "secret"))
        assertEquals(listOf("r10", "r11"), app.favorites.favorites())
    }

    @Test
    fun switchingAccountsAfterLogoutShowsNewFavorites() {
        val app = AppGraph(FakeBackend())
        app.session.login("ana@example.com", "secret")
        app.favorites.favorites()
        app.session.logout()
        app.session.login("ben@example.com", "secret")
        assertEquals(listOf("r20"), app.favorites.favorites())
    }

    @Test
    fun wrongPasswordFails() {
        val app = AppGraph(FakeBackend())
        assertFalse(app.session.login("ana@example.com", "nope"))
    }
}
