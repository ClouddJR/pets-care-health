package com.clouddroid.pettypetscarehealth.activities

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.dialogs.DialogAnimalPicker
import com.clouddroid.pettypetscarehealth.fragments.InfoFragment
import com.clouddroid.pettypetscarehealth.repositories.UserRepository
import com.clouddroid.pettypetscarehealth.viewmodels.AnimalViewModel
import kotlinx.android.synthetic.main.layout_app_bar_main.*
import kotlinx.android.synthetic.main.layout_content_main.*
import kotlinx.android.synthetic.main.layout_drawer_main.*

class MainActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager
    private var activeFragment = InfoFragment()
    private val userRepository = UserRepository()
    private var animalViewModel: AnimalViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        redirectToLogin()
        setContentView(R.layout.layout_drawer_main)
        setSupportActionBar(toolbar)
        connectWithViewModel()
        observeAnimalData()
        initializeInfoFragment()
        setUpHamburgerIcon()
        setupDrawerLayout()
        removeTitle()
        setFabOnClickListeners()
    }

    private fun redirectToLogin() {
        if (!userRepository.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            finish()
        }
    }

    private fun removeTitle() {
        val actionBar = supportActionBar
        actionBar?.title = ""
    }

    private fun connectWithViewModel() {
        animalViewModel = ViewModelProviders.of(this).get(AnimalViewModel::class.java)
        animalViewModel?.initialize()
    }

    private fun observeAnimalData() {
        animalViewModel?.getAnimalsList()?.observe(this, Observer {
            setUpSpinner(it as List<Animal>)
        })
    }

    private fun initializeInfoFragment() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, activeFragment)
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
                item.isChecked = true
                when (item.itemId) {
                    R.id.menu_nav_info -> {
                        activeFragment = InfoFragment()
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction?.add(R.id.fragmentContainer, activeFragment)
                    }
                    R.id.menu_nav_sign_out -> {
                        val dialog = AlertDialog.Builder(this)
                        dialog.setMessage(R.string.main_activity_sign_out_dialog)
                        dialog.setPositiveButton(R.string.main_activity_sign_out_yes) { _, _ ->
                            userRepository.signOut()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        dialog.setNegativeButton(R.string.main_activity_sign_out_no, { _, _ ->
                            //nothing here
                        })
                        dialog.show()
                    }
                }
            }
            layoutDrawer.closeDrawers()
            true
        }
    }


    private fun setFabOnClickListeners() {
        fabAnimal.setOnClickListener { displayAnimalPickerDialog() }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun displayAnimalPickerDialog() {
        val dialog = DialogAnimalPicker(this, R.style.AnimalDialog)
        dialog.show()
        fabMenu.close(true)
    }

    private fun setUpSpinner(listOfAnimals: List<Animal>) {
        val listOfNames = mutableListOf<String>()
        listOfAnimals.mapTo(listOfNames) { it.name }

        val adapter = ArrayAdapter(this, R.layout.spinner_animal_item, listOfNames)
        spinnerAnimals?.adapter = adapter
        spinnerAnimals?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                (view as? TextView?)?.setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                //nothing here
            }

        }
        spinnerAnimals?.setSelection(0, true)
        val view = spinnerAnimals.selectedView
        (view as? TextView)?.setTextColor(Color.WHITE)
    }

}
