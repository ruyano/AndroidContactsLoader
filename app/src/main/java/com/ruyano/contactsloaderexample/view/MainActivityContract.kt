package com.ruyano.contactsloaderexample.view

import android.os.Parcelable
import com.ruyano.contactsloader.Contact

interface MainActivityContract {

    interface View {
        fun updateItensVisibility(recyclerViewVisibility: Int, loadingVisibility: Int, emptyViewVisibility: Int)
        fun updateRecyclerView(contactsList: List<Contact>)
        fun setNewList(contactsList: List<Contact>)
        fun showRecyclerViewLoading()
        fun hideRecyclerViewLoading()
    }

    interface Presenter {
        fun unBindView()
        fun getContactsFirstPage()
        fun getContactsNewPage()
        fun getContacts(name: String)
        fun getCurrentPage(): Int
        fun setCurrentPage(page: Int)
        fun isLoaging(): Boolean
        fun isLastPage(): Boolean
        fun setIsLastPage(isLastPage: Boolean)
        fun getSearchViewState(): String?
        fun setIsPaginating(isPaginating: Boolean)
        fun isPaginating(): Boolean
        fun getContactsListState(): List<Contact>?
        fun getRecyclerViewState(): Parcelable?
    }
}