package com.clouddroid.pettypetscarehealth.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.repositories.AnimalsRepository
import com.clouddroid.pettypetscarehealth.repositories.StorageRepository
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_animal.*
import kotlinx.android.synthetic.main.content_add_animal.*
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddAnimalActivity : AppCompatActivity() {

    private val WRITE_RQ = 1234
    private val animalsRepository = AnimalsRepository()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_animal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpSpinner()
        initOnClickListeners()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //open the crop activity
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(this, data)
            if (Build.VERSION.SDK_INT >= 23) {
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
                } else {
                    startCropImageActivity(imageUri)
                }
            } else {
                startCropImageActivity(imageUri)
            }
        }

        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            imageUri = StorageRepository.saveFile(CropImage.getActivityResult(data).uri as Uri)
            Picasso.with(this).load(File(imageUri?.path)).into(petImage)
        }
    }

    private fun initOnClickListeners() {
        imageButton.setOnClickListener { askForPermissionsAndDisplayCropActivity() }
        dateEditText.setOnClickListener(showDatePicker)
        addAnimalButton.setOnClickListener {
            if (validateForm()) {
                animalsRepository.addNewAnimal(imageUri ?: Uri.parse(""),
                        nameEditText.text.toString(),
                        dateEditText.text.toString(),
                        breedEditText.text.toString(),
                        colorEditText.text.toString(),
                        (genderSpinner.getChildAt(0) as TextView).text.toString(),
                        intent.extras.getString("animalType"))

                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, R.string.add_activity_toast_form, Toast.LENGTH_LONG).show()
            }
        }

    }

    private val showDatePicker = View.OnClickListener {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, chosenYear, chosenMonth, chosenDay ->
            val date = "$chosenYear-$chosenMonth-$chosenDay"
            dateEditText.setText(date)
        }, year, month, day).show()
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_animal_item)
        genderSpinner.adapter = adapter
    }


    private fun validateForm(): Boolean {
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
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_RQ)
        } else {
            //permission already granted, so display crop dialog
            CropImage.startPickImageActivity(this)
        }
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri)
                .start(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_RQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this)
            }
        }
    }

}
