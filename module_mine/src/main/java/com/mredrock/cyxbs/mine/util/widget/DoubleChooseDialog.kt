package com.mredrock.cyxbs.mine.util.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.mredrock.cyxbs.mine.R

class DoubleChooseDialog (context: Context?, theme : Int) : Dialog(context!! , theme) {
    companion object {
        private var doubleChooseDialog: DoubleChooseDialog? = null
        fun createDialog(context: Context?): DoubleChooseDialog? {
            doubleChooseDialog = DoubleChooseDialog(context, R.style.transparent_dialog)
            doubleChooseDialog?.window?.attributes?.gravity = Gravity.CENTER
            return doubleChooseDialog
        }
    }
}