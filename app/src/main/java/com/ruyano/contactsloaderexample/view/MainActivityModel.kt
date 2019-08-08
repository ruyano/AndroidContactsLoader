package com.ruyano.contactsloaderexample.view

import android.os.Parcelable
import com.ruyano.contactsloader.Contact
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainActivityModel(var mRecyclerViewState: Parcelable? = null,
                             var mContactsListState: List<Contact>? = null,
                             var mPage: Int = -1,
                             var mIsLoading: Boolean = false,
                             var mIsLastPage:Boolean = false,
                             var mIsPaginating: Boolean = true,
                             var mSearchViewState: String? = null) : Parcelable