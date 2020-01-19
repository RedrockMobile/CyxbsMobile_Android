package com.mredrock.cyxbs.mine.util.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * Create by roger
 * on 2019/11/15
 */
class MineDialogFragment(private val title: String, private val content: String, private val positiveCallback: (() -> Unit), private val cancelCallback: (() -> Unit)) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("确定") { _, _ ->
                    positiveCallback.invoke()
                }
                .setNegativeButton("取消") { _, _ ->
                    cancelCallback.invoke()
                }
        return builder.create()


    }
}