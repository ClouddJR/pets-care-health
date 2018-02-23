package com.clouddroid.petscarehealth.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.clouddroid.petscarehealth.model.Animal
import com.clouddroid.petscarehealth.repositories.AnimalsRepository

/**
 * Created by Arkadiusz on 17.12.2017
 */

class AnimalViewModel : ViewModel(), AnimalsRepository.AnimalListListener {

    private var animalsList: MutableLiveData<List<Animal>>? = null
    private var selectedAnimal: MutableLiveData<Animal> = MutableLiveData()

    private val animalsRepository = AnimalsRepository()

    fun initialize() {
        animalsRepository.setAnimalsListListener(this)
    }

    fun getAnimalsList(): LiveData<List<Animal>>? {
        if (animalsList == null) {
            animalsList = MutableLiveData()
            animalsRepository.getAnimals()
        }
        return animalsList
    }

    override fun onAnimalsListLoaded(list: List<Animal>) {
        animalsList?.value = list
    }

    fun setSelectedAnimal(animal: Animal) {
        selectedAnimal.value = animal
    }

    fun getSelectedAnimal(): LiveData<Animal> {
        return selectedAnimal
    }

}