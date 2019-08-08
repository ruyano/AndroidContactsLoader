package com.ruyano.contactsloader

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Contact(val id: Long,
              val imageUrl: String? = null,
              val name: String,
              val phoneList: List<String>) : Parcelable