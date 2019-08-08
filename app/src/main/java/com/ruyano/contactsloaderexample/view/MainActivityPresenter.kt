package com.ruyano.contactsloaderexample.view

import android.os.Parcelable
import android.view.View.GONE
import android.view.View.VISIBLE
import com.ruyano.contactsloader.Contact
import com.ruyano.contactsloader.ContactsLoader
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivityPresenter(private var mView: MainActivityContract.View?,
                            private val mContactsLoader: ContactsLoader,
                            private val mModel: MainActivityModel) : MainActivityContract.Presenter {

    private val mCompositeDisposable = CompositeDisposable()
    private val mPageSize = 100

    override fun unBindView() {
        mCompositeDisposable.dispose()
        mView = null
    }

    override fun getContactsFirstPage() {
        mModel.mPage = -1
        getContactsNewPage()
    }

    override fun getContactsNewPage() {
        mModel.mPage++
        mCompositeDisposable.add(
            mContactsLoader.load(mModel.mPage, mPageSize)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe { doOnSubscribe() }
                .subscribe(this::updateList))
    }

    private fun doOnSubscribe() {
        mModel.mIsLoading = true
        if (mModel.mPage == 0) {
            mModel.mIsLastPage = false
            setVisibleItem(VisibleIten.LOADING)
        } else {
            mView?.showRecyclerViewLoading()
        }
    }

    private fun updateList(newContacts: List<Contact>) {
        mModel.mIsLoading = false
        if (mModel.mPage == 0) {
            if (newContacts.isEmpty()) {
                setVisibleItem(VisibleIten.EMPTY_VIEW)
            } else {
                setVisibleItem(VisibleIten.LIST)
                mView?.setNewList(newContacts)
            }
        } else {
            mView?.updateRecyclerView(newContacts)
        }
        if (newContacts.size < mPageSize) {
            mModel.mIsLastPage = true
            mView?.hideRecyclerViewLoading()
        }
    }

    override fun getContacts(name: String) {
        mCompositeDisposable.add(
            mContactsLoader.load(name)
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe { setVisibleItem(VisibleIten.LOADING) }
                .subscribe(this::setNewList))
    }

    override fun getCurrentPage(): Int {
        return mModel.mPage
    }

    override fun setCurrentPage(page: Int) {
        mModel.mPage = page
    }

    private fun setNewList(newContacts: List<Contact>) {
        if (newContacts.isEmpty()) {
            setVisibleItem(VisibleIten.EMPTY_VIEW)
        } else {
            setVisibleItem(VisibleIten.LIST)
            mView?.setNewList(newContacts)
        }
    }

    override fun isLoaging(): Boolean {
        return mModel.mIsLoading
    }

    override fun isLastPage(): Boolean {
        return mModel.mIsLastPage
    }

    override fun setIsLastPage(isLastPage: Boolean) {
        mModel.mIsLastPage = isLastPage
    }

    override fun getSearchViewState(): String? {
        return mModel.mSearchViewState
    }

    override fun setIsPaginating(isPaginating: Boolean) {
        mModel.mIsPaginating = isPaginating
    }

    override fun isPaginating(): Boolean {
        return mModel.mIsPaginating
    }

    override fun getContactsListState(): List<Contact>? {
        return mModel.mContactsListState
    }

    override fun getRecyclerViewState(): Parcelable? {
        return mModel.mRecyclerViewState
    }

    fun setVisibleItem(visibleIten: VisibleIten) {
        when(visibleIten) {
            VisibleIten.LIST -> mView?.updateItensVisibility(VISIBLE, GONE, GONE)
            VisibleIten.LOADING -> mView?.updateItensVisibility(GONE, VISIBLE, GONE)
            VisibleIten.EMPTY_VIEW -> mView?.updateItensVisibility(GONE, GONE, VISIBLE)
        }
    }

    enum class VisibleIten {
        LIST, LOADING, EMPTY_VIEW
    }
}