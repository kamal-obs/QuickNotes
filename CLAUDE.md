# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

QuickNotes — a small Compose notes app that exists purely as a learning vehicle for Jetpack Compose/Android (the user is an experienced RN/Node/NestJS dev, new to Kotlin/Android). It is intentionally minimal: don't add production-grade extras (analytics, CI, multi-module split, testing infra, etc.) unless explicitly asked. Prefer idiomatic, simple solutions over scalable ones, and explain Android/Compose concepts via RN/JS analogies where useful.

## Environment / build

This WSL directory has **no Java, Gradle, or Android SDK installed** — `gradlew` cannot run from this shell (`ANDROID_HOME` is unset here). The project is built and run from Android Studio on the Windows side, using the SDK at the path in `local.properties`. When verification is needed, ask the user to build/run in Android Studio rather than attempting `./gradlew` in this shell.

For reference, if run from a Windows shell with the SDK installed (`gradlew.bat`):
- `assembleDebug` — build debug APK
- `test` — run JVM unit tests (`app/src/test`)
- `connectedAndroidTest` — run instrumented tests on a device/emulator (`app/src/androidTest`)

No test sources exist yet (`app/src/test`, `app/src/androidTest` are both absent), even though JUnit/Espresso/Compose-test dependencies are declared in `app/build.gradle.kts`.

## Architecture

Single-activity Compose app (`MainActivity`) hosting one `NavHost` (`ui/navigation/NavGraph.kt`, routes in `QuickNotesDestinations`). Standard layered structure under `com.quicknotes`:

- **`data/local`** — Room: `NoteEntity`, `NoteDao`, `QuickNotesDatabase` (schema exported to `app/schemas/`).
- **`data/remote`** — Retrofit: `JsonPlaceholderApi` hits `jsonplaceholder.typicode.com` (practice API for the `explore/UsersScreen` demo, unrelated to notes CRUD).
- **`data/repository`** — `NotesRepository` interface + `NotesRepositoryImpl`; `NoteMappers.kt` converts `NoteEntity` (DB, epoch millis) to the UI-facing `Note` model (display date string). ViewModels depend only on the `NotesRepository` interface, never on `NoteDao` directly.
- **`di`** — Hilt modules, one per concern: `DatabaseModule` (Room), `NetworkModule` (OkHttp/Retrofit/Json), `RepositoryModule` (`@Binds` interface→impl).
- **`ui/<feature>`** — one package per screen (`notes`, `noteedit`, `explore`), each a `XScreen.kt` (Composable) + `XViewModel.kt` (`@HiltViewModel`) pair. `NotesListViewModel` exposes repository `Flow` as `StateFlow` via `stateIn`; `NoteEditViewModel` reads the nav arg via `SavedStateHandle` and holds local form state in a `MutableStateFlow<UiState>`; `UsersViewModel` models network state as a sealed `UsersUiState` (Loading/Success/Error) — this is the pattern to follow for any new async UI state.
- **`ui/theme`** — design tokens: `Spacing` (dp scale), `Color`/`Type`/`Shape`, and `TagColors` provided via `staticCompositionLocalOf` (`LocalTagColors`) rather than props — a deliberate example contrasting Compose's `CompositionLocal` with prop-drilling/React Context for the user's learning purposes. Don't "fix" this to be prop-based.

Data flow is one-directional: Room (`Flow`) → Repository (`Flow<Note>`) → ViewModel (`StateFlow`) → Composable (`collectAsState`); edits go the other way through repository `suspend fun`s, never DAO calls from the UI layer.
