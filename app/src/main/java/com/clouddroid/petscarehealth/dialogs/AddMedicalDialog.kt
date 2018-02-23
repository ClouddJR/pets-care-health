package com.clouddroid.petscarehealth.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.repositories.MedicalsRepository
import com.clouddroid.petscarehealth.utils.DateUtils.formatDate
import kotlinx.android.synthetic.main.dialog_add_medical.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by arkadiusz on 16.02.18.
 */
class AddMedicalDialog(private val passedContext: Context, themeResId: Int) : Dialog(passedContext, themeResId) {

    private val medicalsRepository = MedicalsRepository()
    private var animalKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_medical)
        setOnDateEditTextClick()
        setOnAddMedicalButtonClick()
    }

    override fun dismiss() {
        if (isSomethingInForm()) {
            passedContext.alert(R.string.dialog_back_pressed_question) {
                positiveButton(R.string.dialog_ok_button) {
                    super.dismiss()
                }
                negativeButton(R.string.dialog_cancel_button) {
                    it.dismiss()
                }
            }.show()
        } else {
            super.dismiss()
        }
    }

    private fun isSomethingInForm(): Boolean {
        return titleEditText.text.toString().isNotEmpty() ||
                dateEditText.text.toString().isNotEmpty() ||
                hospitalEditText.text.toString().isNotEmpty() ||
                commentEditText.text.toString().isNotEmpty()

    }

    fun setCurrentAnimalKey(key: String) {
        animalKey = key
        checkIfAnimalIsSet()
    }

    private fun checkIfAnimalIsSet() {
        if (animalKey.isEmpty()) {
            passedContext.toast("Add animal first")
            super.dismiss()
        }
    }

    private fun setOnAddMedicalButtonClick() {
        addMedicalButton.setOnClickListener {
            if (isFormValid()) {
                medicalsRepository.addNewMedical(animalKey, dateEditText.text.toString(),
                        titleEditText.text.toString(),
                        hospitalEditText.text.toString(),
                        commentEditText.text.toString())
                super.dismiss()
            } else {
                passedContext.toast(R.string.add_medical_dialog_toast_form_invalid)
            }
        }
    }

    private fun isFormValid(): Boolean {
        return titleEditText.text.toString().isNotEmpty()
    }

    private fun setOnDateEditTextClick() {
        dateEditText.setOnClickListener(showDatePicker)
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