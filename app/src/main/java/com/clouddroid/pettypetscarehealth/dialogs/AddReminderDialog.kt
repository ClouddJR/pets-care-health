package com.clouddroid.pettypetscarehealth.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.Reminder
import com.clouddroid.pettypetscarehealth.repositories.RemindersRepository
import com.clouddroid.pettypetscarehealth.utils.DateUtils
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatDate
import com.clouddroid.pettypetscarehealth.utils.RemindersUtils
import kotlinx.android.synthetic.main.dialog_add_reminder.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by arkadiusz on 19.02.18
 */

class AddReminderDialog(private val passedContext: Context, themeResId: Int) : Dialog(passedContext, themeResId) {

    private val remindersRepository = RemindersRepository()
    private var passedAnimal: Animal? = null
    private var animalKey = ""
    private var chosenDay = 0
    private var chosenMonth = 0
    private var chosenYear = 0
    private var chosenHour = 0
    private var chosenMinute = 0
    private var date = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_reminder)
        setOnDateEditTextClick()
        setUpNumbersSpinner()
        setUpIntervalsSpinner()
        setUpCheckBox()
        setOnAddReminderButtonClick()
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
        return titleEditText.text.isNotEmpty() || dateEditText.text.isNotEmpty()
    }

    fun setCurrentAnimal(animal: Animal) {
        animalKey = animal.key
        passedAnimal = animal
        checkIfAnimalIsSet()
    }

    private fun checkIfAnimalIsSet() {
        if (animalKey.isEmpty()) {
            passedContext.toast("Add animal first")
            super.dismiss()
        }
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
            this.chosenDay = chosenDay
            this.chosenMonth = chosenMonth
            this.chosenYear = chosenYear
            date = formatDate(chosenYear, chosenMonth, chosenDay)
            displayTimePickerDialog()
        }, year, month, day).show()
    }


    private fun displayTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(passedContext, TimePickerDialog.OnTimeSetListener { _, chosenHour, chosenMinute ->
            this.chosenHour = chosenHour
            this.chosenMinute = chosenMinute
            val time = DateUtils.formatTime(chosenHour, chosenMinute)
            date += " $time"
            dateEditText.setText(date)
        }, hour, minute, true).show()
    }

    private fun setUpNumbersSpinner() {
        val numbersAdapter = ArrayAdapter.createFromResource(passedContext, R.array.numbers, R.layout.spinner_animal_item)
        daysSpinner.adapter = numbersAdapter
    }

    private fun setUpIntervalsSpinner() {
        val intervalsAdapter = ArrayAdapter.createFromResource(passedContext, R.array.intervals, R.layout.spinner_animal_item)
        intervalsSpinner.adapter = intervalsAdapter
    }

    private fun setUpCheckBox() {
        onceCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                repetitionTextView.visibility = View.GONE
                daysSpinner.visibility = View.GONE
                intervalsSpinner.visibility = View.GONE
            } else {
                repetitionTextView.visibility = View.VISIBLE
                daysSpinner.visibility = View.VISIBLE
                intervalsSpinner.visibility = View.VISIBLE
            }
        }
    }

    private fun setOnAddReminderButtonClick() {
        addReminderButton.setOnClickListener {
            if (isFormValid()) {
                val reminderKey = remindersRepository.generateKey()
                val reminder = if (onceCheckBox.isChecked) {
                    generateReminderForSingleEvent(reminderKey)
                } else {
                    generateReminderForRepetitiveEvents(reminderKey)

                }
                RemindersUtils.addNewReminder(passedContext, reminder)
                remindersRepository.addNewReminder(animalKey, reminder)
                super.dismiss()
            } else {
                passedContext.toast(R.string.add_reminder_dialog_toast_form_invalid)
            }
        }
    }

    private fun isFormValid(): Boolean {
        return titleEditText.text.toString().isNotEmpty() && dateEditText.text.toString().isNotEmpty()
    }

    private fun generateReminderForSingleEvent(reminderKey: String): Reminder {
        return Reminder(reminderKey, titleEditText.text.toString(), chosenYear, chosenMonth, chosenDay, chosenHour, chosenMinute, 0, "",
                passedAnimal?.name ?: "")
    }

    private fun generateReminderForRepetitiveEvents(reminderKey: String): Reminder {
        return Reminder(reminderKey, titleEditText.text.toString(), chosenYear, chosenMonth, chosenDay, chosenHour, chosenMinute,
                (daysSpinner.getChildAt(0) as TextView).text.toString().toInt(), (intervalsSpinner.getChildAt(0) as TextView).text.toString(),
                passedAnimal?.name ?: "")
    }

}