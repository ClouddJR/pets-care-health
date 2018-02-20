package com.clouddroid.pettypetscarehealth.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Arkadiusz on 01.12.2017
 */
data class Animal(val key: String = "",
                  val name: String = "",
                  val date: String = "",
                  val breed: String = "",
                  val color: String = "",
                  val gender: String = "",
                  val imagePath: String = "",
                  val imageCachePath: String = "",
                  val type: String = "") : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(key)
        writeString(name)
        writeString(date)
        writeString(breed)
        writeString(color)
        writeString(gender)
        writeString(imagePath)
        writeString(imageCachePath)
        writeString(type)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Animal> = object : Parcelable.Creator<Animal> {
            override fun createFromParcel(source: Parcel): Animal = Animal(source)
            override fun newArray(size: Int): Array<Animal?> = arrayOfNulls(size)
        }
    }
}