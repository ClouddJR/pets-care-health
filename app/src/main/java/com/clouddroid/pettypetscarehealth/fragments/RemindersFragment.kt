package com.clouddroid.pettypetscarehealth.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.adapters.RemindersRV
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.Reminder
import com.clouddroid.pettypetscarehealth.repositories.RemindersRepository
import com.clouddroid.pettypetscarehealth.viewmodels.AnimalViewModel
import kotlinx.android.synthetic.main.fragment_reminders.*
import kotlinx.android.synthetic.main.layout_content_main.*

/**
 * Created by arkadiusz on 19.02.18
 */

class RemindersFragment : Fragment(), RemindersRepository.RemindersListListener {

    private var animalViewModel: AnimalViewModel? = null
    private val remindersRepository = RemindersRepository()
    private val remindersAdapter = RemindersRV()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onStart() {
        super.onStart()
        connectWithViewModel()
        observeAnimalData()
        showFAB()
        setRemindersListener()
        displayRecyclerView()
        hideFABOnScroll()
    }

    private fun connectWithViewModel() {
        animalViewModel = activity?.let { ViewModelProviders.of(it).get(AnimalViewModel::class.java) }
    }


    private fun observeAnimalData() {
        animalViewModel?.getSelectedAnimal()?.observe(activity!!, Observer {
            it?.let {
                getRemindersForAnimal(it)
                setCurrentAnimalInRV(it)
            }
        })
    }

    private fun getRemindersForAnimal(animal: Animal) {
        remindersRepository.getRemindersForAnimal(animal.key)
    }

    private fun setCurrentAnimalInRV(animal: Animal) {
        remindersAdapter.updateCurrentAnimal(animal)
    }

    private fun showFAB() {
        activity?.fabMenu?.showMenuButton(true)
    }

    private fun setRemindersListener() {
        remindersRepository.setRemindersListListener(this)
    }

    private fun displayRecyclerView() {
        remindersRV.setHasFixedSize(true)
        remindersRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        remindersRV.adapter = remindersAdapter
    }

    override fun onRemindersLoaded(list: List<Reminder>) {
        if (list.isEmpty()) {
            displayNoDataText()
        } else {
            hideNoDataText()
            remindersAdapter.initRemindersList(list)
            remindersAdapter.notifyDataSetChanged()
        }
    }

    private fun displayNoDataText() {
        remindersRV?.visibility = View.GONE
        noRemindersTextView?.visibility = View.VISIBLE
    }

    private fun hideNoDataText() {
        noRemindersTextView?.visibility = View.GONE
        remindersRV?.visibility = View.VISIBLE
    }

    private fun hideFABOnScroll() {
        remindersRV?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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