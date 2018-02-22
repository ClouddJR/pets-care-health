package com.clouddroid.pettypetscarehealth.adapters

import PreferenceUtils.defaultPrefs
import PreferenceUtils.get
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.fragments.SettingsFragment
import com.clouddroid.pettypetscarehealth.model.MeasurementValue
import kotlinx.android.synthetic.main.measurement_value_item.view.*

/**
 * Created by arkadiusz on 08.02.18
 */

class MeasurementValuesRV : RecyclerView.Adapter<MeasurementValuesRV.ViewHolder>() {

    private var measurementValuesList: List<MeasurementValue>? = null
    private var valueType = ""

    fun initValuesList(list: List<MeasurementValue>) {
        measurementValuesList = list
    }

    fun initType(type: String) {
        valueType = type
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.measurement_value_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindElement(measurementValuesList?.get(position))
    }

    override fun getItemCount(): Int {
        return measurementValuesList?.size ?: 0
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView

        fun bindElement(measurementValue: MeasurementValue?) {
            view.dateTextView.text = measurementValue?.x
            view.unitTextView.text = when (valueType) {
                "height" -> defaultPrefs(view.context)[SettingsFragment.Keys.heightPreferencesKey, ""]
                "weight" -> defaultPrefs(view.context)[SettingsFragment.Keys.weightPreferencesKey, ""]
                else -> ""
            }
            view.valueTextView.text = measurementValue?.y.toString()
        }
    }

}