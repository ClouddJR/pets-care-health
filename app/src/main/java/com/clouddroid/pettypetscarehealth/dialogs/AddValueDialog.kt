package com.clouddroid.pettypetscarehealth.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.repositories.AnimalsRepository
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatDate
import kotlinx.android.synthetic.main.dialog_add_measurement_value.*
import org.jetbrains.anko.toast
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
        setContentView(R.layout.dialog_add_measurement_value)
        initAddButtonClickListener()
        initPositiveAndNegativeButton()
    }

    private fun initAddButtonClickListener() {
        dateEditText.setOnClickListener(showDatePicker)
    }

    private fun initPositiveAndNegativeButton() {
        positiveButton.setOnClickListener {
            if (isFormValid()) {
                when (valueType) {
                    "height" -> addNewHeightValue()
                    "weight" -> addNewWeightValue()
                }
                dismiss()
            } else {
                context.toast(R.string.dialog_values_toast_form_invalid)
            }
        }

        negativeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun isFormValid(): Boolean {
        return dateEditText.text.toString().isNotEmpty() && valueEditText.text.toString().isNotEmpty()
    }

    private fun addNewHeightValue() {
        val xValue = dateEditText.text.toString()
        val yValue = valueEditText.text.toString().toFloat()
        animalsRepository.addNewHeightValue(xValue, yValue,
                currentAnimal!!.key)
    }

    private fun addNewWeightValue() {
        val xValue = dateEditText.text.toString()
        val yValue = valueEditText.text.toString().toFloat()
        animalsRepository.addNewWeightValue(xValue, yValue,
                currentAnimal!!.key)
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
            val date = formatDate(chosenYear, chosenMonth, chosenDay)
            dateEditText.setText(date)
        }, year, month, day).show()
    }
}