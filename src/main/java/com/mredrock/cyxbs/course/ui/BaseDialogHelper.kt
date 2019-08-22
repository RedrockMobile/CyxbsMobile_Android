package com.mredrock.cyxbs.course.ui

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import com.mredrock.cyxbs.course.R

/**
 * Created by anriku on 2019/3/10.
 */
abstract class BaseDialogHelper(val context: Context, @LayoutRes val layoutId: Int) {

    protected val dialog: Dialog

    init {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(layoutId, null)
        dialog = Dialog(context).apply {
            setCancelable(true)
            setContentView(dialogView)
            window?.setWindowAnimations(R.style.CourseDialogFragmentAnimation)
        }
    }
}