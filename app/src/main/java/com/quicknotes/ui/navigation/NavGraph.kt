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

object QuickNotesDestinations {
    const val NOTES_LIST = "notes_list"
    const val NOTE_EDIT_ROUTE = "note_edit?noteId={noteId}"
    const val SETTINGS = "settings"
    const val USERS_DEMO = "users_demo"

    fun noteEdit(noteId: Long? = null) =
        if (noteId != null) "note_edit?noteId=$noteId" else "note_edit"
}

@Composable
fun QuickNotesNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = QuickNotesDestinations.NOTES_LIST) {
        composable(QuickNotesDestinations.NOTES_LIST) {
            NotesListScreen(
                onNoteClick = { id -> navController.navigate(QuickNotesDestinations.noteEdit(id)) },
                onAddClick = { navController.navigate(QuickNotesDestinations.noteEdit()) },
                onOpenUsersDemo = { navController.navigate(QuickNotesDestinations.USERS_DEMO) }
            )
        }
        composable(
            route = QuickNotesDestinations.NOTE_EDIT_ROUTE,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            NoteEditScreen(
                onSave = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(QuickNotesDestinations.SETTINGS) { Text("Settings — coming soon") }
        composable(QuickNotesDestinations.USERS_DEMO) {
            UsersScreen(onBack = { navController.popBackStack() })
        }
    }
}
