package com.ruyano.contactsloaderexample.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ruyano.contactsloader.Contact
import com.ruyano.contactsloaderexample.R
import kotlinx.android.synthetic.main.my_recycler_view_item.view.*


class ContactsRecyclerViewAdapter(private val list: ArrayList<Contact> = ArrayList()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLoading: Boolean = false
    val VIEW_TYPE_ITEM = 1
    val VIEW_TYPE_LOADER = 2

    override fun getItemViewType(position: Int) = if (position >= list.size) VIEW_TYPE_LOADER else VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when(viewType) {
            VIEW_TYPE_LOADER -> LoaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_recycler_view_loading_item, parent, false))
            else -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_recycler_view_item, parent, false))
        }

    // +1 if loading
    override fun getItemCount() = list.size + (if (isLoading) 1 else 0)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(position + 1, list[position])
        }
    }

    fun addOnContactList(contacts: List<Contact>) {
        list.addAll(contacts)
        notifyDataSetChanged()
    }

    fun setNewList(contacts: List<Contact>) {
        list.clear()
        list.addAll(contacts)
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<Contact>  {
        return list
    }

    fun showLoading() {
        isLoading = true
        notifyItemInserted(itemCount)
    }

    fun hideLoading() {
        isLoading = false
        notifyItemRemoved(itemCount + 1)
    }

    inner class ItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        fun bind(position: Int, currentItem: Contact) {
            val name = currentItem.name
            view.tv_name.text = name
            view.tv_phone.text = getPhones(currentItem)
            if (!currentItem.imageUrl.isNullOrEmpty()) {
                val uri = Uri.parse(currentItem.imageUrl)
                view.iv_photo.setImageURI(uri)
            }
        }

        private fun getPhones(currentItem: Contact): String {
            val phonesList = currentItem.phoneList
            var phones = ""
            if (phonesList.isNotEmpty()) {
                for (phone in phonesList) {
                    phones += phone
                    if (phonesList.indexOf(phone) != (phonesList.size - 1)) {
                        phones += "\n"
                    }
                }
            } else {
                phones = "-"
            }
            return phones
        }
    }

    inner class LoaderViewHolder(private val view: View): RecyclerView.ViewHolder(view)

}