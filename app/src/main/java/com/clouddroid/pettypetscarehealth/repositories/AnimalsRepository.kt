package com.clouddroid.pettypetscarehealth.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by Arkadiusz on 25.11.2017
 */
class AnimalsRepository {

    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val databaseReference = database.getReference(mAuth.currentUser?.uid)

    fun addNewAnimal(imageUri: Uri, name: String, date: String, breed: String, color: String, gender: String, type: String) {
        databaseReference.child("animals").child(name).child("imageUri").setValue(imageUri.toString())
        databaseReference.child("animals").child(name).child("name").setValue(name)
        databaseReference.child("animals").child(name).child("date").setValue(date)
        databaseReference.child("animals").child(name).child("breed").setValue(breed)
        databaseReference.child("animals").child(name).child("color").setValue(color)
        databaseReference.child("animals").child(name).child("gender").setValue(gender)
        databaseReference.child("animals").child(name).child("type").setValue(type)
    }

    fun getAnimals() {
        val animalListener = object : ValueEventListener {
            override fun onDataChange(animalsList: DataSnapshot?) {
                animalsList?.let {
                    for (animal in animalsList.children) {
                        Log.d("animal", animal.toString())
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        }

        databaseReference.child("animals").addValueEventListener(animalListener)
    }


}