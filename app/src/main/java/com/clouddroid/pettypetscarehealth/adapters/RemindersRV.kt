package com.clouddroid.pettypetscarehealth.adapters

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.dialogs.EditReminderDialog
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.Reminder
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatDate
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatTime
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

        fun bindElement(reminder: Reminder?) {
            d("reminderRV", reminder.toString())
            val date = "${formatDate(reminder!!.year, reminder.month, reminder.day)} \n${formatTime(reminder.hour, reminder.minute)}"
            view.dateTextView.text = date
            val repetitionText = "Every ${reminder.numberIntervals} ${reminder.typeInterval}"
            view.repetitionTextView.text = repetitionText
            view.textTextView.text = reminder.text

            val context = view.context
            view.mainCardView.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                val inflater = context.layoutInflater
                val view = inflater.inflate(R.layout.dialog_reminder_detail, null)
                builder.setView(view)
                view.dateTV.text = date
                view.repetitionTV.text = repetitionText
                view.textTV.text = reminder.text
                val dialog = builder.create()
                dialog.show()

                view.editImage.setOnClickListener {
                    dialog.dismiss()
                    val editDialog = EditReminderDialog(context, R.style.NoteDialog)
                    editDialog.setCanceledOnTouchOutside(false)
                    editDialog.setCurrentAnimal(currentAnimal!!)
                    editDialog.setCurrentReminder(reminder)
                    editDialog.show()
                }
            }
        }
    }
}