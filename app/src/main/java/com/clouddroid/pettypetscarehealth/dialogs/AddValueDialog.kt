package com.clouddroid.pettypetscarehealth.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.repositories.AnimalsRepository
import kotlinx.android.synthetic.main.dialog_chart_add_value.*
import java.util.*

/**
 * Created by arkadiusz on 25.01.18
 */

class AddValueDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    private val passedContext = context
    private val animalsRepository = AnimalsRepository()
    private var currentAnimal: Animal? = null
    private var valueType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_chart_add_value)
        initAddButtonClickListener()
        initPositiveAndNegativeButton()
    }

    private fun initAddButtonClickListener() {
        dateEditText.setOnClickListener(showDatePicker)
    }

    private fun initPositiveAndNegativeButton() {
        positiveButton.setOnClickListener {
            when (valueType) {
                "height" -> addNewHeightValue()
                "weight" -> addNewWeightValue()
            }
            dismiss()
        }

        negativeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun addNewHeightValue() {
        val xValue = dateEditText.text.toString()
        val yValue = valueEditText.text.toString().toFloat()
        animalsRepository.addNewHeightValue(xValue, yValue,
                currentAnimal!!.name,
                currentAnimal!!.date)
    }

    private fun addNewWeightValue() {
        val xValue = dateEditText.text.toString()
        val yValue = valueEditText.text.toString().toFloat()
        animalsRepository.addNewWeightValue(xValue, yValue,
                currentAnimal!!.name,
                currentAnimal!!.date)
    }

    fun setCurrentAnimal(pet: Animal?) {
        currentAnimal = pet
    }

    fun setChartValuesType(type: String) {
        valueType = type
    }

    private val showDatePicker = View.OnClickListener {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(passedContext, DatePickerDialog.OnDateSetListener { _, chosenYear, chosenMonth, chosenDay ->
            val date = formatDate(chosenYear, chosenMonth + 1, chosenDay)
            dateEditText.setText(date)
        }, year, month, day).show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val formattedMonth = if (month < 10) {
            "0$month"
        } else {
            "$month"
        }

        val formattedDay = if (day < 10) {
            "0$day"
        } else {
            "$day"
        }

        return "$year-$formattedMonth-$formattedDay"
    }
}