package app.circles.account

import app.circles.api.CirclesApi
import app.circles.session.Session

class AccountDeletion(private val api: CirclesApi, private val session: Session) {

    /** Called from Settings > Delete account, after the user confirms. */
    suspend fun deleteAccount() {
        api.deleteMe()          // server deletes profile, posts, messages (see server-jobs.md)
        session.clear()         // local tokens and cached profile
    }
}
