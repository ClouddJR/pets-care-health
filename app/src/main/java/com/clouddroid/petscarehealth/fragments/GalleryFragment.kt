package com.clouddroid.petscarehealth.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.adapters.ImagesRV
import com.clouddroid.petscarehealth.model.Animal
import com.clouddroid.petscarehealth.model.GalleryItem
import com.clouddroid.petscarehealth.repositories.ImagesRepository
import com.clouddroid.petscarehealth.viewmodels.AnimalViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.layout_content_main.*

/**
 * Created by arkadiusz on 16.02.18
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
        showFAB()
        setImagesListener()
        displayRecyclerView()
        hideFABOnScroll()
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

    private fun showFAB() {
        activity?.fabMenu?.showMenuButton(true)
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

    private fun hideFABOnScroll() {
        galleryRV?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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