package com.clouddroid.pettypetscarehealth.repositories

import android.util.Log.d
import com.clouddroid.pettypetscarehealth.model.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by arkadiusz on 19.02.18.
 */
class RemindersRepository {

    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val databaseReference = database.getReference(mAuth.currentUser?.uid ?: "none")

    private var remindersListener: RemindersListListener? = null

    interface RemindersListListener {
        fun onRemindersLoaded(list: List<Reminder>)
    }

    fun setRemindersListListener(listener: RemindersListListener) {
        remindersListener = listener
    }

    fun addNewReminder(animalKey: String, reminder: Reminder) {
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("key").setValue(reminder.key)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("text").setValue(reminder.text)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("year").setValue(reminder.year)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("month").setValue(reminder.month)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("day").setValue(reminder.day)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("hour").setValue(reminder.hour)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("minute").setValue(reminder.minute)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("numberIntervals").setValue(reminder.numberIntervals)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("typeInterval").setValue(reminder.typeInterval)
    }

    fun editReminder(animalKey: String, reminder: Reminder) {
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("key").setValue(reminder.key)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("text").setValue(reminder.text)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("year").setValue(reminder.year)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("month").setValue(reminder.month)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("day").setValue(reminder.day)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("hour").setValue(reminder.hour)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("minute").setValue(reminder.minute)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("numberIntervals").setValue(reminder.numberIntervals)
        databaseReference.child("reminders").child(animalKey).child(reminder.key).child("typeInterval").setValue(reminder.typeInterval)
    }

    fun generateKey(): String {
        return databaseReference.push().key
    }

    fun deleteReminder(animalKey: String, reminderKey: String) {
        databaseReference.child("reminders").child(animalKey).child(reminderKey).removeValue()
    }

    fun getRemindersForAnimal(animalKey: String) {
        databaseReference.child("reminders").child(animalKey).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                // not used
            }

            override fun onDataChange(remindersReceived: DataSnapshot?) {
                val remindersTempList = mutableListOf<Reminder>()
                remindersReceived?.children?.let { it.mapTo(remindersTempList) { it.getValue(Reminder::class.java) as Reminder } }
                remindersListener?.onRemindersLoaded(remindersTempList)
                d("remindersLoaded", remindersTempList.toString())
            }


        })
    }

}