package com.clouddroid.pettypetscarehealth.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.model.MeasurementValue
import kotlinx.android.synthetic.main.dialog_value_element.view.*

/**
 * Created by arkadiusz on 12.02.18
 */

class MeasurementDialogValuesRV : RecyclerView.Adapter<MeasurementDialogValuesRV.ViewHolderDialog>() {

    private var measurementValuesList: List<MeasurementValue>? = null
    private var trashListener: OnTrashIconClick? = null

    interface OnTrashIconClick {
        fun onTrashClicked(value: MeasurementValue)
    }

    fun initValuesList(list: List<MeasurementValue>) {
        measurementValuesList = list
    }

    fun initTrashListener(trashListener: OnTrashIconClick) {
        this.trashListener = trashListener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolderDialog {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.dialog_value_element, parent, false)
        return ViewHolderDialog(view)
    }

    override fun onBindViewHolder(holder: ViewHolderDialog?, position: Int) {
        holder?.bindElement(measurementValuesList?.get(position), trashListener, measurementValuesList?.get(position))
    }

    override fun getItemCount(): Int {
        return measurementValuesList?.size ?: 0
    }


    inner class ViewHolderDialog(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView

        fun bindElement(measurementValue: MeasurementValue?, listener: MeasurementDialogValuesRV.OnTrashIconClick?, value: MeasurementValue?) {
            val dateText = measurementValue?.x + ":"
            view.dateTextView.text = dateText
            val valueText = measurementValue?.y.toString() + " kg"
            view.valueTextView.text = valueText
            view.trashIcon.setOnClickListener {
                listener?.onTrashClicked(value!!)
            }
        }
    }
}