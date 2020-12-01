package com.mredrock.cyxbs.mine.util.ui

import android.app.Dialog
import android.content.Context
import com.mredrock.cyxbs.mine.R

/**
 * Author: RayleighZ
 * Time: 2020-12-01 9:34
 */
class OnExitDialog(context: Context?) : Dialog(context, R.style.transparent_dialog){
    companion object{
        var dialog: Dialog? = null
        fun showDialog(context: Context?){
            if (context == null) return
            if (dialog == null || dialog?.context != context ){
                dialog = OnExitDialog(context)
            }
        }
    }
}