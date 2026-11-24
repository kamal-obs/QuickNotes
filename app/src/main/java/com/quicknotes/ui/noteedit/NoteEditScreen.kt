package com.quicknotes.ui.noteedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quicknotes.ui.theme.Spacing
import kotlinx.coroutines.launch

// SCREEN: create / edit a note. Route: NOTE_EDIT_ROUTE in ui/navigation/NavGraph.kt.
// Reached from NotesListScreen (tap a card, or tap the + button).
// onSave / onBack both pop back to the list (see NavGraph.kt).
// Form state lives in NoteEditViewModel.kt, not in local useState-style state.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    onSave: () -> Unit,
    onBack: () -> Unit,
    viewModel: NoteEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isNew) "New note" else "Edit note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.snippet,
                onValueChange = viewModel::onSnippetChange,
                label = { Text("Note") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                supportingText = { Text("${uiState.snippet.length} characters") }
                // TASK 6 — slot parameters: label / supportingText take a composable LAMBDA,
                //   not a string. Like passing JSX as a prop: label={<Text>Note</Text>}.
                // Updates live: each keystroke → onSnippetChange → ViewModel updates uiState
                //   → Compose re-runs this screen with the new snippet → new length shown.
            )
            OutlinedTextField(
                value = uiState.tag,
                onValueChange = viewModel::onTagChange,
                label = { Text("Tag") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    scope.launch {
                        viewModel.save()
                        onSave()
                    }
                },
                enabled = uiState.title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
