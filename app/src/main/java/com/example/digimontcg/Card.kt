package com.example.digimontcg

import android.os.Parcel
import android.os.Parcelable

data class Card(
    val card_id: String = "",
    val color1: String = "",
    val color2: String = "",
    val description: String = "",
    val name: String = "",
    val pack: String = "",
    val rarity: String = "",
    val type: String = "",
    var quantity: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        card_id = parcel.readString() ?: "",
        color1 = parcel.readString() ?: "",
        color2 = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        pack = parcel.readString() ?: "",
        rarity = parcel.readString() ?: "",
        type = parcel.readString() ?: "",
        quantity = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(card_id)
        parcel.writeString(color1)
        parcel.writeString(color2)
        parcel.writeString(description)
        parcel.writeString(name)
        parcel.writeString(pack)
        parcel.writeString(rarity)
        parcel.writeString(type)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
