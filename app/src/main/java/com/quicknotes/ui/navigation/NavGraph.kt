package com.quicknotes.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quicknotes.ui.explore.UsersScreen
import com.quicknotes.ui.noteedit.NoteEditScreen
import com.quicknotes.ui.notes.NotesListScreen

// STARTUP PATH — STEP 3: navigation. THIS IS THE FILE TO OPEN TO FIND ANY SCREEN.
//
// Route names, like the screen names you'd pass to navigation.navigate("...") in React Navigation.
// `object` = a singleton, similar to `export const QuickNotesDestinations = { ... }` in JS.
object QuickNotesDestinations {
    const val NOTES_LIST = "notes_list"
    const val NOTE_EDIT_ROUTE = "note_edit?noteId={noteId}"
    const val SETTINGS = "settings"
    const val USERS_DEMO = "users_demo"

    fun noteEdit(noteId: Long? = null) =
        if (noteId != null) "note_edit?noteId=$noteId" else "note_edit"
}

// The navigator. Comparison with React Navigation:
//   NavHost(startDestination = X)   ~  <Stack.Navigator initialRouteName="X">
//   composable("route") { Screen() } ~  <Stack.Screen name="route" component={Screen} />
//   navController.navigate("route")  ~  navigation.navigate("route")
//   navController.popBackStack()     ~  navigation.goBack()
//
// Difference from RN: screens do NOT get a `navigation` prop. Each screen receives
// plain callbacks (onNoteClick, onBack, ...), and THIS file decides what those
// callbacks do. Screens stay unaware of navigation.
//
// To find a screen: find its route below → follow the function it calls
// (Ctrl+Click / Ctrl+B in Android Studio jumps to the definition).
@Composable
fun QuickNotesNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = QuickNotesDestinations.NOTES_LIST) {
        // Home screen (startDestination) → ui/notes/NotesListScreen.kt
        composable(QuickNotesDestinations.NOTES_LIST) {
            NotesListScreen(
                onNoteClick = { id -> navController.navigate(QuickNotesDestinations.noteEdit(id)) },
                onAddClick = { navController.navigate(QuickNotesDestinations.noteEdit()) },
                onOpenUsersDemo = { navController.navigate(QuickNotesDestinations.USERS_DEMO) }
            )
        }
        // Create/edit note → ui/noteedit/NoteEditScreen.kt
        // "noteId" is a route param (like route.params.noteId). -1 means "new note".
        // The screen's ViewModel reads it through SavedStateHandle, not from props.
        composable(
            route = QuickNotesDestinations.NOTE_EDIT_ROUTE,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            NoteEditScreen(
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        // Placeholder: nothing navigates here yet
        composable(QuickNotesDestinations.SETTINGS) { Text("Settings — coming soon") }
        // API demo (Retrofit) → ui/explore/UsersScreen.kt
        composable(QuickNotesDestinations.USERS_DEMO) {
            UsersScreen(onBack = { navController.popBackStack() })
        }
    }
}
