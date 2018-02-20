package com.clouddroid.pettypetscarehealth.adapters

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.clouddroid.pettypetscarehealth.R
import com.clouddroid.pettypetscarehealth.dialogs.EditGalleryItemDialog
import com.clouddroid.pettypetscarehealth.model.Animal
import com.clouddroid.pettypetscarehealth.model.GalleryItem
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.dialog_gallery_detail.view.*
import kotlinx.android.synthetic.main.gallery_item.view.*
import org.jetbrains.anko.layoutInflater
import java.io.File

/**
 * Created by arkadiusz on 17.02.18.
 */
class ImagesRV : RecyclerView.Adapter<ImagesRV.ViewHolder>() {

    private var imagesList: List<GalleryItem>? = null
    private var currentAnimal: Animal? = null

    fun initImagesList(list: List<GalleryItem>) {
        imagesList = list
    }

    fun updateCurrentAnimal(animal: Animal) {
        currentAnimal = animal
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.gallery_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindElement(imagesList?.get(position))
    }

    override fun getItemCount(): Int {
        return imagesList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: View = itemView

        fun bindElement(item: GalleryItem?) {
            when {
                File(item?.cachePath).exists() -> Glide.with(view.context).load(File(item?.cachePath)).into(view.itemImage)
                item?.path?.isNotEmpty() == true -> Glide.with(view.context).load(FirebaseStorage.getInstance().getReference(item.path)).into(view.itemImage)
            }

            view.titleTextView.text = item?.title
            view.dateTextView.text = item?.date

            val context = view.context
            view.mainCardView.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                val inflater = context.layoutInflater
                val view = inflater.inflate(R.layout.dialog_gallery_detail, null)
                builder.setView(view)
                view.titleTV.text = item?.title
                view.dateTV.text = item?.date

                when {
                    File(item?.cachePath).exists() -> Glide.with(context).load(File(item?.cachePath)).into(view.itemImg)
                    item?.path?.isNotEmpty() == true -> Glide.with(context).load(FirebaseStorage.getInstance().getReference(item.path)).into(view.itemImg)
                }

                val dialog = builder.create()
                dialog.show()

                view.imageEdit.setOnClickListener {
                    dialog.dismiss()
                    val editDialog = EditGalleryItemDialog(context, R.style.NoteDialog)
                    editDialog.setCanceledOnTouchOutside(false)
                    editDialog.setCurrentAnimal(currentAnimal!!)
                    editDialog.setPassedImage(item!!)
                    editDialog.show()
                }
            }
        }
    }
}