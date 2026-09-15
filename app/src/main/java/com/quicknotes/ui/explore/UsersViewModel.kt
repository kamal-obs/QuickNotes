package com.quicknotes.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quicknotes.data.remote.JsonPlaceholderApi
import com.quicknotes.data.remote.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed interface UsersUiState {
    data object Loading : UsersUiState
    data class Success(val users: List<UserDto>) : UsersUiState
    data class Error(val message: String) : UsersUiState
}

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val api: JsonPlaceholderApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UsersUiState.Loading
            _uiState.value = try {
                UsersUiState.Success(api.getUsers())
            } catch (e: IOException) {
                UsersUiState.Error(e.message ?: "Network error")
            } catch (e: HttpException) {
                UsersUiState.Error("Server error: ${e.code()}")
            }
        }
    }
}
