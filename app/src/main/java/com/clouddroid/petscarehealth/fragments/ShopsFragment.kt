package com.clouddroid.petscarehealth.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.adapters.PlacesRV
import com.clouddroid.petscarehealth.model.Place
import com.clouddroid.petscarehealth.repositories.PlacesRepository
import com.clouddroid.petscarehealth.utils.PlacesUtils
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.fragment_shops.*
import kotlinx.android.synthetic.main.layout_content_main.*

/**
 * Created by arkadiusz on 21.02.18
 */

class ShopsFragment : Fragment(), PlacesRepository.PlacesListListener {
    private val placesRepository = PlacesRepository()
    private val shopsAdapter = PlacesRV()
    private val locationRequest = 1234
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shops, container, false)
    }

    override fun onStart() {
        super.onStart()
        initLocationClient()
        askForPermission()
        showFAB()
        setPlacesListener()
        displayNoDataText()
        showRecyclerView()
        hideFABOnScroll()
    }

    private fun initLocationClient() {
        activity?.let {
            fusedLocationClient = FusedLocationProviderClient(activity!!)
        }
    }

    private fun askForPermission() {
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), locationRequest)
        } else {
            getLocation()
        }
    }

    private fun showFAB() {
        activity?.fabMenu?.showMenuButton(true)
    }

    private fun setPlacesListener() {
        placesRepository.setPlacesListener(this)
    }

    private fun showRecyclerView() {
        shopsRV.setHasFixedSize(true)
        shopsRV.layoutManager = LinearLayoutManager(context)
        shopsRV.adapter = shopsAdapter
    }

    fun setLocationPermissionGranted() {
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                val urlString = PlacesUtils.generateShopURLString(it)
                d("urlStringShop",urlString)
                placesRepository.getPlacesForLocation(urlString)
            }
        }
    }

    override fun onPlacesLoaded(list: List<Place>) {
        hideNoDataText()
        shopsAdapter.initValuesList(list)
        shopsAdapter.notifyDataSetChanged()
    }

    private fun displayNoDataText() {
        shopsRV?.visibility = View.GONE
        noShopsTextView?.visibility = View.VISIBLE
    }

    private fun hideNoDataText() {
        noShopsTextView?.visibility = View.GONE
        shopsRV?.visibility = View.VISIBLE
    }

    private fun hideFABOnScroll() {
        shopsRV?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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