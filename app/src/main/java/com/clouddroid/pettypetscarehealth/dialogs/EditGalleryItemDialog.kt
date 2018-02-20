package com.clouddroid.pettypetscarehealth.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.GalleryItem
import com.clouddroid.pettypetscarehealth.repositories.ImagesRepository
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatDate
import kotlinx.android.synthetic.main.dialog_edit_gallery_item.*
import org.jetbrains.anko.alert
import java.util.*

/**
 * Created by arkadiusz on 17.02.18
 */

class EditGalleryItemDialog(private val passedContext: Context, themeResId: Int) : Dialog(passedContext, themeResId) {

    private val imagesRepository = ImagesRepository()
    private var passedAnimal: Animal? = null
    private var passedImage: GalleryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_gallery_item)
        setOnEditImageButtonClick()
        fillFormWithPassedData()
        setOnDateEditTextClick()
        setOnDeleteImageClick()
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
                dateEditText.text.toString().isNotEmpty()
    }

    fun setCurrentAnimal(animal: Animal) {
        passedAnimal = animal
    }

    fun setPassedImage(item: GalleryItem) {
        passedImage = item
    }


    private fun setOnEditImageButtonClick() {
        editImageButton.setOnClickListener {
            imagesRepository.editImageForAnimal(passedAnimal?.key ?: "", passedImage?.key ?: "",
                    titleEditText.text.toString(),
                    dateEditText.text.toString(),
                    passedImage?.path ?: "",
                    passedImage?.cachePath ?: "")
            super.dismiss()
        }
    }

    private fun fillFormWithPassedData() {
        titleEditText.setText(passedImage?.title)
        dateEditText.setText(passedImage?.date)
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


    private fun setOnDeleteImageClick() {
        deleteGalleryItemButton.setOnClickListener {
            passedContext.alert(R.string.edit_image_dialog_delete_question) {
                positiveButton(R.string.edit_image_dialog_delete_YES) {
                    imagesRepository.deleteGalleryImage(passedAnimal?.key ?: "",
                            passedImage?.key ?: "")
                    imagesRepository.deleteImage(passedImage?.path ?: "image")
                    super.dismiss()
                }
                negativeButton(R.string.edit_image_dialog_delete_NO) {
                    it.dismiss()
                }
            }.show()
        }
    }
}