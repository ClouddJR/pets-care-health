package com.clouddroid.pettypetscarehealth.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.repositories.AnimalsRepository


class InfoFragment : Fragment() {

    private val animalRepository = AnimalsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animalRepository.getAnimals()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_info, container, false)
}

