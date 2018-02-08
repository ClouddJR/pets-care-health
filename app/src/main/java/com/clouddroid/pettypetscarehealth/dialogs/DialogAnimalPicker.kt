package com.clouddroid.pettypetscarehealth.dialogs

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.v7.app.AlertDialog
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.activities.AddAnimalActivity
import kotlinx.android.synthetic.main.dialog_animal_picker.*

/**
 * Created by Arkadiusz on 13.07.2017
 */

class DialogAnimalPicker(internal var context: Context, @StyleRes themeResId: Int) : Dialog(context, themeResId) {

    private var customType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_animal_picker)
        dialog_image_dog.setOnClickListener { pickAnimal(it as ImageView) }
        dialog_image_hamster.setOnClickListener { pickAnimal(it as ImageView) }
        dialog_image_rabbit.setOnClickListener { pickAnimal(it as ImageView) }
        dialog_image_fish.setOnClickListener { pickAnimal(it as ImageView) }
        dialog_image_cat.setOnClickListener { pickAnimal(it as ImageView) }
        dialog_image_parrot.setOnClickListener { pickAnimal(it as ImageView) }
        dialog_image_turtle.setOnClickListener { pickAnimal(it as ImageView) }
        dialog_image_other.setOnClickListener { displayDialog(it as ImageView) }

    }

    private fun pickAnimal(imageAnimal: ImageView) {
        val animalType: String = when (imageAnimal.id) {
            R.id.dialog_image_dog -> "dog"
            R.id.dialog_image_hamster -> "hamster"
            R.id.dialog_image_rabbit -> "rabbit"
            R.id.dialog_image_fish -> "fish"
            R.id.dialog_image_cat -> "cat"
            R.id.dialog_image_parrot -> "parrot"
            R.id.dialog_image_turtle -> "turtle"
            R.id.dialog_image_other -> customType
            else -> "unknown"
        }
        imageAnimal.animate()
                .rotationY(360f)
                .setDuration(500)
                .setInterpolator(LinearInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animator: Animator) {
                        imageAnimal.rotationY = 0f
                        val intent = Intent(context, AddAnimalActivity::class.java)
                        intent.putExtra("animalType", animalType)
                        dismiss()
                        context.startActivity(intent)
                    }
                })
    }

    private fun displayDialog(imageAnimal: ImageView) {
        val dialog = AlertDialog.Builder(context, R.style.AnimalOtherDialog)
        val editText = EditText(context)
        editText.hint = "Enter animal type"
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        editText.layoutParams = layoutParams
        dialog.setView(editText)
        dialog.setPositiveButton("ok", { _, _ ->
            customType = editText.text.toString()
            pickAnimal(imageAnimal)
        })
        dialog.show()
    }

}
