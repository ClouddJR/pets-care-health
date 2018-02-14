package com.clouddroid.pettypetscarehealth.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.dialogs.DialogAnimalPicker
import com.clouddroid.pettypetscarehealth.fragments.InfoFragment
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.repositories.UserRepository
import com.clouddroid.pettypetscarehealth.viewmodels.AnimalViewModel
import kotlinx.android.synthetic.main.layout_app_bar_main.*
import kotlinx.android.synthetic.main.layout_content_main.*
import kotlinx.android.synthetic.main.layout_drawer_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment = InfoFragment()
    private var animalViewModel: AnimalViewModel? = null
    private val userRepository = UserRepository()
    private var currentAnimal: Animal? = null


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
                item.isChecked = true
                when (item.itemId) {
                    R.id.menu_nav_info -> {
                        replaceActiveFragmentWith(InfoFragment())
                    }
                    R.id.menu_nav_sign_out -> {
                        displaySignOutDialog()
                    }
                }
            }
            layoutDrawer.closeDrawers()
            true
        }
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
                animalViewModel?.setSelectedAnimal(listOfAnimals[i])
                currentAnimal = listOfAnimals[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                //nothing here
            }
        }

        //terrible way of doing this but can't see the other possibility right now
        spinnerAnimals?.setSelection(0, true)
        val view = spinnerAnimals.selectedView
        (view as? TextView)?.setTextColor(Color.WHITE)
    }

}
