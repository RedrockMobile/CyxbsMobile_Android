package com.mredrock.cyxbs.mine.page.edit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.mredrock.cyxbs.common.utils.LogUtils

/**
 * Create by roger
 * on 2019/11/15
 */
class SaveInfoDialogFragment(private val positiveCallback: (() -> Unit), private val cancelCallback: (() -> Unit)) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
                .setTitle("退出编辑")
                .setMessage("你的资料还没有保存，是否退出？")
                .setPositiveButton("确定") { _, _ ->
                    positiveCallback.invoke()
                }
                .setNegativeButton("取消") { _, _ ->
                    cancelCallback.invoke()
                }
        return builder.create()


    }
}