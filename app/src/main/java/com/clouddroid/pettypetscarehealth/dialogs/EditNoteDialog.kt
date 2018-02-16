package com.clouddroid.pettypetscarehealth.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.Note
import com.clouddroid.pettypetscarehealth.repositories.NotesRepository
import kotlinx.android.synthetic.main.dialog_edit_note.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import petrov.kristiyan.colorpicker.ColorPicker
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by arkadiusz on 15.02.18.
 */
class EditNoteDialog(private val passedContext: Context, themeResId: Int) : Dialog(passedContext, themeResId) {

    private val notesRepository = NotesRepository()
    private val arrayOfColors = arrayListOf("#80d8ff", "#ff8a80", "#ffd180", "#ffffff", "#ffff8d", "#ccff90", "#a7ffeb", "#82b1ff", "#b388ff", "#f8bbd0", "#d7ccc8", "#cfd8dc")
    private var chosenColor = -115
    private var passedAnimal: Animal? = null
    private var passedNote: Note? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_note)
        setOnColorImageClick()
        setOnAddNoteButtonClick()
        fillFormWithPassedData()
    }

    override fun dismiss() {
        passedContext.alert(R.string.edit_activity_dialog_back_pressed_question) {
            positiveButton(R.string.edit_activity_dialog_ok) {
                super.dismiss()
            }
            negativeButton(R.string.edit_activity_dialog_cancel) {
                it.dismiss()
            }
        }.show()
    }

    fun setCurrentAnimal(animal: Animal) {
        passedAnimal = animal
    }

    fun setCurrentNote(note: Note) {
        passedNote = note
    }

    private fun fillFormWithPassedData() {
        titleEditText.setText(passedNote?.title)
        contentEditText.setText(passedNote?.content)
        colorImageView.setBackgroundColor(passedNote?.color ?: 0)
        chosenColor = passedNote?.color ?: 0

    }

    private fun setOnColorImageClick() {
        colorImageView.setOnClickListener {
            val colorPicker = ColorPicker(passedContext as Activity)
            colorPicker.setColors(arrayOfColors)
            colorPicker.setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    colorImageView.setBackgroundColor(color)
                    chosenColor = color
                }

                override fun onCancel() {
                    //nothing here
                }
            })
            colorPicker.show()
        }
    }

    private fun setOnAddNoteButtonClick() {
        addNoteButton.setOnClickListener {
            if (isFormValid()) {
                notesRepository.editNote(passedAnimal?.key ?: "", passedNote?.key
                        ?: "", currentDate(), titleEditText.text.toString(), chosenColor, contentEditText.text.toString())
                super.dismiss()
            } else {
                passedContext.toast(R.string.edit_note_dialog_toast_form_invalid)
            }
        }

        deleteNoteButton.setOnClickListener {
            passedContext.alert(R.string.edit_note_dialog_delete_question) {
                positiveButton(R.string.edit_note_dialog_delete_YES) {
                    notesRepository.deleteNote(passedAnimal?.key ?: "", passedNote?.key ?: "")
                    super.dismiss()
                }
                negativeButton(R.string.edit_note_dialog_delete_NO) {
                    it.dismiss()
                }
            }.show()
        }
    }

    private fun isFormValid(): Boolean {
        return titleEditText.text.toString().isNotEmpty() && contentEditText.text.toString().isNotEmpty()
    }

    private fun currentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return formatter.format(Date())
    }
}