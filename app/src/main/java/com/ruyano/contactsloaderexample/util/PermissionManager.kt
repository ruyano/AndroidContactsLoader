package com.ruyano.contactsloaderexample.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(val mActivity: AppCompatActivity, val permissionManagerListener: PermissionManagerListener) {

    private val READ_CONTACTS_PERMISSION_REQUEST_CODE = 999

    fun checkReadContactsPermission() =
        ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    fun requestReadContactsPermission() {
        ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.READ_CONTACTS), READ_CONTACTS_PERMISSION_REQUEST_CODE)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_CONTACTS_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission granted
                    permissionManagerListener.onPermissionGranted()
                } else {
                    // permission rejected
                    permissionManagerListener.onPermissionRejected()
                }
                return
            }
            else -> {
                return
            }
        }
    }

    interface PermissionManagerListener {
        fun onPermissionGranted()
        fun onPermissionRejected()
    }

}