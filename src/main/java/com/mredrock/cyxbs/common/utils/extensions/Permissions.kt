package com.mredrock.cyxbs.common.utils.extensions

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

/**
 * 简化运行时权限的操作
 *
 * Created By jay68 on 2018/8/9.
 */

/*
eg:
doPermissionAction(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE) {
    reason = "为使用xxx需要您的xxx权限"  //optional. show a dialog to tell user why need this permission before request permission

    doAfterGranted {
        //optional. do something after permission granted
    }

    doAfterRefused {
        //optional. do something after permission refused by user
    }
}
*/

class PermissionActionBuilder {
    var doAfterGranted: () -> Unit = {}
        private set
    var doAfterRefused: (() -> Unit)? = null
        private set
    var reason: String? = null


    fun doAfterGranted(action: () -> Unit) {
        doAfterGranted = action
    }

    fun doAfterRefused(action: () -> Unit) {
        doAfterRefused = action
    }
}

private fun requestPermission(rxPermissions: RxPermissions,
                              builder: PermissionActionBuilder,
                              vararg permissionsToRequest: String) =
        rxPermissions.request(*permissionsToRequest).subscribe { granted ->
            if (granted) {
                builder.doAfterGranted()
            } else {
                builder.doAfterRefused?.invoke()
            }
        }

private fun performRequestPermission(context: Context,
                                     rxPermissions: RxPermissions,
                                     vararg permissionsRequired: String,
                                     actionBuilder: PermissionActionBuilder.() -> Unit) {
    val builder = PermissionActionBuilder().apply(actionBuilder)
    val permissionsToRequest = permissionsRequired.filterNot { rxPermissions.isGranted(it) }

    when {
        permissionsToRequest.isEmpty() -> builder.doAfterGranted.invoke()
        builder.reason != null -> context.alert(builder.reason!!) {
            yesButton {
                requestPermission(rxPermissions, builder, *permissionsToRequest.toTypedArray())
            }
        }.show()
        else -> requestPermission(rxPermissions, builder, *permissionsToRequest.toTypedArray())
    }
}

fun AppCompatActivity.doPermissionAction(vararg permissionsRequired: String,
                                         actionBuilder: PermissionActionBuilder.() -> Unit) {
    performRequestPermission(this, RxPermissions(this), *permissionsRequired, actionBuilder = actionBuilder)
}

fun androidx.fragment.app.Fragment.doPermissionAction(vararg permissionsRequired: String,
                                                      actionBuilder: PermissionActionBuilder.() -> Unit) {
    performRequestPermission(activity!!, RxPermissions(this), *permissionsRequired, actionBuilder = actionBuilder)
}

fun AppCompatActivity.isPermissionGranted(permissions: String) = RxPermissions(this).isGranted(permissions)
fun androidx.fragment.app.Fragment.isPermissionGranted(permissions: String) = RxPermissions(this).isGranted(permissions)