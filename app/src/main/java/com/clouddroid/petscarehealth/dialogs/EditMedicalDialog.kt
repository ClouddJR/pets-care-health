package com.clouddroid.petscarehealth.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.model.Animal
import com.clouddroid.petscarehealth.model.MedicalRecord
import com.clouddroid.petscarehealth.repositories.MedicalsRepository
import com.clouddroid.petscarehealth.utils.DateUtils.formatDate
import kotlinx.android.synthetic.main.dialog_edit_medical.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by arkadiusz on 16.02.18
 */

class EditMedicalDialog(private val passedContext: Context, themeResId: Int) : Dialog(passedContext, themeResId) {

    private val medicalsRepository = MedicalsRepository()
    private var passedAnimal: Animal? = null
    private var passedMedical: MedicalRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_medical)
        setOnEditMedicalButtonClick()
        fillFormWithPassedData()
        setOnDateEditTextClick()
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

    fun setCurrentAnimal(animal: Animal) {
        passedAnimal = animal
    }

    fun setCurrentNote(medicalRecord: MedicalRecord) {
        passedMedical = medicalRecord
    }

    private fun isSomethingInForm(): Boolean {
        return titleEditText.text.toString().isNotEmpty() ||
                dateEditText.text.toString().isNotEmpty() ||
                hospitalEditText.text.toString().isNotEmpty() ||
                commentEditText.text.toString().isNotEmpty()

    }

    private fun fillFormWithPassedData() {
        titleEditText.setText(passedMedical?.title)
        dateEditText.setText(passedMedical?.date)
        hospitalEditText.setText(passedMedical?.hospital)
        commentEditText.setText(passedMedical?.comment)
    }

    private fun setOnEditMedicalButtonClick() {
        editMedicalButton.setOnClickListener {
            if (isFormValid()) {
                medicalsRepository.editMedical(passedAnimal?.key ?: "", passedMedical?.key ?: "",
                        dateEditText.text.toString(),
                        titleEditText.text.toString(),
                        hospitalEditText.text.toString(),
                        commentEditText.text.toString())
                super.dismiss()
            } else {
                passedContext.toast(R.string.edit_medical_dialog_toast_form_invalid)
            }
        }

        deleteMedicalButton.setOnClickListener {
            passedContext.alert(R.string.edit_medical_dialog_delete_question) {
                positiveButton(R.string.edit_medical_dialog_delete_YES) {
                    medicalsRepository.deleteMedical(passedAnimal?.key ?: "", passedMedical?.key
                            ?: "")
                    super.dismiss()
                }
                negativeButton(R.string.edit_medical_dialog_delete_NO) {
                    it.dismiss()
                }
            }.show()
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