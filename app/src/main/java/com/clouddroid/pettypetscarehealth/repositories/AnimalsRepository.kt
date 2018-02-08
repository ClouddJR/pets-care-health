package com.clouddroid.pettypetscarehealth.repositories

import android.net.Uri
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.MeasurementValue
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

    //animalListListener from AnimalViewModel
    private var animalListListener: AnimalListListener? = null

    private var heightValuesListener: HeightValuesListener? = null
    private var weightValuesListener: WeightValuesListener? = null


    interface AnimalListListener {
        fun onAnimalsListLoaded(list: List<Animal>)
    }

    interface HeightValuesListener {
        fun onHeightValuesLoaded(list: List<MeasurementValue>)
    }

    interface WeightValuesListener {
        fun onWeightValuesLoaded(list: List<MeasurementValue>)
    }

    fun setAnimalsListListener(listener: AnimalListListener) {
        this.animalListListener = listener
    }

    fun setHeightValuesListener(listener: HeightValuesListener) {
        this.heightValuesListener = listener
    }

    fun setWeightValuesListener(listener: WeightValuesListener) {
        this.weightValuesListener = listener
    }

    fun addNewAnimal(imageUri: Uri, name: String, date: String, breed: String, color: String, gender: String, type: String) {
        databaseReference.child("animals").child(name).child("imageUri").setValue(imageUri.path)
        databaseReference.child("animals").child(name).child("name").setValue(name)
        databaseReference.child("animals").child(name).child("date").setValue(date)
        databaseReference.child("animals").child(name).child("breed").setValue(breed)
        databaseReference.child("animals").child(name).child("color").setValue(color)
        databaseReference.child("animals").child(name).child("gender").setValue(gender)
        databaseReference.child("animals").child(name).child("type").setValue(type)
    }

    fun getAnimals() {
        databaseReference.child("animals").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                // not used
            }

            override fun onDataChange(animalsReceived: DataSnapshot?) {
                val animalsTempList = mutableListOf<Animal>()
                animalsReceived?.children?.let { it.mapTo(animalsTempList) { it.getValue(Animal::class.java) as Animal } }
                animalListListener?.onAnimalsListLoaded(animalsTempList)
            }


        })
    }

    fun addNewHeightValue(xValue: String, yValue: Float, animalName: String, animalDate: String) {
        databaseReference.child("measurements").child("heights").child("$animalName : $animalDate").child("value " + xValue).child("x").setValue(xValue)
        databaseReference.child("measurements").child("heights").child("$animalName : $animalDate").child("value " + xValue).child("y").setValue(yValue)
    }

    fun addNewWeightValue(xValue: String, yValue: Float, animalName: String, animalDate: String) {
        databaseReference.child("measurements").child("weights").child("$animalName : $animalDate").child("value " + xValue).child("x").setValue(xValue)
        databaseReference.child("measurements").child("weights").child("$animalName : $animalDate").child("value " + xValue).child("y").setValue(yValue)
    }

    fun deleteHeightValue(xValue: String, animalName: String, animalDate: String) {
        databaseReference.child("measurements").child("heights").child("$animalName : $animalDate").child("value " + xValue).removeValue()
    }

    fun deleteWeightValue(xValue: String, animalName: String, animalDate: String) {
        databaseReference.child("measurements").child("weights").child("$animalName : $animalDate").child("value " + xValue).removeValue()
    }

    fun getHeightValuesForAnimal(animalName: String, animalDate: String) {
        databaseReference.child("measurements").child("heights").child("$animalName : $animalDate").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                //not used
            }

            override fun onDataChange(measurementsReceived: DataSnapshot?) {
                val measurementTempList = mutableListOf<MeasurementValue>()
                measurementsReceived?.children?.let { it.mapTo(measurementTempList) { it.getValue(MeasurementValue::class.java) as MeasurementValue } }
                heightValuesListener?.onHeightValuesLoaded(measurementTempList)
            }
        })
    }

    fun getWeightValuesForAnimal(animalName: String, animalDate: String) {
        databaseReference.child("measurements").child("weights").child("$animalName : $animalDate").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                //not used
            }

            override fun onDataChange(measurementsReceived: DataSnapshot?) {
                val measurementTempList = mutableListOf<MeasurementValue>()
                measurementsReceived?.children?.let { it.mapTo(measurementTempList) { it.getValue(MeasurementValue::class.java) as MeasurementValue } }
                weightValuesListener?.onWeightValuesLoaded(measurementTempList)
            }
        })
    }

}