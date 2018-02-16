package com.clouddroid.pettypetscarehealth.adapters

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.dialogs.EditNoteDialog
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.Note
import kotlinx.android.synthetic.main.dialog_note_detail.view.*
import kotlinx.android.synthetic.main.note_item.view.*
import org.jetbrains.anko.layoutInflater


/**
 * Created by arkadiusz on 15.02.18.
 */
class NotesRV : RecyclerView.Adapter<NotesRV.ViewHolder>() {

    private var notesList: List<Note>? = null
    private var currentAnimal: Animal? = null

    fun initNotesList(list: List<Note>) {
        notesList = list
    }

    fun updateCurrentAnimal(animal: Animal) {
        currentAnimal = animal
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.note_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindElement(notesList?.get(position))
    }

    override fun getItemCount(): Int {
        return notesList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView

        fun bindElement(note: Note?) {
            view.mainCardView.setCardBackgroundColor(note?.color ?: 0)
            view.titleTextView.text = note?.title
            view.dateTextView.text = note?.date
            view.contentTextView.text = note?.content

            val context = view.context
            view.mainCardView.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                val inflater = context.layoutInflater
                val view = inflater.inflate(R.layout.dialog_note_detail, null)
                builder.setView(view)
                view.titleTV.text = note?.title
                view.dateTV.text = note?.date
                view.contentTV.text = note?.content
                view.setBackgroundColor(note?.color ?: 0)
                val dialog = builder.create()
                dialog.show()

                view.imageEdit.setOnClickListener {
                    dialog.dismiss()
                    val editDialog = EditNoteDialog(context, R.style.NoteDialog)
                    editDialog.setCanceledOnTouchOutside(false)
                    editDialog.setCurrentAnimal(currentAnimal!!)
                    editDialog.setCurrentNote(note!!)
                    editDialog.show()
                }

            }
        }
    }
}