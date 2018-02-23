package com.clouddroid.petscarehealth.adapters

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.model.Place
import kotlinx.android.synthetic.main.place_item.view.*


/**
 * Created by arkadiusz on 21.02.18
 */

class PlacesRV : RecyclerView.Adapter<PlacesRV.ViewHolder>() {

    private var placesList: List<Place>? = null

    fun initValuesList(list: List<Place>) {
        placesList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindElement(placesList?.get(position))
    }

    override fun getItemCount(): Int {
        return placesList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView

        fun bindElement(place: Place?) {
            view.titleTextView.text = place!!.name
            view.addressTextView.text = place.vicinity
            if (place.rating == 0.0) {
                view.ratingLinear.visibility = View.GONE
            } else {
                view.ratingLinear.visibility = View.VISIBLE
                view.ratingTextView.text = place.rating.toString()
            }

            view.mainCardView.setOnClickListener {
                displayGoogleMaps(place)
            }
        }

        private fun displayGoogleMaps(place: Place) {
            val gmmIntentUri = Uri.parse("geo:${place.lat},${place.lng}?q=${place.lat},${place.lng}(${place.name})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            view.context.startActivity(mapIntent)
        }
    }
}