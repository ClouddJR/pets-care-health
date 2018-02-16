package com.clouddroid.pettypetscarehealth.repositories

import com.clouddroid.pettypetscarehealth.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by arkadiusz on 14.02.18.
 */
class NotesRepository {

    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val databaseReference = database.getReference(mAuth.currentUser?.uid ?: "none")

    private var notesListener: NotesListListener? = null

    interface NotesListListener {
        fun onNotesLoaded(list: List<Note>)
    }

    fun setNotesListListener(listener: NotesListListener) {
        notesListener = listener
    }

    fun addNewNote(animalKey: String, date: String, title: String, color: Int, content: String) {
        val key = databaseReference.push().key
        databaseReference.child("notes").child(animalKey).child(key).child("key").setValue(key)
        databaseReference.child("notes").child(animalKey).child(key).child("title").setValue(title)
        databaseReference.child("notes").child(animalKey).child(key).child("date").setValue(date)
        databaseReference.child("notes").child(animalKey).child(key).child("color").setValue(color)
        databaseReference.child("notes").child(animalKey).child(key).child("content").setValue(content)
    }

    fun editNote(animalKey: String, noteKey: String, date: String, title: String, color: Int, content: String) {
        databaseReference.child("notes").child(animalKey).child(noteKey).child("key").setValue(noteKey)
        databaseReference.child("notes").child(animalKey).child(noteKey).child("title").setValue(title)
        databaseReference.child("notes").child(animalKey).child(noteKey).child("date").setValue(date)
        databaseReference.child("notes").child(animalKey).child(noteKey).child("color").setValue(color)
        databaseReference.child("notes").child(animalKey).child(noteKey).child("content").setValue(content)
    }

    fun deleteNote(animalKey: String, noteKey: String) {
        databaseReference.child("notes").child(animalKey).child(noteKey).removeValue()
    }

    fun getNotesForAnimal(animalKey: String) {
        databaseReference.child("notes").child(animalKey).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                // not used
            }

            override fun onDataChange(notesReceived: DataSnapshot?) {
                val notesTempList = mutableListOf<Note>()
                notesReceived?.children?.let { it.mapTo(notesTempList) { it.getValue(Note::class.java) as Note } }
                notesListener?.onNotesLoaded(notesTempList)
            }


        })
    }
}