package com.clouddroid.pettypetscarehealth.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.adapters.MeasurementValuesRV
import com.clouddroid.pettypetscarehealth.dialogs.ValuesDialog
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.MeasurementValue
import com.clouddroid.pettypetscarehealth.repositories.AnimalsRepository
import com.clouddroid.pettypetscarehealth.viewmodels.AnimalViewModel
import kotlinx.android.synthetic.main.fragment_info.*
import java.io.File


class InfoFragment : Fragment(), AnimalsRepository.HeightValuesListener, AnimalsRepository.WeightValuesListener {

    private var animalViewModel: AnimalViewModel? = null
    private val animalsRepository = AnimalsRepository()
    private var currentAnimal: Animal? = null
    private val heightAdapter = MeasurementValuesRV()
    private val weightAdapter = MeasurementValuesRV()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onStart() {
        super.onStart()
        connectWithViewModel()
        observeAnimalData()
        initValuesListener()
        initMoreButtonsClickListeners()
        displayRecyclerView()
    }

    private fun connectWithViewModel() {
        animalViewModel = activity?.let { ViewModelProviders.of(it).get(AnimalViewModel::class.java) }
    }


    private fun observeAnimalData() {
        animalViewModel?.getSelectedAnimal()?.observe(activity!!, Observer {

            if (isAnimalEmpty(it)) {
                hideMainView()
            } else {
                it?.let {
                    showMainView()
                    updateCurrentAnimal(it)
                    updateImage(it)
                    updateGeneralInfo(it)
                    updateOtherInfo(it)
                    updateMeasurementValues(it)
                }
            }

        })
    }

    private fun isAnimalEmpty(animal: Animal?): Boolean {
        return animal?.name?.equals("") ?: true
    }

    private fun showMainView() {
        main_scroll_view.visibility = View.VISIBLE
    }

    private fun hideMainView() {
        main_scroll_view.visibility = View.GONE
    }

    private fun updateCurrentAnimal(chosenAnimal: Animal) {
        currentAnimal = chosenAnimal
    }

    private fun updateImage(pet: Animal) {
        if (petImageView != null && pet.imageUri != "") {
            Glide.with(context!!).load(File(pet.imageUri)).into(petImageView)
        }
    }

    private fun updateGeneralInfo(pet: Animal) {
        petNameTextView?.text = pet.name
        petDateTextView?.text = pet.date
        petTypeTextView?.text = pet.type
    }

    private fun updateOtherInfo(pet: Animal) {
        petBreedTextView?.text = pet.breed
        petColorTextView?.text = pet.color
        petGenderTextView?.text = pet.gender
    }

    private fun updateMeasurementValues(pet: Animal) {
        animalsRepository.getHeightValuesForAnimal(pet.key)
        animalsRepository.getWeightValuesForAnimal(pet.key)
    }


    private fun initValuesListener() {
        animalsRepository.setHeightValuesListener(this)
        animalsRepository.setWeightValuesListener(this)
    }

    private fun initMoreButtonsClickListeners() {
        moreButtonHeight.setOnClickListener {
            displayHeightDialog()
        }

        moreButtonWeight.setOnClickListener {
            displayWeightDialog()
        }
    }

    private fun displayRecyclerView() {
        heightRV.setHasFixedSize(true)
        heightRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        heightRV.adapter = heightAdapter

        weightRV.setHasFixedSize(true)
        weightRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        weightRV.adapter = weightAdapter
    }

    private fun displayHeightDialog() {
        val dialog = ValuesDialog(context, R.style.ChartValuesDialog)
        dialog.setValuesType("height")
        dialog.initSelectedAnimalAndGetValues(currentAnimal)
        dialog.show()
    }

    private fun displayWeightDialog() {
        val dialog = ValuesDialog(context, R.style.ChartValuesDialog)
        dialog.setValuesType("weight")
        dialog.initSelectedAnimalAndGetValues(currentAnimal)
        dialog.show()
    }

    override fun onHeightValuesLoaded(list: List<MeasurementValue>) {
        heightAdapter.initValuesList(list)
        heightAdapter.notifyDataSetChanged()
    }

    override fun onWeightValuesLoaded(list: List<MeasurementValue>) {
        weightAdapter.initValuesList(list)
        weightAdapter.notifyDataSetChanged()
    }

}


