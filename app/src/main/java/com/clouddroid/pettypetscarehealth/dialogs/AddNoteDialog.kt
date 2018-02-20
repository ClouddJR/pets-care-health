package com.clouddroid.pettypetscarehealth.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.repositories.NotesRepository
import kotlinx.android.synthetic.main.dialog_add_note.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import petrov.kristiyan.colorpicker.ColorPicker
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by arkadiusz on 14.02.18.
 */
class AddNoteDialog(private val passedContext: Context, themeResId: Int) : Dialog(passedContext, themeResId) {


    private val notesRepository = NotesRepository()
    private val arrayOfColors = arrayListOf("#80d8ff", "#ff8a80", "#ffd180", "#ffffff", "#ffff8d", "#ccff90", "#a7ffeb", "#82b1ff", "#b388ff", "#f8bbd0", "#d7ccc8", "#cfd8dc")
    private var chosenColor = -115
    private var animalKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_note)
        setOnColorImageClick()
        setOnAddNoteButtonClick()
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
        return titleEditText.text.isNotEmpty() ||
                contentEditText.text.isNotEmpty()

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
                notesRepository.addNewNote(animalKey, currentDate(), titleEditText.text.toString(), chosenColor, contentEditText.text.toString())
                super.dismiss()
            } else {
                passedContext.toast(R.string.add_note_dialog_toast_form_invalid)
            }
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