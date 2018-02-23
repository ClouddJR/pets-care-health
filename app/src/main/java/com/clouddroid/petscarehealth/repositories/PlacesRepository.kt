package com.clouddroid.petscarehealth.repositories

import com.clouddroid.petscarehealth.model.Place
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class PlacesRepository {

    private var placesListener: PlacesListListener? = null

    interface PlacesListListener {
        fun onPlacesLoaded(list: List<Place>)
    }

    fun setPlacesListener(listener: PlacesListListener) {
        placesListener = listener
    }

    fun getPlacesForLocation(urlString: String) {

        val stringBuffer = StringBuffer()
        doAsync {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()

            val inputStream = urlConnection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            var line: String? = ""

            while (line != null) {
                line = bufferedReader.readLine()
                stringBuffer.append(line)
            }
            uiThread {
                val jsonObject = JSONObject(stringBuffer.toString())
                val jsonArray = jsonObject.getJSONArray("results")
                val listOfPlaces = getPlacesFromJSONArray(jsonArray)
                placesListener?.onPlacesLoaded(listOfPlaces)
            }
        }
    }


    private fun getPlacesFromJSONArray(jsonArray: JSONArray): List<Place> {
        return (0 until jsonArray.length()).map { getSinglePlace(jsonArray.get(it) as JSONObject) }
    }

    private fun getSinglePlace(jsonObject: JSONObject): Place {
        val name = jsonObject.getString("name")
        val rating = if (jsonObject.isNull("rating")) {
            0.0
        } else {
            jsonObject.getDouble("rating")
        }
        val latitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat").toDouble()
        val longitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng").toDouble()
        val vicinity = jsonObject.getString("vicinity")
        return Place(name, rating, vicinity, latitude, longitude)
    }

}