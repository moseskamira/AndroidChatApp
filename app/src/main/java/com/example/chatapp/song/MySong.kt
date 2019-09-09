package com.example.chatapp.song

import android.os.Parcel
import android.os.Parcelable

class MySong(private val songImage: String?, private val songTitle: String?): Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString())
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(songImage)
        dest.writeValue(songTitle)
    }
    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MySong> {
        override fun createFromParcel(parcel: Parcel): MySong {
            return MySong(parcel)
        }
        override fun newArray(size: Int): Array<MySong?> = arrayOfNulls(size)
    }
}