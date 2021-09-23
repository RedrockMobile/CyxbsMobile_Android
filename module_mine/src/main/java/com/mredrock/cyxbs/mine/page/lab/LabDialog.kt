package com.mredrock.cyxbs.mine.page.lab

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_lab_dialog.view.*


/**
 *@author 林潼
 *@date 2021/9/16
 *@description 进入实验室时的弹窗
 */


object LabDialog {
    fun show(context: Context) {
        if (!context.sharedPreferences("lab").getBoolean("first_use",true)) {
            return
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.transparent_dialog)
        val view = LayoutInflater.from(context).inflate(R.layout.mine_lab_dialog, null, false)
        builder.setView(view)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.show()
        view.mine_lab_tv_positive.setOnClickListener {
            dialog.dismiss()
            context.sharedPreferences("lab").editor {
                putBoolean("first_use",false)
            }
        }
    }
}