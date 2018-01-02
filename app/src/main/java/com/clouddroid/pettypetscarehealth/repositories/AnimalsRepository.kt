package com.clouddroid.pettypetscarehealth.repositories

import android.net.Uri
import com.clouddroid.pettypetscarehealth.model.Animal
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
    private val databaseReference = database.getReference(mAuth.currentUser?.uid ?: "none")

    //listener from AnimalViewModel
    private var listener: AnimalListListener? = null

    interface AnimalListListener {
        fun onSuccessLoaded(list: List<Animal>)
    }

    fun setListener(listener: AnimalListListener) {
        this.listener = listener
    }

    fun addNewAnimal(imageUri: Uri, name: String, date: String, breed: String, color: String, gender: String, type: String) {
        databaseReference.child("animals").child(name).child("imageUri").setValue(imageUri.toString())
        databaseReference.child("animals").child(name).child("name").setValue(name)
        databaseReference.child("animals").child(name).child("date").setValue(date)
        databaseReference.child("animals").child(name).child("breed").setValue(breed)
        databaseReference.child("animals").child(name).child("color").setValue(color)
        databaseReference.child("animals").child(name).child("gender").setValue(gender)
        databaseReference.child("animals").child(name).child("type").setValue(type)
    }

    fun addNewChartValue(xValue: String, yValue: Float, animalName: String, animalDate: String) {
        databaseReference.child("measurements").child("$animalName : $animalDate").child("value").child(xValue).setValue(yValue)
    }

    fun getAnimals() {
        databaseReference.child("animals").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                // not used
            }

            override fun onDataChange(animalsReceived: DataSnapshot?) {
                val animalsTempList = mutableListOf<Animal>()
                animalsReceived?.children?.let { it.mapTo(animalsTempList) { it.getValue(Animal::class.java) as Animal } }
                listener?.onSuccessLoaded(animalsTempList)
            }


        })
    }

}