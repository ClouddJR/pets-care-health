package com.clouddroid.pettypetscarehealth.adapters

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.dialogs.EditReminderDialog
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.Reminder
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatDate
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatTime
import com.clouddroid.pettypetscarehealth.utils.RemindersUtils.addNewReminder
import kotlinx.android.synthetic.main.dialog_reminder_detail.view.*
import kotlinx.android.synthetic.main.reminder_item.view.*
import org.jetbrains.anko.layoutInflater

/**
 * Created by arkadiusz on 19.02.18
 */

class RemindersRV : RecyclerView.Adapter<RemindersRV.ViewHolder>() {

    private var remindersList: List<Reminder>? = null
    private var currentAnimal: Animal? = null

    fun initRemindersList(list: List<Reminder>) {
        remindersList = list
    }

    fun updateCurrentAnimal(animal: Animal) {
        currentAnimal = animal
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.reminder_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindElement(remindersList?.get(position))
    }

    override fun getItemCount(): Int {
        return remindersList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val view: View = itemView
        private val context = view.context

        fun bindElement(reminder: Reminder?) {
            val date = generateDate(reminder)
            val repetitionText = generateRepetitionText(reminder)
            fillViewsWithPassedData(date, repetitionText, reminder!!.text)
            updateAlarmForReminder(reminder)

            view.mainCardView.setOnClickListener {
                val builder = generateDialogBuilder()
                val detailDialogView = prepareViewForBuilder()
                fillDialogViewWithData(detailDialogView, date, repetitionText, reminder.text)
                builder.setView(detailDialogView)
                val detailDialog = buildAndShowDetailDialog(builder)

                detailDialogView.editImage.setOnClickListener {
                    detailDialog.dismiss()
                    buildAndDisplayEditReminderDialog(reminder)
                }
            }
        }

        private fun generateDate(reminder: Reminder?): String {
            return "${formatDate(reminder!!.year, reminder.month, reminder.day)} \n${formatTime(reminder.hour, reminder.minute)}"
        }

        private fun generateRepetitionText(reminder: Reminder?): String {
            val firstWord = context.getString(R.string.reminder_item_repetition_text_first_word)
            val numberIntervals = reminder!!.numberIntervals
            val typeInterval = reminder.typeInterval
            var intervalWord = ""

            if (numberIntervals == 0) {
                return context.getString(R.string.reminder_item_repetition_text_single_event)
            }

            if (numberIntervals == 1) {
                when (typeInterval) {
                    "days" -> intervalWord = context.getString(R.string.reminder_item_repetition_text_single_day)
                    "weeks" -> intervalWord = context.getString(R.string.reminder_item_repetition_text_single_week)
                    "months" -> intervalWord = context.getString(R.string.reminder_item_repetition_text_single_month)
                }
            } else {
                when (typeInterval) {
                    "days" -> intervalWord = context.getString(R.string.reminder_item_repetition_text_multiple_days)
                    "weeks" -> intervalWord = context.getString(R.string.reminder_item_repetition_text_multiple_weeks)
                    "months" -> intervalWord = context.getString(R.string.reminder_item_repetition_text_multiple_months)
                }
            }

            return "$firstWord $numberIntervals $intervalWord"
        }

        private fun fillViewsWithPassedData(date: String, repetitionText: String, reminderTitle: String) {
            view.dateTextView.text = date
            view.repetitionTextView.text = repetitionText
            view.textTextView.text = reminderTitle
        }

        private fun updateAlarmForReminder(reminder: Reminder) {
            addNewReminder(context, reminder)
        }

        private fun generateDialogBuilder(): AlertDialog.Builder {
            return AlertDialog.Builder(context)
        }

        private fun prepareViewForBuilder(): View {
            return context.layoutInflater.inflate(R.layout.dialog_reminder_detail, null)
        }

        private fun fillDialogViewWithData(dialogView: View, date: String, repetitionText: String, reminderTitle: String) {
            dialogView.dateTV.text = date
            dialogView.repetitionTV.text = repetitionText
            dialogView.textTV.text = reminderTitle
        }

        private fun buildAndShowDetailDialog(builder: AlertDialog.Builder): AlertDialog {
            val dialog = builder.create()
            dialog.show()
            return dialog
        }

        private fun buildAndDisplayEditReminderDialog(reminder: Reminder?) {
            val editDialog = EditReminderDialog(context, R.style.NoteDialog)
            editDialog.setCanceledOnTouchOutside(false)
            editDialog.setCurrentAnimal(currentAnimal!!)
            editDialog.setCurrentReminder(reminder!!)
            editDialog.show()
        }
    }
}