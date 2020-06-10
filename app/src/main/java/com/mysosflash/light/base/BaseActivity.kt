package com.mysosflash.light.base

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mysosflash.light.helpers.hasPermission

abstract class BaseActivity : AppCompatActivity() {
    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    var isAskingPermissions = false

    private val GENERIC_PERM_HANDLER = 100


    override fun onStop() {
        super.onStop()
        actionOnPermission = null
    }

    fun handlePermission(permissionString: String, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (hasPermission(permissionString)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(this, arrayOf(permissionString), GENERIC_PERM_HANDLER)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAskingPermissions = false
        if (requestCode == GENERIC_PERM_HANDLER && grantResults.isNotEmpty()) {
            actionOnPermission?.invoke(grantResults[0] == 0)
        }
    }
}