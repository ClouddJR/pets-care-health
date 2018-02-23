package com.clouddroid.petscarehealth.repositories

import android.net.Uri
import android.util.Log.d
import com.clouddroid.petscarehealth.model.GalleryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

/**
 * Created by arkadiusz on 16.02.18
 */

class ImagesRepository {

    private val mAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val databaseReference = database.getReference(mAuth.currentUser?.uid ?: "none")

    private var imagesListener: ImagesListListener? = null

    interface ImagesListListener {
        fun onImagesLoaded(list: List<GalleryItem>)
    }

    fun setImagesListListener(listener: ImagesListListener) {
        imagesListener = listener
    }

    fun addImageForAnimal(animalKey: String, imageUri: Uri): String {
        val imageName = imageUri.lastPathSegment
        val path = "${mAuth.currentUser?.uid}/$animalKey/$imageName"
        val storageReference = storage.getReference(path)
        storageReference.putFile(Uri.fromFile(File(imageUri.path)))
        return path
    }

    fun addImageForAnimal(animalKey: String, imageUri: Uri, title: String, date: String, cachePath: String) {
        val imageName = imageUri.lastPathSegment
        val path = "${mAuth.currentUser?.uid}/$animalKey/$imageName"
        val storageReference = storage.getReference(path)
        storageReference.putFile(Uri.fromFile(File(imageUri.path)))

        val key: String = databaseReference.push().key
        databaseReference.child("images").child(animalKey).child(key).child("title").setValue(title)
        databaseReference.child("images").child(animalKey).child(key).child("date").setValue(date)
        databaseReference.child("images").child(animalKey).child(key).child("path").setValue(path)
        databaseReference.child("images").child(animalKey).child(key).child("cachePath").setValue(cachePath)
        databaseReference.child("images").child(animalKey).child(key).child("key").setValue(key)
    }

    fun editImageForAnimal(animalKey: String, imageKey: String, title: String, date: String, path: String, cachePath: String) {
        databaseReference.child("images").child(animalKey).child(imageKey).child("title").setValue(title)
        databaseReference.child("images").child(animalKey).child(imageKey).child("date").setValue(date)
        databaseReference.child("images").child(animalKey).child(imageKey).child("path").setValue(path)
        databaseReference.child("images").child(animalKey).child(imageKey).child("cachePath").setValue(cachePath)
        databaseReference.child("images").child(animalKey).child(imageKey).child("key").setValue(imageKey)
    }

    fun getImagesForAnimal(animalKey: String) {
        databaseReference.child("images").child(animalKey).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                //not used
            }

            override fun onDataChange(imagesReceived: DataSnapshot?) {
                val imagesTempList = mutableListOf<GalleryItem>()
                imagesReceived?.children?.let { it.mapTo(imagesTempList) { it.getValue(GalleryItem::class.java) as GalleryItem } }
                d("galleryList", imagesTempList.toString())
                imagesListener?.onImagesLoaded(imagesTempList)
            }


        })
    }

    fun deleteGalleryImage(animalKey: String, imageKey: String) {
        databaseReference.child("images").child(animalKey).child(imageKey).removeValue()
    }

    fun deleteImagesForAnimal(animalKey: String) {
        databaseReference.child("images").child(animalKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                //not used
            }

            override fun onDataChange(imagesReceived: DataSnapshot?) {
                val imagesTempList = mutableListOf<GalleryItem>()
                imagesReceived?.children?.let { it.mapTo(imagesTempList) { it.getValue(GalleryItem::class.java) as GalleryItem } }
                d("deleteList", imagesTempList.toString())
                imagesTempList.forEach {
                    deleteImage(it.path)
                }
            }
        })
    }

    fun deleteImage(path: String) {
        if (path.isNotEmpty()) {
            val storageReference = storage.getReference(path)
            storageReference.delete()
        }
    }


}