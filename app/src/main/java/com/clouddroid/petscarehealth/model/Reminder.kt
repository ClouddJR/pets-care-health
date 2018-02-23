package com.clouddroid.petscarehealth.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by arkadiusz on 19.02.18
 */

data class Reminder(val key: String = "",
                    val text: String = "",
                    val year: Int = 0,
                    val month: Int = 0,
                    val day: Int = 0,
                    val hour: Int = 0,
                    val minute: Int = 0,
                    val numberIntervals: Int = 0,
                    val typeInterval: String = "",
                    val animalName: String = "") : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(text)
        parcel.writeInt(year)
        parcel.writeInt(month)
        parcel.writeInt(day)
        parcel.writeInt(hour)
        parcel.writeInt(minute)
        parcel.writeInt(numberIntervals)
        parcel.writeString(typeInterval)
        parcel.writeString(animalName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reminder> {
        override fun createFromParcel(parcel: Parcel): Reminder {
            return Reminder(parcel)
        }

        override fun newArray(size: Int): Array<Reminder?> {
            return arrayOfNulls(size)
        }
    }
}