import app.AppGraph
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Hidden tests for the h2h bug task. Public API only (README).
class HiddenSessionTest {
    // H1: report path. Session expires (401), another user logs in on the same device.
    @Test fun h1_favoritesAfterExpiryBelongToNewUser() {
        val be = FakeBackend(); val app = AppGraph(be)
        app.session.login("ana@example.com", "secret"); app.favorites.favorites()
        be.revokeAll("u1")
        app.profile.profile() // any call gets 401 -> expired
        assertFalse(app.session.isLoggedIn)
        app.session.login("ben@example.com", "secret")
        assertEquals(listOf("r20"), app.favorites.favorites())
    }

    // H2: same path, recent searches.
    @Test fun h2_searchesClearedAfterExpiry() {
        val be = FakeBackend(); val app = AppGraph(be)
        app.session.login("ana@example.com", "secret"); app.searches.add("jazz")
        be.revokeAll("u1"); app.favorites.favorites()
        app.session.login("ben@example.com", "secret")
        assertEquals(emptyList(), app.searches.all())
    }

    // H3: sibling cache, normal logout.
    @Test fun h3_profileAfterLogoutBelongsToNewUser() {
        val app = AppGraph(FakeBackend())
        app.session.login("ana@example.com", "secret"); app.profile.profile()
        app.session.logout()
        app.session.login("ben@example.com", "secret")
        assertEquals("u2", app.profile.profile()?.userId)
    }

    // H4: sibling cache, expiry path.
    @Test fun h4_profileAfterExpiryBelongsToNewUser() {
        val be = FakeBackend(); val app = AppGraph(be)
        app.session.login("ana@example.com", "secret"); app.profile.profile()
        be.revokeAll("u1"); app.favorites.favorites()
        app.session.login("ben@example.com", "secret")
        assertEquals("u2", app.profile.profile()?.userId)
    }

    // Guards: nothing broken.
    @Test fun g1_sameUserAgainAfterLogout() {
        val app = AppGraph(FakeBackend())
        app.session.login("ana@example.com", "secret"); app.favorites.favorites()
        app.session.logout()
        assertTrue(app.session.login("ana@example.com", "secret"))
        assertEquals(listOf("r10", "r11"), app.favorites.favorites())
        assertEquals("u1", app.profile.profile()?.userId)
    }

    @Test fun g2_cacheStillServesWithinSession() {
        val be = FakeBackend(); val app = AppGraph(be)
        app.session.login("ana@example.com", "secret"); app.favorites.favorites()
        be.favs["u1"] = listOf("changed")
        assertEquals(listOf("r10", "r11"), app.favorites.favorites())
    }

    @Test fun g3_searchesKeptWithinSession() {
        val app = AppGraph(FakeBackend())
        app.session.login("ana@example.com", "secret")
        app.searches.add("jazz"); app.searches.add("rock")
        assertEquals(listOf("rock", "jazz"), app.searches.all())
    }
}
