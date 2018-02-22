package com.clouddroid.pettypetscarehealth.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.adapters.MeasurementDialogValuesRV
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.MeasurementValue
import com.clouddroid.pettypetscarehealth.repositories.AnimalsRepository
import kotlinx.android.synthetic.main.dialog_values.*

/**
 * Created by Arkadiusz on 08.01.2018
 */
class ValuesDialog(context: Context?, themeResId: Int) : Dialog(context, themeResId),
        AnimalsRepository.WeightValuesListener,
        AnimalsRepository.HeightValuesListener,
        MeasurementDialogValuesRV.OnTrashIconClick {

    private val passedContext = context
    private val animalsRepository = AnimalsRepository()
    private var RVAdapter: MeasurementDialogValuesRV = MeasurementDialogValuesRV()
    private var selectedAnimal: Animal? = null
    private var valueType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_values)
        initAddButtonListener()
        initValuesListener()
        displayRecyclerView()
    }

    private fun initAddButtonListener() {
        addValueButton.setOnClickListener {
            displayDialogToAddValue()
        }
    }

    private fun displayDialogToAddValue() {
        val dialog = AddValueDialog(passedContext!!, R.style.ChartValuesAddingDialog)
        dialog.setChartValuesType(valueType)
        dialog.setCurrentAnimal(selectedAnimal)
        dialog.show()
    }


    private fun initValuesListener() {
        animalsRepository.setHeightValuesListener(this)
        animalsRepository.setWeightValuesListener(this)
        RVAdapter.initTrashListener(this)
    }

    private fun displayRecyclerView() {
        valuesRecyclerView.setHasFixedSize(true)
        valuesRecyclerView.layoutManager = LinearLayoutManager(passedContext)
        valuesRecyclerView.addItemDecoration(DividerItemDecoration(passedContext, 1))
        valuesRecyclerView.adapter = RVAdapter
    }

    fun initSelectedAnimalAndGetValues(pet: Animal?) {
        selectedAnimal = pet
        getValuesForSelectedAnimal()
    }

    private fun getValuesForSelectedAnimal() {
        when (valueType) {
            "height" -> animalsRepository.getHeightValuesForAnimal(selectedAnimal!!.key)
            "weight" -> animalsRepository.getWeightValuesForAnimal(selectedAnimal!!.key)
        }
    }

    fun setValuesType(type: String) {
        valueType = type
        RVAdapter.initType(valueType)
    }

    override fun onHeightValuesLoaded(list: List<MeasurementValue>) {
        if (list.isEmpty()) {
            displayNoDataText()
        } else {
            hideNoDataText()
        }
        setChartValuesAndRefreshRV(list)
    }

    override fun onWeightValuesLoaded(list: List<MeasurementValue>) {
        if (list.isEmpty()) {
            displayNoDataText()
        } else {
            hideNoDataText()
        }
        setChartValuesAndRefreshRV(list)
    }

    private fun displayNoDataText() {
        noDataTextView.visibility = View.VISIBLE
    }

    private fun hideNoDataText() {
        noDataTextView.visibility = View.GONE
    }

    private fun setChartValuesAndRefreshRV(list: List<MeasurementValue>) {
        RVAdapter.initValuesList(list)
        RVAdapter.notifyDataSetChanged()
    }

    override fun onTrashClicked(value: MeasurementValue) {
        when (valueType) {
            "height" -> animalsRepository.deleteHeightValue(value.x, selectedAnimal!!.key)
            "weight" -> animalsRepository.deleteWeightValue(value.x, selectedAnimal!!.key)
        }
    }
}