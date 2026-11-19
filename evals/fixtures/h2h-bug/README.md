# Music

Account and personal data layer of the music app (Android). The UI calls `AppGraph`; screens read
favourites, recent searches and the profile from the repositories below.

- `android/src/main/kotlin/`: Kotlin/JVM code. `network/Backend` is the server interface.
- Run the tests with `./gradlew test`.

Keep the public API unchanged: `AppGraph(backend)`, `SessionManager.login/logout`,
`FavoritesRepository.favorites()`, `RecentSearchesStore.add/all`, `ProfileRepository.profile()`.
