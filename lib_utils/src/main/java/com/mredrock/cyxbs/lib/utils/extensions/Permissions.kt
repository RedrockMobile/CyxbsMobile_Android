package com.mredrock.cyxbs.lib.utils.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions3.RxPermissions

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
    
    doNeverShow {
        // 在设置成永远不显示时回调
        true // 如果返回 true，则什么都不会发送，如果返回 false，则会再次显示授权弹窗
    }
}

// if you think this may cause user feel bad, you can add a neverNotice flag like this:
doPermissionAction(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE) {
    isShowNeverNotice = true
    ...
    //others are the same as above
}

// also,you can give a choice to cancel this dialog, but this dialog also show in next time.
// you just add a flag called:isShowCancelNotice = true
// but there, show never notice and show cancel notice are opposite, and priority : never notice > cancel notice
*/

class PermissionActionBuilder {
    var doAfterGranted: (() -> Unit)? = null
        private set
    var doAfterRefused: (() -> Unit)? = null
        private set
    var doNeverShow: (() -> Boolean)? = null
        private set
    var doOnCancel: (() -> Unit)? = null
        private set
    
    /**
     * 显示在 dialog 上的文字，为 null 时不显示 dialog，直接申请权限
     */
    var reason: String? = null
    
    /**
     * 是否显示不再提示按钮，默认不显示
     */
    var isShowNeverNotice = false
    
    /**
     * 是否显示取消按钮，默认不显示。
     * 如果用户点击其他区域或者按返回键也是可以取消的
     */
    var isShowCancelNotice = false
    
    /**
     * 权限被系统同意的回调
     */
    fun doAfterGranted(action: () -> Unit) {
        doAfterGranted = action
    }
    
    /**
     * 权限被系统拒绝的回调
     */
    fun doAfterRefused(action: () -> Unit) {
        doAfterRefused = action
    }
    
    /**
     * 在被取消时回调，此时没有去申请权限，但 dialog 被取消了
     * - 点击了取消
     * - 点击了不再提示
     * - 点击了其他区域
     * - 按了返回键
     *
     * 注意：只有 [reason] 不为 null 时才会有 dialog 的显示
     */
    fun doOnCancel(action: () -> Unit) {
        doOnCancel = action
    }
    
    /**
     * 在设置了永远不显示时回调
     * @return 返回 true，则什么都不干，返回 false，则取消永远不显示，并再回调
     */
    fun doNeverShow(action: () -> Boolean) {
        doNeverShow = action
    }
}

private fun requestPermission(
    rxPermissions: RxPermissions,
    builder: PermissionActionBuilder,
    vararg permissionsToRequest: String
) =
    rxPermissions.request(*permissionsToRequest).subscribe { granted ->
        if (granted) {
            builder.doAfterGranted?.invoke()
        } else {
            builder.doAfterRefused?.invoke()
        }
    }

private fun performRequestPermission(
    context: Context,
    rxPermissions: RxPermissions,
    vararg permissionsRequired: String,
    actionBuilder: PermissionActionBuilder.() -> Unit
) {
    val builder = PermissionActionBuilder().apply(actionBuilder)
    val permissionsToRequest = permissionsRequired.filterNot { rxPermissions.isGranted(it) }
    if (permissionsToRequest.isEmpty()) {
        builder.doAfterGranted?.invoke()
        return
    }
    var isNeverShow = context.getSp(permissionsRequired.toString())
        .getBoolean("isNeverShow", false)
    if (isNeverShow) {
        isNeverShow = builder.doNeverShow?.invoke() ?: true
        if (isNeverShow) {
            return // 仍然是 NeverShow 时直接结束
        }
        context.getSp(permissionsRequired.toString()).edit {
            putBoolean("isNeverShow", false)
        }
    }
    if (builder.reason != null) {
        AlertDialog.Builder(context).apply {
            setMessage(builder.reason)
            setPositiveButton(android.R.string.ok) { _, _ ->
                requestPermission(rxPermissions, builder, *permissionsToRequest.toTypedArray())
            }
            if (builder.isShowNeverNotice) {
                setNegativeButton("不再提示") { _, _ ->
                    context.getSp(permissionsRequired.toString()).edit {
                        putBoolean("isNeverShow", true)
                    }
                    builder.doOnCancel?.invoke()
                }
            } else if (builder.isShowCancelNotice) {
                setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    builder.doOnCancel?.invoke()
                }
            }
            setOnCancelListener {
                builder.doOnCancel?.invoke()
            }
        }.show()
    } else {
        requestPermission(rxPermissions, builder, *permissionsToRequest.toTypedArray())
    }
}

fun FragmentActivity.doPermissionAction(
    vararg permissionsRequired: String,
    actionBuilder: PermissionActionBuilder.() -> Unit
) {
    performRequestPermission(
        this,
        RxPermissions(this),
        *permissionsRequired,
        actionBuilder = actionBuilder
    )
}

fun Fragment.doPermissionAction(
    vararg permissionsRequired: String,
    actionBuilder: PermissionActionBuilder.() -> Unit
) {
    performRequestPermission(
        activity!!,
        RxPermissions(this),
        *permissionsRequired,
        actionBuilder = actionBuilder
    )
}