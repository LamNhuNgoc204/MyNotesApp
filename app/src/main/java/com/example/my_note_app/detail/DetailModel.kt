package com.example.my_note_app.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.my_note_app.models.Notes
import com.example.my_note_app.repository.StorageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class DetailModel(
    private val repository: StorageRepository = StorageRepository()
): ViewModel(){
    var detailState by mutableStateOf(DetailState())
        private set

    private val hasUser:Boolean
        get()= repository.hasUser()

    private val user:FirebaseUser?
        get() = repository.user()

    fun handleChangeColor(colorId: Int){
        detailState = detailState.copy(colorId = colorId)
    }

    fun handleChangeTitle(title: String){
        detailState = detailState.copy(title = title)
    }

    fun handleChangeNote(note: String){
        detailState = detailState.copy(note = note)
    }

    fun addNote() {
        if(hasUser){
            repository.addNote(
                userId = user!!.uid,
                title = detailState.title,
                desription = detailState.note,
                colorId = detailState.colorId,
                timestamp = Timestamp.now()
            ){
                detailState = detailState.copy(noteStatus = it)
            }
        }
    }

    fun setEditFieds(note: Notes) {
        detailState = detailState.copy(
            colorId = note.colorId,
            title = note.title,
            note = note.desription
        )
    }

    fun getNote(noteId: String){
        repository.getNote(noteId = noteId, onError = {}){
            detailState = detailState.copy(selectedNote = it)
            detailState.selectedNote?.let { it1 -> setEditFieds(it1) }
        }
    }

    fun updateNote(noteId: String) {
        repository.updateNote(
            title = detailState.title,
            note = detailState.note,
            noteId = noteId,
            colorId = detailState.colorId
        ){
            detailState = detailState.copy(updateNoteStatus = it)
        }
    }

    fun resetNoteStatus(){
        detailState = detailState.copy(noteStatus = false, updateNoteStatus = false)
    }

    fun resetState() {
        detailState = DetailState()
    }

}

data class DetailState(
    val colorId: Int = 0,
    val title: String = "",
    val note: String = "",
    val noteStatus:Boolean = false,
    val updateNoteStatus:Boolean = false,
    val selectedNote: Notes? = null
)