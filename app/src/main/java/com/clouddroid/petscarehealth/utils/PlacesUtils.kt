package com.clouddroid.petscarehealth.utils

import android.location.Location

/**
 * Created by arkadiusz on 21.02.18
 */

object PlacesUtils {

    var urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
    private const val vetType = "veterinary_care"
    private const val shopType = "pet_store"
    private const val apiKey = "AIzaSyD39Kanxz9C7hqyVYo8iAgrRn-6Y0TRvno"

    fun generateVetURLString(location: Location): String {
        return urlString + "${location.latitude},${location.longitude}&radius=6000&type=$vetType&key=$apiKey"
    }

    fun generateShopURLString(location: Location): String {
        return urlString + "${location.latitude},${location.longitude}&radius=6000&type=$shopType&key=$apiKey"
    }
}