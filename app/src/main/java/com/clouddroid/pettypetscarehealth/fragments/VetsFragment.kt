package com.clouddroid.pettypetscarehealth.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.adapters.PlacesRV
import com.clouddroid.pettypetscarehealth.model.Place
import com.clouddroid.pettypetscarehealth.repositories.PlacesRepository
import com.clouddroid.pettypetscarehealth.utils.PlacesUtils
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.fragment_vets.*
import kotlinx.android.synthetic.main.layout_content_main.*

/**
 * Created by arkadiusz on 21.02.18
 */

class VetsFragment : Fragment(), PlacesRepository.PlacesListListener {

    private val placesRepository = PlacesRepository()
    private val vetsAdapter = PlacesRV()
    private val locationRequest = 1234
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vets, container, false)
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
        vetsRV.setHasFixedSize(true)
        vetsRV.layoutManager = LinearLayoutManager(context)
        vetsRV.adapter = vetsAdapter
    }

    fun setLocationPermissionGranted() {
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                val urlString = PlacesUtils.generateVetURLString(it)
                Log.d("urlStringVet", urlString)
                placesRepository.getPlacesForLocation(urlString)
            }
        }
    }

    override fun onPlacesLoaded(list: List<Place>) {
        hideNoDataText()
        vetsAdapter.initValuesList(list)
        vetsAdapter.notifyDataSetChanged()
    }

    private fun displayNoDataText() {
        vetsRV?.visibility = View.GONE
        noVetsTextView?.visibility = View.VISIBLE
    }

    private fun hideNoDataText() {
        noVetsTextView?.visibility = View.GONE
        vetsRV?.visibility = View.VISIBLE
    }

    private fun hideFABOnScroll() {
        vetsRV?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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