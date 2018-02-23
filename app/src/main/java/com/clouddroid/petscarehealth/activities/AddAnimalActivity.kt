package com.clouddroid.petscarehealth.activities

import PreferenceUtils.defaultPrefs
import PreferenceUtils.get
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.clouddroid.petscarehealth.R
import com.clouddroid.petscarehealth.repositories.AnimalsRepository
import com.clouddroid.petscarehealth.repositories.ImagesRepository
import com.clouddroid.petscarehealth.utils.DateUtils.formatDate
import com.google.android.gms.ads.AdRequest
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_animal.*
import kotlinx.android.synthetic.main.content_add_animal.*
import org.jetbrains.anko.alert
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddAnimalActivity : AppCompatActivity() {

    private val writeRequestCode = 1234
    private val animalsRepository = AnimalsRepository()
    private val imagesRepository = ImagesRepository()
    private var imageUri: Uri? = null
    private var animalKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainView()
        setUpSpinner()
        initOnClickListeners()
        displayAd()
    }

    private fun displayAd() {
        val prefs = defaultPrefs(this)
        val areAdsRemoved: String? = prefs["removed_ads"]
        if (areAdsRemoved != "true") {
            adView.loadAd(AdRequest.Builder().build())
        } else {
            adView.visibility = View.GONE
        }
    }

    private fun setMainView() {
        setContentView(R.layout.activity_add_animal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_animal_item)
        genderSpinner.adapter = adapter
    }

    private fun initOnClickListeners() {
        imageButton.setOnClickListener { askForPermissionsAndDisplayCropActivity() }
        dateEditText.setOnClickListener(showDatePicker)
        addAnimalButton.setOnClickListener {
            if (isFormValid()) {
                animalKey = animalsRepository.generateKey()
                var imagePath: String? = ""
                if (imageUri != null) {
                    imagePath = imagesRepository.addImageForAnimal(animalKey, imageUri!!)
                }

                animalsRepository.addNewAnimal(animalKey, imagePath ?: "", imageUri?.path ?: "",
                        nameEditText.text.toString(),
                        dateEditText.text.toString(),
                        replaceSpacesWithNewLines(breedEditText.text.toString()),
                        colorEditText.text.toString(),
                        (genderSpinner.getChildAt(0) as TextView).text.toString(),
                        intent.extras.getString("animalType"))
                finish()
            } else {
                Toast.makeText(this, R.string.add_activity_toast_form, Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun replaceSpacesWithNewLines(text: String): String {
        return text.replace(" ", "\n")
    }

    override fun onBackPressed() {
        alert(R.string.dialog_back_pressed_question) {
            positiveButton(R.string.dialog_ok_button) {
                super.onBackPressed()
            }
            negativeButton(R.string.dialog_cancel_button) {
                it.dismiss()
            }
        }.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, imageData: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageData)

        if (isPossibleToOpenCropActivityAfterChoosingImage(requestCode, resultCode)) {
            val chosenImageUri = CropImage.getPickImageResultUri(this, imageData)
            if (Build.VERSION.SDK_INT >= 23) {
                checkForReadingExternalStoragePermissionsAndStartCropActivity(chosenImageUri)
            } else {
                startCropImageActivity(chosenImageUri)
            }
        }

        if (isResultComingWithImageAfterCropping(requestCode)) {
            displayImageHere(imageData)
        }
    }

    private fun isPossibleToOpenCropActivityAfterChoosingImage(requestCode: Int, resultCode: Int): Boolean {
        return requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkForReadingExternalStoragePermissionsAndStartCropActivity(chosenImageUri: Uri) {
        if (CropImage.isReadExternalStoragePermissionsRequired(this, chosenImageUri)) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
        } else {
            startCropImageActivity(chosenImageUri)
        }
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri)
                .start(this)
    }

    private fun isResultComingWithImageAfterCropping(requestCode: Int): Boolean {
        return requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
    }

    private fun displayImageHere(data: Intent?) {
        data?.let {
            imageUri = CropImage.getActivityResult(data).uri as Uri
            Glide.with(this).load(File(imageUri?.path)).into(petImage)
        }
    }


    private val showDatePicker = View.OnClickListener {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, chosenYear, chosenMonth, chosenDay ->
            val date = formatDate(chosenYear, chosenMonth, chosenDay)
            dateEditText.setText(date)
        }, year, month, day).show()
    }

    private fun isFormValid(): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        try {
            format.parse(dateEditText.text.toString())
        } catch (e: ParseException) {
            return false
        }

        if (nameEditText.text.toString() == "") {
            return false
        }

        return true
    }

    private fun askForPermissionsAndDisplayCropActivity() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), writeRequestCode)
        } else {
            //permission already granted, so display crop dialog
            CropImage.startPickImageActivity(this)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == writeRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            CropImage.startPickImageActivity(this)
        }
    }

}
