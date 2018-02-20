package com.clouddroid.pettypetscarehealth.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.Reminder
import com.clouddroid.pettypetscarehealth.repositories.RemindersRepository
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatDate
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatTime
import com.clouddroid.pettypetscarehealth.utils.RemindersUtils
import kotlinx.android.synthetic.main.dialog_edit_reminder.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by arkadiusz on 19.02.18
 */

class EditReminderDialog(private val passedContext: Context, themeResId: Int) : Dialog(passedContext, themeResId) {

    private val remindersRepository = RemindersRepository()
    private var chosenDay = 0
    private var chosenMonth = 0
    private var chosenYear = 0
    private var chosenHour = 0
    private var chosenMinute = 0
    private var date = ""

    private var passedAnimal: Animal? = null
    private var passedReminder: Reminder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_reminder)
        setOnDateEditTextClick()
        setUpNumbersSpinner()
        setUpIntervalsSpinner()
        fillFormWithPassedData()
        setUpCheckBox()
        setOnEditReminderButtonClick()

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
        passedAnimal = animal
    }

    fun setCurrentReminder(reminder: Reminder) {
        passedReminder = reminder
    }

    private fun fillFormWithPassedData() {
        titleEditText.setText(passedReminder?.text)
        val date = "${formatDate(passedReminder!!.year, passedReminder!!.month, passedReminder!!.day)} ${formatTime(passedReminder!!.hour, passedReminder!!.minute)}"
        dateEditText.setText(date)
        d("passedInterval", "${passedReminder!!.numberIntervals} : ${passedReminder!!.typeInterval}")
        when (passedReminder?.typeInterval) {
            "" -> {
                onceCheckBox.isChecked = true
                hideSpinners()
            }
            "days" -> {
                intervalsSpinner.setSelection(0)
                daysSpinner.setSelection((passedReminder?.numberIntervals ?: 1) - 1)
            }
            "weeks" -> {
                intervalsSpinner.setSelection(1)
                daysSpinner.setSelection((passedReminder?.numberIntervals ?: 1) - 1)
            }
            "months" -> {
                intervalsSpinner.setSelection(2)
                daysSpinner.setSelection((passedReminder?.numberIntervals ?: 1) - 1)
            }
        }
        chosenYear = passedReminder?.year ?: 0
        chosenMonth = passedReminder?.month ?: 0
        chosenDay = passedReminder?.day ?: 0
        chosenHour = passedReminder?.hour ?: 0
        chosenMinute = passedReminder?.minute ?: 0
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
            val time = formatTime(chosenHour, chosenMinute)
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
                hideSpinners()
            } else {
                showSpinners()
            }
        }
    }

    private fun hideSpinners() {
        repetitionTextView.visibility = View.GONE
        daysSpinner.visibility = View.GONE
        intervalsSpinner.visibility = View.GONE
    }

    private fun showSpinners() {
        repetitionTextView.visibility = View.VISIBLE
        daysSpinner.visibility = View.VISIBLE
        intervalsSpinner.visibility = View.VISIBLE
    }

    private fun setOnEditReminderButtonClick() {
        editReminderButton.setOnClickListener {
            if (isFormValid()) {
                RemindersUtils.deleteReminder(passedContext, passedReminder!!)
                val reminderKey = passedReminder?.key
                val reminder = if (onceCheckBox.isChecked) {
                    generateReminderForSingleEvent(reminderKey!!)
                } else {
                    generateReminderForRepetitiveEvents(reminderKey!!)
                }
                RemindersUtils.addNewReminder(passedContext, reminder)
                remindersRepository.editReminder(passedAnimal!!.key, reminder)
                super.dismiss()
            } else {
                passedContext.toast(R.string.edit_reminder_dialog_toast_form_invalid)
            }
        }

        deleteReminderButton.setOnClickListener {
            passedContext.alert(R.string.edit_reminder_dialog_delete_question) {
                positiveButton(R.string.edit_reminder_dialog_delete_YES) {
                    remindersRepository.deleteReminder(passedAnimal?.key ?: "", passedReminder?.key
                            ?: "")
                    super.dismiss()
                }
                negativeButton(R.string.edit_reminder_dialog_delete_NO) {
                    it.dismiss()
                }
            }.show()
        }
    }

    private fun isFormValid(): Boolean {
        return titleEditText.text.toString().isNotEmpty() && dateEditText.text.toString().isNotEmpty()
    }

    private fun generateReminderForSingleEvent(reminderKey: String): Reminder {
        return Reminder(reminderKey, titleEditText.text.toString(), chosenYear, chosenMonth, chosenDay, chosenHour, chosenMinute, 0, "")
    }

    private fun generateReminderForRepetitiveEvents(reminderKey: String): Reminder {
        return Reminder(reminderKey, titleEditText.text.toString(), chosenYear, chosenMonth, chosenDay, chosenHour, chosenMinute,
                (daysSpinner.getChildAt(0) as TextView).text.toString().toInt(), (intervalsSpinner.getChildAt(0) as TextView).text.toString())
    }


}