package com.quicknotes.ui.noteedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quicknotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteEditUiState(
    val title: String = "",
    val snippet: String = "",
    val tag: String = "",
    val isNew: Boolean = true
)

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NotesRepository
) : ViewModel() {

    private val noteId: Long? = savedStateHandle.get<Long>("noteId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(NoteEditUiState(isNew = noteId == null))
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    init {
        noteId?.let { id ->
            viewModelScope.launch {
                repository.getById(id)?.let { note ->
                    _uiState.update {
                        it.copy(title = note.title, snippet = note.snippet, tag = note.tag)
                    }
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onSnippetChange(value: String) = _uiState.update { it.copy(snippet = value) }
    fun onTagChange(value: String) = _uiState.update { it.copy(tag = value) }

    suspend fun save() {
        val state = _uiState.value
        repository.save(noteId, state.title, state.snippet, state.tag)
    }
}
