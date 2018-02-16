package com.clouddroid.pettypetscarehealth.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.adapters.NotesRV
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.Note
import com.clouddroid.pettypetscarehealth.repositories.NotesRepository
import com.clouddroid.pettypetscarehealth.viewmodels.AnimalViewModel
import kotlinx.android.synthetic.main.fragment_notes.*

/**
 * Created by arkadiusz on 15.02.18.
 */
class NotesFragment : Fragment(), NotesRepository.NotesListListener {

    private var animalViewModel: AnimalViewModel? = null
    private val notesRepository = NotesRepository()
    private val notesAdapter = NotesRV()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onStart() {
        super.onStart()
        connectWithViewModel()
        observeAnimalData()
        setNoteListener()
        displayRecyclerView()
    }

    private fun connectWithViewModel() {
        animalViewModel = activity?.let { ViewModelProviders.of(it).get(AnimalViewModel::class.java) }
    }


    private fun observeAnimalData() {
        animalViewModel?.getSelectedAnimal()?.observe(activity!!, Observer {
            it?.let {
                getNotesForAnimal(it)
                setCurrentAnimalInRV(it)
            }
        })
    }

    private fun getNotesForAnimal(animal: Animal) {
        notesRepository.getNotesForAnimal(animal.key)
    }

    private fun setCurrentAnimalInRV(animal: Animal) {
        notesAdapter.updateCurrentAnimal(animal)
    }

    private fun setNoteListener() {
        notesRepository.setNotesListListener(this)
    }

    private fun displayRecyclerView() {
        notesRV.setHasFixedSize(true)
        notesRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesRV.adapter = notesAdapter
    }

    override fun onNotesLoaded(list: List<Note>) {
        notesAdapter.initNotesList(list)
        notesAdapter.notifyDataSetChanged()
    }


}