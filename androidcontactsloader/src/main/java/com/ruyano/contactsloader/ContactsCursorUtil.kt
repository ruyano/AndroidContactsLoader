package com.ruyano.contactsloader

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract

class ContactsCursorUtil {

    companion object {
        private val PROJECTION = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.Data.PHOTO_URI
        )

        private val PHONE_PROJECTION = arrayOf (
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        fun createCursor(context: Context, name: String?, pageNumber: Int?, pageSize: Int?) : Cursor? {
            val uri = ContactsContract.Data.CONTENT_URI
            val selection = getSelection(name)
            val searchList = getSearchList(name)
            val sortOrder = getSortOrder(pageNumber, pageSize)
            return context.contentResolver.query(
                uri,
                PROJECTION,
                selection,
                searchList,
                sortOrder)
        }

        fun createPhoneCursor(context: Context, id: Long): Cursor? {
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val selector = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
            return context.contentResolver.query(uri, PHONE_PROJECTION, selector, arrayOf(id.toString()), null)
        }

        private fun getSearchList(name: String?): Array<String>? {
            val searchText = getSearchText(name)
            var searchList: Array<String>? = null
            searchText?.let {
                searchList = arrayOf(searchText)
            }
            return searchList
        }

        private fun getSelection(name: String?) : String? {
            var selection: String? = null
            name?.let {
                selection = "lower(" + ContactsContract.Data.DISPLAY_NAME_PRIMARY + ") GLOB ?"
            }
            return selection
        }

        private fun getSearchText(name: String?): String? {
            var searchText: String? = null
            name?.let {
                searchText = "*" + addSpecialCharactersOptions(name.toLowerCase()) + "*"
            }
            return searchText
        }

        private fun getSortOrder(pageNumber: Int?, pageSize: Int?): String {
            var sortOrder = ContactsContract.Data.DISPLAY_NAME + " COLLATE NOCASE ASC"
            pageNumber?.let {
                pageSize?.let {
                    val offset = pageSize * pageNumber
                    sortOrder += " LIMIT $pageSize OFFSET $offset"
                }
            }
            return sortOrder
        }

        private fun addSpecialCharactersOptions(searchText: String): String {
            return searchText.toLowerCase()
                .replace("[aáàäâã]".toRegex(), "\\[aáàäâã\\]")
                .replace("[eéèëê]".toRegex(), "\\[eéèëê\\]")
                .replace("[iíìî]".toRegex(), "\\[iíìî\\]")
                .replace("[oóòöôõ]".toRegex(), "\\[oóòöôõ\\]")
                .replace("[uúùüû]".toRegex(), "\\[uúùüû\\]")
                .replace("[cç]".toRegex(), "\\[cç\\]")
                .replace("*", "[*]")
                .replace("?", "[?]")
        }
    }
}