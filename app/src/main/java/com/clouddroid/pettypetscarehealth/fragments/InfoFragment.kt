package com.clouddroid.pettypetscarehealth.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.repositories.AnimalsRepository
import com.clouddroid.pettypetscarehealth.viewmodels.AnimalViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_info.*
import java.io.File


class InfoFragment : Fragment() {

    private var animalViewModel: AnimalViewModel? = null
    private val animalsRepository = AnimalsRepository()
    private var currentAnimal: Animal? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onStart() {
        super.onStart()
        connectWithViewModel()
        observeAnimalData()
        initCharts()
    }

    private fun connectWithViewModel() {
        animalViewModel = activity?.let { ViewModelProviders.of(it).get(AnimalViewModel::class.java) }
    }

    private fun observeAnimalData() {
        animalViewModel?.getSelectedAnimal()?.observe(activity!!, Observer {
            it?.let {
                updateCurrentAnimal(it)
                updateImage(it)
                updateGeneralInfo(it)
                updateOtherInfo(it)
            }
        })
    }

    private fun updateCurrentAnimal(chosenAnimal: Animal) {
        currentAnimal = chosenAnimal
    }

    private fun updateImage(pet: Animal) {
        if (petImageView != null && pet.imageUri != "") {
            Picasso.with(context).load(File(pet.imageUri)).resize(0, 700).into(petImageView)
        } else if (petImageView != null) {
            Picasso.with(context).load(R.drawable.paw).resize(0, 700).into(petImageView)
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

    private fun initCharts() {
        currentAnimal?.let {
            animalsRepository.addNewChartValue("25-05", 42f, it.name, it.date)
            animalsRepository.addNewChartValue("04-12", 55f, it.name, it.date)
        }
        val entries = arrayListOf(Entry(1f, 40f),
                Entry(2f, 42f),
                Entry(3f, 45f),
                Entry(4f, 50f),
                Entry(5f, 47f),
                Entry(6f, 52f),
                Entry(7f, 60f))
        val dataSet = LineDataSet(entries, "Height")
        dataSet.color = Color.BLUE
        val lineData = LineData(dataSet)
        //heightChar.data = lineData
        heightChar.setNoDataText(getString(R.string.info_fragment_height_chart_no_data))
        heightChar.invalidate()

        val dataSet2 = LineDataSet(entries, "Weight")
        dataSet2.color = Color.RED
        val lineData2 = LineData(dataSet2)
        //weightChar.data = lineData2
        weightChar.setNoDataText(getString(R.string.info_fragment_weight_chart_no_data))
        weightChar.invalidate()

    }


}

