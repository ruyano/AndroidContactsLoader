package com.ruyano.contactsloader

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class ContactsLoader(var mContext: Context) {

    fun load(): Observable<List<Contact>> {
        return Observable.create { emitter -> getContacts(null, null, null, emitter) }
    }

    fun load(name: String): Observable<List<Contact>> {
        return Observable.create { emitter -> getContacts(name, null, null, emitter) }
    }

    fun load(pageNumber: Int, pageSize: Int): Observable<List<Contact>> {
        return Observable.create { emitter -> getContacts(null, pageNumber, pageSize, emitter) }
    }

    fun load(name: String, pageNumber: Int, pageSize: Int): Observable<List<Contact>> {
        return Observable.create { emitter -> getContacts(name, pageNumber, pageSize, emitter) }
    }

    private fun getContacts(name: String? = null,
                    pageNumber: Int? = null,
                    pageSize: Int? = null,
                    emitter: ObservableEmitter<List<Contact>>) {
        val contacts = getContactsWithoutPhone(name, pageNumber, pageSize)
        triggerEmitter(contacts, emitter)
    }

    private fun triggerEmitter(contacts: List<Contact>?, emitter: ObservableEmitter<List<Contact>>) {
        contacts?.let {
            emitter.onNext(contacts)
        } ?: run {
            emitter.onNext(emptyList())
        }
        emitter.onComplete()
    }

    private fun getContactsWithoutPhone(name: String? = null,
                                        pageNumber: Int? = null,
                                        pageSize: Int? = null) : List<Contact>? {
        var cursor = ContactsCursorUtil.createCursor(mContext, name, pageNumber, pageSize)
        cursor?.let {
            val list = generateSequence { if (cursor.moveToNext()) cursor else null }
                .map { cursorToObject(it) }
                .toList()
            cursor.close()
            return list
        } ?: run {
            return null
        }
    }

    private fun cursorToObject(cursor: Cursor) : Contact {
        val id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID))
        val imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI)) ?: ""
        val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY))
        val phonesList = getPhonesForContact(id)
        phonesList?.let {
            return Contact(id, imageUri, name, it)
        }
        return Contact(id, imageUri, name, emptyList())
    }


    fun getPhonesForContact(id: Long) : List<String>? {
        val phoneCursor = ContactsCursorUtil.createPhoneCursor(mContext, id)
        phoneCursor?.let {
            val list = generateSequence { if (phoneCursor.moveToNext()) phoneCursor else null }
                .map { cursorToPhoneObject(it) }
                .toList()
            phoneCursor.close()
            return list
        } ?: run {
            return null
        }
    }

    private fun cursorToPhoneObject(phoneCursor: Cursor): String {
        val phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val phone = phoneCursor.getString(phoneIndex)
        return phone
    }

}