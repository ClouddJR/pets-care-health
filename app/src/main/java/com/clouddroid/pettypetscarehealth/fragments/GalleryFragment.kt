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
import com.clouddroid.pettypetscarehealth.adapters.ImagesRV
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.GalleryItem
import com.clouddroid.pettypetscarehealth.repositories.ImagesRepository
import com.clouddroid.pettypetscarehealth.viewmodels.AnimalViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*

/**
 * Created by arkadiusz on 16.02.18.
 */
class GalleryFragment : Fragment(), ImagesRepository.ImagesListListener {

    private var animalViewModel: AnimalViewModel? = null
    private val imagesRepository = ImagesRepository()
    private val imagesAdapter = ImagesRV()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onStart() {
        super.onStart()
        connectWithViewModel()
        observeAnimalData()
        setImagesListener()
        displayRecyclerView()
    }

    private fun connectWithViewModel() {
        animalViewModel = activity?.let { ViewModelProviders.of(it).get(AnimalViewModel::class.java) }
    }


    private fun observeAnimalData() {
        animalViewModel?.getSelectedAnimal()?.observe(activity!!, Observer {
            it?.let {
                getImagesForAnimal(it)
                setCurrentAnimalInRV(it)
            }
        })
    }

    private fun getImagesForAnimal(animal: Animal) {
        imagesRepository.getImagesForAnimal(animal.key)
    }

    private fun setCurrentAnimalInRV(animal: Animal) {
        imagesAdapter.updateCurrentAnimal(animal)
    }

    private fun setImagesListener() {
        imagesRepository.setImagesListListener(this)
    }

    private fun displayRecyclerView() {
        galleryRV.setHasFixedSize(true)
        galleryRV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        galleryRV.adapter = imagesAdapter
    }

    override fun onImagesLoaded(list: List<GalleryItem>) {
        if (list.isEmpty()) {
            displayNoDataText()
        } else {
            hideNoDataText()
            imagesAdapter.initImagesList(list)
            imagesAdapter.notifyDataSetChanged()
        }
    }

    private fun displayNoDataText() {
        galleryRV?.visibility = View.GONE
        noImagesTextView?.visibility = View.VISIBLE
    }

    private fun hideNoDataText() {
        noImagesTextView?.visibility = View.GONE
        galleryRV?.visibility = View.VISIBLE
    }
}