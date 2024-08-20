package com.example.my_note_app.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_note_app.models.Notes
import com.example.my_note_app.repository.Resources
import com.example.my_note_app.repository.StorageRepository
import kotlinx.coroutines.launch

class HomeModel(
    private val repository: StorageRepository = StorageRepository()
):ViewModel() {
    var homeState by mutableStateOf(HomeState())

    val user = repository.user()
    val hasUser:Boolean
        get() = repository.hasUser()

    private val userId: String
        get() = repository.getUserId().toString()

    fun loadNotes() {
        if(hasUser){
            if(userId.isNotBlank()){
                getUserNotes(userId)
            }
        }else{
            homeState = homeState.copy(notesList = Resources.Error(
                throwable = Throwable(message = "User not login")
            ))
        }
    }

    private fun getUserNotes(userId: String) = viewModelScope.launch {
        repository.getUserNotes(userId).collect{
            homeState = homeState.copy(notesList = it)
        }
    }

    fun deleteNote(noteId: String) = repository.deleteNote(noteId){
        homeState = homeState.copy(deleteNoteStatus = it)
    }

    fun signOut() = repository.signOut()

}

data class HomeState(
    val notesList:Resources<List<Notes>> = Resources.Loading(),
    val deleteNoteStatus: Boolean = false,
)