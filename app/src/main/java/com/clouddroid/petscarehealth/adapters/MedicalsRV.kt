package com.clouddroid.petscarehealth.adapters

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.dialogs.EditMedicalDialog
import com.clouddroid.petscarehealth.model.Animal
import com.clouddroid.petscarehealth.model.MedicalRecord
import kotlinx.android.synthetic.main.dialog_medical_detail.view.*
import kotlinx.android.synthetic.main.medical_item.view.*
import org.jetbrains.anko.layoutInflater

/**
 * Created by arkadiusz on 16.02.18
 */

class MedicalsRV : RecyclerView.Adapter<MedicalsRV.ViewHolder>() {

    private var medicalsList: List<MedicalRecord>? = null
    private var currentAnimal: Animal? = null

    fun initMedicalsList(list: List<MedicalRecord>) {
        medicalsList = list
    }

    fun updateCurrentAnimal(animal: Animal) {
        currentAnimal = animal
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.medical_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindElement(medicalsList?.get(position))
    }

    override fun getItemCount(): Int {
        return medicalsList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView

        fun bindElement(medicalRecord: MedicalRecord?) {
            view.titleTextView.text = medicalRecord?.title
            view.dateTextView.text = medicalRecord?.date
            view.hospitalTextView.text = medicalRecord?.hospital
            if (medicalRecord?.comment?.isNotEmpty() == true) {
                view.commentTextView.visibility = View.VISIBLE
                view.commentTextView.text = medicalRecord.comment
            } else {
                view.commentTextView.visibility = View.GONE
            }

            val context = view.context
            view.mainCardView.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                val inflater = context.layoutInflater
                val view = inflater.inflate(R.layout.dialog_medical_detail, null)
                builder.setView(view)
                view.titleTV.text = medicalRecord?.title
                view.dateTV.text = medicalRecord?.date
                view.hospitalTV.text = medicalRecord?.hospital
                view.commentTV.text = medicalRecord?.comment
                val dialog = builder.create()
                dialog.show()

                view.editImage.setOnClickListener {
                    dialog.dismiss()
                    val editDialog = EditMedicalDialog(context, R.style.NoteDialog)
                    editDialog.setCanceledOnTouchOutside(false)
                    editDialog.setCurrentAnimal(currentAnimal!!)
                    editDialog.setCurrentNote(medicalRecord!!)
                    editDialog.show()
                }
            }
        }
    }
}