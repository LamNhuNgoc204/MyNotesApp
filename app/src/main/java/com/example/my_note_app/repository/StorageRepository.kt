package com.example.my_note_app.repository

import com.example.my_note_app.models.Notes
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val NOTES_COLLECTION_REF = "notes"

class StorageRepository() {
    fun user() = Firebase.auth.currentUser

    fun hasUser():Boolean = Firebase.auth.currentUser!= null

    fun getUserId(): String? = Firebase.auth.uid.orEmpty()

    private val notesRef:CollectionReference = Firebase.firestore.collection(NOTES_COLLECTION_REF)

    fun getUserNotes(userId: String ):Flow<Resources<List<Notes>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration ?= null

        try {
            snapshotStateListener = notesRef
                .orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener{ snapshot, e ->
                    val response = if(snapshot != null){
                        val notes = snapshot.toObjects(Notes::class.java)
                        Resources.Success(data = notes)
                    }else{
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)
                }

        }catch (e:Exception){
            trySend(Resources.Error(e?.cause))
            e.printStackTrace()
        }

        awaitClose(){
            snapshotStateListener?.remove()
        }
    }

    fun getNote(
        noteId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Notes?) -> Unit,
    ){
        notesRef.document(noteId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it.toObject(Notes::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }
    }

    fun addNote(
        userId: String = "",
        title: String = "",
        desription:String = "",
        timestamp: Timestamp = Timestamp.now(),
        colorId:Int = 0,
        onComplete:(Boolean) -> Unit
    ){
        val docId = notesRef.document().id
        val note = Notes(userId,title,desription,timestamp,colorId,docId)

        notesRef.document(docId)
            .set(note)
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun deleteNote(noteId: String = "", onComplete: (Boolean) -> Unit){
        notesRef.document(noteId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateNote(
        title: String,
        note: String,
        colorId: Int,
        noteId: String,
        onResult:(Boolean) -> Unit
    ){
        val data = hashMapOf<String,Any>(
            "colorIndex" to colorId,
            "description" to note,
            "title" to title
        )

        notesRef.document(noteId)
            .update(data)
            .addOnCompleteListener {
                onResult.invoke(it.isSuccessful)
            }
    }

    fun signOut() = Firebase.auth.signOut()
}


sealed class Resources<T>(val data: T? = null, var throwable: Throwable? = null) {
    class Loading<T>: Resources<T>()
    class Success<T>(data: T?): Resources<T>(data = data)
    class Error<T>(throwable: Throwable?): Resources<T>(throwable = throwable)
}