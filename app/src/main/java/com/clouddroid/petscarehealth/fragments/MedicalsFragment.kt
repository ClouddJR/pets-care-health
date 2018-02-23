package com.clouddroid.petscarehealth.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.adapters.MedicalsRV
import com.clouddroid.petscarehealth.model.Animal
import com.clouddroid.petscarehealth.model.MedicalRecord
import com.clouddroid.petscarehealth.repositories.MedicalsRepository
import com.clouddroid.petscarehealth.viewmodels.AnimalViewModel
import kotlinx.android.synthetic.main.fragment_medicals.*
import kotlinx.android.synthetic.main.layout_content_main.*


/**
 * Created by arkadiusz on 16.02.18
 */

class MedicalsFragment : Fragment(), MedicalsRepository.MedicalsListListener {

    private var animalViewModel: AnimalViewModel? = null
    private val medicalsRepository = MedicalsRepository()
    private val medicalsAdapter = MedicalsRV()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_medicals, container, false)
    }

    override fun onStart() {
        super.onStart()
        connectWithViewModel()
        observeAnimalData()
        showFAB()
        setMedicalListener()
        displayRecyclerView()
        hideFABOnScroll()
    }

    private fun connectWithViewModel() {
        animalViewModel = activity?.let { ViewModelProviders.of(it).get(AnimalViewModel::class.java) }
    }


    private fun observeAnimalData() {
        animalViewModel?.getSelectedAnimal()?.observe(activity!!, Observer {
            it?.let {
                getMedicalsForAnimal(it)
                setCurrentAnimalInRV(it)
            }
        })
    }

    private fun getMedicalsForAnimal(animal: Animal) {
        medicalsRepository.getMedicalsForAnimal(animal.key)
    }

    private fun setCurrentAnimalInRV(animal: Animal) {
        medicalsAdapter.updateCurrentAnimal(animal)
    }

    private fun showFAB() {
        activity?.fabMenu?.showMenuButton(true)
    }

    private fun setMedicalListener() {
        medicalsRepository.setMedicalsListListener(this)
    }

    private fun displayRecyclerView() {
        medicalsRV.setHasFixedSize(true)
        medicalsRV.layoutManager = LinearLayoutManager(context)
        medicalsRV.adapter = medicalsAdapter
    }

    override fun onMedicalsLoaded(list: List<MedicalRecord>) {
        if (list.isEmpty()) {
            displayNoDataText()
        } else {
            hideNoDataText()
            medicalsAdapter.initMedicalsList(list)
            medicalsAdapter.notifyDataSetChanged()
        }
    }

    private fun displayNoDataText() {
        medicalsRV?.visibility = View.GONE
        noMedicalsTextView?.visibility = View.VISIBLE
    }

    private fun hideNoDataText() {
        noMedicalsTextView?.visibility = View.GONE
        medicalsRV?.visibility = View.VISIBLE
    }

    private fun hideFABOnScroll() {
        medicalsRV?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0) {
                    activity?.fabMenu?.hideMenuButton(true)
                } else if (dy < 0) {
                    activity?.fabMenu?.showMenuButton(true)
                }
            }
        })
    }
}