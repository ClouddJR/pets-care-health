package com.clouddroid.pettypetscarehealth.repositories

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

    fun addNewAnimal(key: String, imagePath: String, imageCachePath: String, name: String, date: String, breed: String, color: String, gender: String, type: String) {
        databaseReference.child("animals").child(key).child("key").setValue(key)
        databaseReference.child("animals").child(key).child("imagePath").setValue(imagePath)
        databaseReference.child("animals").child(key).child("imageCachePath").setValue(imageCachePath)
        databaseReference.child("animals").child(key).child("name").setValue(name)
        databaseReference.child("animals").child(key).child("date").setValue(date)
        databaseReference.child("animals").child(key).child("breed").setValue(breed)
        databaseReference.child("animals").child(key).child("color").setValue(color)
        databaseReference.child("animals").child(key).child("gender").setValue(gender)
        databaseReference.child("animals").child(key).child("type").setValue(type)
    }

    fun generateKey(): String {
        return databaseReference.push().key
    }

    fun editAnimal(key: String, imagePath: String, imageCachePath: String, name: String, date: String, breed: String, color: String, gender: String, type: String) {
        databaseReference.child("animals").child(key).child("key").setValue(key)
        databaseReference.child("animals").child(key).child("imagePath").setValue(imagePath)
        databaseReference.child("animals").child(key).child("imageCachePath").setValue(imageCachePath)
        databaseReference.child("animals").child(key).child("name").setValue(name)
        databaseReference.child("animals").child(key).child("date").setValue(date)
        databaseReference.child("animals").child(key).child("breed").setValue(breed)
        databaseReference.child("animals").child(key).child("color").setValue(color)
        databaseReference.child("animals").child(key).child("gender").setValue(gender)
        databaseReference.child("animals").child(key).child("type").setValue(type)
    }

    fun deleteAnimal(animal: Animal?) {
        databaseReference.child("animals").child(animal?.key).removeValue()
        databaseReference.child("measurements").child("weights").child(animal?.key).removeValue()
        databaseReference.child("measurements").child("heights").child(animal?.key).removeValue()
        databaseReference.child("notes").child(animal?.key).removeValue()
        databaseReference.child("medicals").child(animal?.key).removeValue()
        ImagesRepository().deleteImage(animal?.imagePath ?: "")
        ImagesRepository().deleteImagesForAnimal(animal?.key ?: "")
        databaseReference.child("images").child(animal?.key).removeValue()
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

    fun addNewHeightValue(xValue: String, yValue: Float, key: String) {
        databaseReference.child("measurements").child("heights").child(key).child("value " + xValue).child("x").setValue(xValue)
        databaseReference.child("measurements").child("heights").child(key).child("value " + xValue).child("y").setValue(yValue)
    }

    fun addNewWeightValue(xValue: String, yValue: Float, key: String) {
        databaseReference.child("measurements").child("weights").child(key).child("value " + xValue).child("x").setValue(xValue)
        databaseReference.child("measurements").child("weights").child(key).child("value " + xValue).child("y").setValue(yValue)
    }

    fun deleteHeightValue(xValue: String, key: String) {
        databaseReference.child("measurements").child("heights").child(key).child("value " + xValue).removeValue()
    }

    fun deleteWeightValue(xValue: String, key: String) {
        databaseReference.child("measurements").child("weights").child(key).child("value " + xValue).removeValue()
    }

    fun getHeightValuesForAnimal(key: String) {
        databaseReference.child("measurements").child("heights").child(key).addValueEventListener(object : ValueEventListener {
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

    fun getWeightValuesForAnimal(key: String) {
        databaseReference.child("measurements").child("weights").child(key).addValueEventListener(object : ValueEventListener {
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