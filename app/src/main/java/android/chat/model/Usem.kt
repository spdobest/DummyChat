package android.chat.model

import android.os.Parcel
import android.os.Parcelable

data class Usem(var name:String,var id:String):Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<Usem> {
        override fun createFromParcel(parcel: Parcel): Usem {
            return Usem(parcel)
        }

        override fun newArray(size: Int): Array<Usem?> {
            return arrayOfNulls(size)
        }
    }
}