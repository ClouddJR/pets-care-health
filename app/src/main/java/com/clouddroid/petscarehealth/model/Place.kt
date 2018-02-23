package com.clouddroid.petscarehealth.model

/**
 * Created by arkadiusz on 21.02.18
 */

data class Place(val name: String = "",
                 val rating: Double = 0.0,
                 val vicinity: String = "",
                 val lat: Double = 0.0,
                 val lng: Double = 0.0)