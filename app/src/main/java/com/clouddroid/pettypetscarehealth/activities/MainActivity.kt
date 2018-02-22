package com.clouddroid.pettypetscarehealth.activities

import PreferenceUtils.defaultPrefs
import PreferenceUtils.get
import PreferenceUtils.set
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.dialogs.*
import com.clouddroid.pettypetscarehealth.fragments.*
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.repositories.UserRepository
import com.clouddroid.pettypetscarehealth.utils.StorageUtils
import com.clouddroid.pettypetscarehealth.viewmodels.AnimalViewModel
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.layout_app_bar_main.*
import kotlinx.android.synthetic.main.layout_content_main.*
import kotlinx.android.synthetic.main.layout_drawer_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.email
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {

    private var animalViewModel: AnimalViewModel? = null
    private val userRepository = UserRepository()
    private var currentAnimal: Animal? = null
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = InfoFragment()
    private var fragmentToBePlaced: Fragment? = null

    private var galleryDialog: AddGalleryItemDialog? = null
    private var wasItemClicked: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        redirectToLogin()
        setContentView(R.layout.layout_drawer_main)
        setSupportActionBar(toolbar)
        connectWithViewModel()
        observeAnimalData()
        initializeActiveFragment()
        setUpHamburgerIcon()
        setupDrawerLayout()
        removeTitle()
        setFabOnClickListeners()
        initEditAnimalButton()
    }

    private fun redirectToLogin() {
        if (!userRepository.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            finish()
        }
    }

    private fun connectWithViewModel() {
        animalViewModel = ViewModelProviders.of(this).get(AnimalViewModel::class.java)
        animalViewModel?.initialize()
    }

    private fun observeAnimalData() {
        animalViewModel?.getAnimalsList()?.observe(this, Observer {
            if (it?.isEmpty() == true) {
                hideViewsRelatedToAnimal()
                setEmptySelectedAnimal()
            } else {
                showViewsRelatedToAnimal()
                setUpSpinner(it as List<Animal>)
            }
        })
    }

    private fun hideViewsRelatedToAnimal() {
        spinnerAnimals?.visibility = View.GONE
        editAnimalButton?.visibility = View.GONE
    }

    private fun setEmptySelectedAnimal() {
        currentAnimal = null
        animalViewModel?.setSelectedAnimal(Animal())
    }

    private fun showViewsRelatedToAnimal() {
        spinnerAnimals?.visibility = View.VISIBLE
        editAnimalButton?.visibility = View.VISIBLE
    }

    private fun initializeActiveFragment() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, activeFragment)
        fragmentTransaction.commit()
    }

    private fun setUpHamburgerIcon() {
        val toggle = ActionBarDrawerToggle(this, layoutDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        layoutDrawer.addDrawerListener(toggle) //may be broken
        toggle.syncState()
    }

    private fun setupDrawerLayout() {
        navigationView.setNavigationItemSelectedListener { item ->
            if (!item.isChecked) {
                wasItemClicked = true
                item.isChecked = true
                when (item.itemId) {
                    R.id.menu_nav_info -> {
                        fragmentToBePlaced = InfoFragment()
                    }
                    R.id.menu_nav_notes -> {
                        fragmentToBePlaced = NotesFragment()
                    }
                    R.id.menu_nav_medical -> {
                        fragmentToBePlaced = MedicalsFragment()
                    }
                    R.id.menu_nav_gallery -> {
                        fragmentToBePlaced = GalleryFragment()
                    }
                    R.id.menu_nav_reminders -> {
                        fragmentToBePlaced = RemindersFragment()
                    }
                    R.id.menu_nav_vets -> {
                        fragmentToBePlaced = VetsFragment()
                    }
                    R.id.menu_nav_shops -> {
                        fragmentToBePlaced = ShopsFragment()
                    }
                    R.id.menu_nav_sign_out -> {
                        displaySignOutDialog()
                        wasItemClicked = false
                    }
                }
            }
            layoutDrawer.closeDrawer(GravityCompat.START)
            true
        }


        layoutDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                //not used
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //not used
            }

            override fun onDrawerClosed(drawerView: View) {
                if (wasItemClicked) {
                    replaceActiveFragmentWith(fragmentToBePlaced!!)
                }
                wasItemClicked = false
            }

            override fun onDrawerOpened(drawerView: View) {
                //not used
            }
        })
    }

    private fun replaceActiveFragmentWith(fragment: Fragment) {
        if (activeFragment.javaClass != fragment.javaClass) {
            activeFragment = fragment
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, activeFragment)
            fragmentTransaction.commit()
        }
    }


    private fun removeTitle() {
        supportActionBar?.title = ""
    }

    private fun setFabOnClickListeners() {
        fabAnimal.setOnClickListener { displayAnimalPickerDialog() }
        fabNote.setOnClickListener { displayNoteDialog() }
        fabMedical.setOnClickListener { displayMedicalDialog() }
        fabGallery_item.setOnClickListener { displayGalleryItemDialog() }
        fabReminder.setOnClickListener { displayReminderDialog() }
    }

    private fun initEditAnimalButton() {
        editAnimalButton.setOnClickListener {
            startActivity<EditAnimalActivity>("selectedAnimal" to currentAnimal)
        }
    }

    private fun displaySignOutDialog() {
        alert(R.string.main_activity_sign_out_dialog) {
            positiveButton(R.string.main_activity_sign_out_yes) {
                userRepository.signOut()
                startActivity<LoginActivity>()
                finish()
            }
            negativeButton(R.string.main_activity_sign_out_no) {
                it.dismiss()
            }

        }.show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_main_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            R.id.menu_main_contact -> {
                email("arekchmura@gmail.com", "Pet's care & health app")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (fabMenu.isOpened) {
            fabMenu.close(true)
            return
        }
        if (activeFragment !is InfoFragment) {
            replaceActiveFragmentWith(InfoFragment())
            navigationView.setCheckedItem(R.id.menu_nav_info)
            return
        }
        if (activeFragment is InfoFragment) {
            displayExitDialog()
        }
    }

    private fun displayExitDialog() {
        alert(R.string.main_activity_exit_dialog) {
            positiveButton(R.string.main_activity_exit_yes) {
                super.onBackPressed()
            }
            negativeButton(R.string.main_activity_exit_no) { }
        }.show()
    }

    private fun displayAnimalPickerDialog() {
        val dialog = DialogAnimalPicker(this, R.style.AnimalDialog)
        dialog.show()
        fabMenu.close(true)
    }

    private fun displayNoteDialog() {
        val dialog = AddNoteDialog(this, R.style.NoteDialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.setCurrentAnimalKey(currentAnimal?.key ?: "")
        fabMenu.close(true)
    }

    private fun displayMedicalDialog() {
        val dialog = AddMedicalDialog(this, R.style.NoteDialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.setCurrentAnimalKey(currentAnimal?.key ?: "")
        fabMenu.close(true)
    }

    private fun displayGalleryItemDialog() {
        galleryDialog = AddGalleryItemDialog(this, R.style.NoteDialog)
        galleryDialog?.setCanceledOnTouchOutside(false)
        galleryDialog?.show()
        galleryDialog?.setCurrentAnimalKey(currentAnimal?.key ?: "")
        fabMenu.close(true)
    }

    private fun displayReminderDialog() {
        val dialog = AddReminderDialog(this, R.style.NoteDialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialog.setCurrentAnimal(currentAnimal ?: Animal())
        fabMenu.close(true)
    }

    private fun setUpSpinner(listOfAnimals: List<Animal>) {
        val listOfNames = mutableListOf<String>()
        listOfAnimals.mapTo(listOfNames) { it.name }

        val prefs = defaultPrefs(this)
        val adapter = ArrayAdapter(this, R.layout.spinner_animal_item, listOfNames)
        spinnerAnimals?.adapter = adapter
        spinnerAnimals?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                (view as? TextView?)?.setTextColor(Color.WHITE)
                animalViewModel?.setSelectedAnimal(listOfAnimals[i])
                currentAnimal = listOfAnimals[i]
                prefs["selectedPosition"] = i
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                //nothing here
            }
        }

        if (prefs["selectedPosition"] ?: 0 < listOfAnimals.size) {
            spinnerAnimals?.setSelection(prefs["selectedPosition", 0]!!, true)
        }
        val view = spinnerAnimals.selectedView
        (view as? TextView)?.setTextColor(Color.WHITE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageData: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageData)
        if (galleryDialog?.isShowing == true && requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageData?.let {
                galleryDialog?.setChosenImageUri(Uri.parse(StorageUtils.getPath(this, imageData.data)))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            galleryDialog?.setPermissionGranted()
        }

        if (requestCode == 1234 && grantResults[0] == PackageManager.PERMISSION_GRANTED && activeFragment is VetsFragment) {
            (activeFragment as VetsFragment).setLocationPermissionGranted()
        }

        if (requestCode == 1234 && grantResults[0] == PackageManager.PERMISSION_GRANTED && activeFragment is ShopsFragment) {
            (activeFragment as ShopsFragment).setLocationPermissionGranted()
        }
    }

}
