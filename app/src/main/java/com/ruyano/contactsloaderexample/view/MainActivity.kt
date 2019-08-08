package com.ruyano.contactsloaderexample.view

import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruyano.contactsloader.Contact
import com.ruyano.contactsloader.ContactsLoader
import com.ruyano.contactsloaderexample.R
import com.ruyano.contactsloaderexample.util.PermissionManager
import com.ruyano.contactsloaderexample.util.PaginationScrollListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), MainActivityContract.View, PermissionManager.PermissionManagerListener {

    private var MODEL_STATE = "MODEL_STATE"
    private lateinit var mAdapter: ContactsRecyclerViewAdapter
    private lateinit var mPresenter: MainActivityContract.Presenter
    private lateinit var mPermissionManager: PermissionManager
    private val disposables = CompositeDisposable()
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPermissionManager = PermissionManager(this, this)

        var model = MainActivityModel()
        savedInstanceState?.let {
           model = savedInstanceState.getParcelable(MODEL_STATE)!!
        }
        mPresenter = MainActivityPresenter(this, ContactsLoader(this), model)

        setupRecyclerView()

        if (!mPermissionManager.checkReadContactsPermission()) {
            mPermissionManager.requestReadContactsPermission()
            return
        }

        if (savedInstanceState == null) {
            requestFirstPage()
        }
    }

    override fun onDestroy() {
        disposables.dispose()
        mPresenter.unBindView()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.imeOptions = searchView.imeOptions or
                    EditorInfo.IME_ACTION_SEARCH or
                    EditorInfo.IME_FLAG_NO_EXTRACT_UI or
                    EditorInfo.IME_FLAG_NO_FULLSCREEN

        val searchViewState = mPresenter.getSearchViewState()
        searchViewState?.let {
            if (!searchViewState.isNullOrEmpty()) {
                searchView.setQuery(searchViewState, false)
                searchItem.expandActionView()
                searchView.isIconified = false
            }
        }

        val d = io.reactivex.Observable.create<String> { emitter ->
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    emitter.onNext(newText)
                    return true
                }

            })
        }
            .debounce(500, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.newThread())
            .subscribe { str -> runOnUiThread { loadMyContacts(str) } }

        disposables.add(d)

        val closeBtn = searchView.findViewById<AppCompatImageView>(R.id.search_close_btn)
        closeBtn.setOnClickListener {
            mPresenter.setIsPaginating(true)
            searchView.setQuery("", false)
            searchView.onActionViewCollapsed()
            searchItem.collapseActionView()
            requestFirstPage()
        }

        return super.onCreateOptionsMenu(menu)
    }

    fun loadMyContacts(str: String) {
        if (str.length >= 3) {
            mPresenter.setIsPaginating(false)
            mPresenter.getContacts(str)
        }
    }

    private fun requestFirstPage() {
        mPresenter.getContactsFirstPage()
    }

    private fun setupRecyclerView() {
        mAdapter = ContactsRecyclerViewAdapter()
        my_recycer_view.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addOnScrollListener(object : PaginationScrollListener(layoutManager as LinearLayoutManager) {
                override fun isLastPage(): Boolean {
                    return mPresenter.isLastPage()
                }

                override fun isLoading(): Boolean {
                    return mPresenter.isLoaging()
                }

                override fun loadMoreItens() {
                    if (mPresenter.isPaginating()) {
                        mPresenter.getContactsNewPage()
                    }
                }
            })
        }
    }

    override fun updateRecyclerView(contactsList: List<Contact>) {
        runOnUiThread {
            mAdapter.addOnContactList(contactsList)
        }
    }

    override fun setNewList(contactsList: List<Contact>) {
        runOnUiThread {
            mAdapter.setNewList(contactsList)
        }
    }

    override fun updateItensVisibility(recyclerViewVisibility: Int,
                                       loadingVisibility: Int,
                                       emptyViewVisibility: Int) {
        runOnUiThread {
            my_recycer_view.visibility = recyclerViewVisibility
            progress_bar.visibility = loadingVisibility
            tv_no_contacts_found.visibility = emptyViewVisibility
        }
    }

    override fun showRecyclerViewLoading() {
        runOnUiThread {
            mAdapter.showLoading()
        }
    }

    override fun hideRecyclerViewLoading() {
        runOnUiThread {
            mAdapter.hideLoading()
        }
    }

    override fun onResume() {
        super.onResume()
        val listState = mPresenter.getContactsListState()
        listState?.let {
            mAdapter.setNewList(listState)
        }

        val recyclerViewState = mPresenter.getRecyclerViewState()
        recyclerViewState?.let {
            my_recycer_view.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var searchViewState: String? = null
        if (::searchView.isInitialized) {
            searchViewState = searchView.query.toString()
        }
        outState.putParcelable(MODEL_STATE, MainActivityModel(
            my_recycer_view.layoutManager?.onSaveInstanceState(),
            mAdapter.getList(),
            mPresenter.getCurrentPage(),
            mPresenter.isLoaging(),
            mPresenter.isLastPage(),
            mPresenter.isPaginating(),
            searchViewState)
        )
    }

    override fun onPermissionGranted() {
        requestFirstPage()
    }

    override fun onPermissionRejected() {
        mPermissionManager.requestReadContactsPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
