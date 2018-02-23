package com.clouddroid.petscarehealth.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clouddroid.petscarehealth.model.Animal
import com.clouddroid.petscarehealth.model.Reminder
import com.clouddroid.petscarehealth.repositories.AnimalsRepository
import com.clouddroid.petscarehealth.repositories.RemindersRepository
import com.clouddroid.petscarehealth.utils.RemindersUtils

/**
 * Created by arkadiusz on 21.02.18
 */

class BootReceiver : BroadcastReceiver(), AnimalsRepository.AnimalListListener, RemindersRepository.RemindersListListener {

    private var passedContext: Context? = null
    private val animalsRepository = AnimalsRepository()
    private val remindersRepository = RemindersRepository()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals("android.intent.action.BOOT_COMPLETED")) {
            passedContext = context
            remindersRepository.setRemindersListListener(this)
            animalsRepository.setAnimalsListListener(this)
            animalsRepository.getAnimals()
        }
    }

    override fun onAnimalsListLoaded(list: List<Animal>) {
        for (animal in list) {
            remindersRepository.getRemindersForAnimal(animal.key)
        }
    }

    override fun onRemindersLoaded(list: List<Reminder>) {
        for (reminder in list) {
            RemindersUtils.addNewReminder(passedContext!!, reminder)
        }
    }
}