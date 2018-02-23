package com.clouddroid.petscarehealth.repositories

import com.clouddroid.petscarehealth.model.MedicalRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by arkadiusz on 16.02.18.
 */
class MedicalsRepository {

    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val databaseReference = database.getReference(mAuth.currentUser?.uid ?: "none")

    private var medicalsListener: MedicalsListListener? = null

    interface MedicalsListListener {
        fun onMedicalsLoaded(list: List<MedicalRecord>)
    }

    fun setMedicalsListListener(listener: MedicalsListListener) {
        medicalsListener = listener
    }

    fun addNewMedical(animalKey: String, date: String, title: String, hospital: String, content: String) {
        val key = databaseReference.push().key
        databaseReference.child("medicals").child(animalKey).child(key).child("key").setValue(key)
        databaseReference.child("medicals").child(animalKey).child(key).child("title").setValue(title)
        databaseReference.child("medicals").child(animalKey).child(key).child("date").setValue(date)
        databaseReference.child("medicals").child(animalKey).child(key).child("hospital").setValue(hospital)
        databaseReference.child("medicals").child(animalKey).child(key).child("comment").setValue(content)
    }

    fun editMedical(animalKey: String, medicalKey: String, date: String, title: String, hospital: String, content: String) {
        databaseReference.child("medicals").child(animalKey).child(medicalKey).child("key").setValue(medicalKey)
        databaseReference.child("medicals").child(animalKey).child(medicalKey).child("title").setValue(title)
        databaseReference.child("medicals").child(animalKey).child(medicalKey).child("date").setValue(date)
        databaseReference.child("medicals").child(animalKey).child(medicalKey).child("hospital").setValue(hospital)
        databaseReference.child("medicals").child(animalKey).child(medicalKey).child("comment").setValue(content)
    }

    fun deleteMedical(animalKey: String, medicalKey: String) {
        databaseReference.child("medicals").child(animalKey).child(medicalKey).removeValue()
    }

    fun getMedicalsForAnimal(animalKey: String) {
        databaseReference.child("medicals").child(animalKey).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                // not used
            }

            override fun onDataChange(medicalsReceived: DataSnapshot?) {
                val medicalsTempList = mutableListOf<MedicalRecord>()
                medicalsReceived?.children?.let { it.mapTo(medicalsTempList) { it.getValue(MedicalRecord::class.java) as MedicalRecord } }
                medicalsListener?.onMedicalsLoaded(medicalsTempList)
            }


        })
    }
}