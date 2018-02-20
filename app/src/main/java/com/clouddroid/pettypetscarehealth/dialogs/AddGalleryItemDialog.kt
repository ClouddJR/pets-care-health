package com.clouddroid.pettypetscarehealth.dialogs

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import com.bumptech.glide.Glide
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.repositories.ImagesRepository
import com.clouddroid.pettypetscarehealth.utils.DateUtils.formatDate
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.dialog_add_gallery_item.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.io.File
import java.util.*




/**
 * Created by arkadiusz on 17.02.18
 */

class AddGalleryItemDialog(private val passedContext: Context, themeResId: Int) : Dialog(passedContext, themeResId) {

    private val imagesRepository = ImagesRepository()
    private var animalKey = ""
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_gallery_item)
        setOnAddImageButtonClick()
        setOnDateEditTextClick()
        setOnImageClick()
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
                dateEditText.text.toString().isNotEmpty() || imageUri?.path?.isNotEmpty() ?: false
    }

    fun setPermissionGranted() {
        CropImage.startPickImageActivity(passedContext as Activity)
    }

    fun setChosenImageUri(uri: Uri?) {
        imageUri = uri
        displayImageHere()
    }

    private fun displayImageHere() {
        Glide.with(passedContext).load(File(imageUri?.path)).into(itemImage)
    }

    fun setCurrentAnimalKey(key: String) {
        animalKey = key
        checkIfAnimalIsSet()
    }

    private fun checkIfAnimalIsSet() {
        if (animalKey.isEmpty()) {
            passedContext.toast(R.string.add_image_dialog_toast_animal_not_set)
            super.dismiss()
        }
    }

    private fun setOnAddImageButtonClick() {
        addGalleryItemButton.setOnClickListener {
            if (isFormValid()) {
                imagesRepository.addImageForAnimal(animalKey, imageUri ?: Uri.parse(""),
                        titleEditText.text.toString(),
                        dateEditText.text.toString(),
                        imageUri.toString())
                super.dismiss()
            } else {
                passedContext.toast(R.string.add_image_dialog_toast_form_invalid)
            }
        }
    }

    private fun isFormValid(): Boolean {
        return imageUri?.toString()?.isNotEmpty() ?: false
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


    private fun setOnImageClick() {
        imageButton.setOnClickListener {
            askForPermissionsAndDisplayCropActivity()
        }
    }

    private fun askForPermissionsAndDisplayCropActivity() {
        if (ActivityCompat.checkSelfPermission(passedContext as Activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(passedContext, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
        } else {
            CropImage.startPickImageActivity(passedContext)
        }
    }

}