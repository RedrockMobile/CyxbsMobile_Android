package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog

/**
 * Created by anriku on 2019/3/10.
 */
abstract class BaseDialogHelper(val context: Context, @LayoutRes val layoutId: Int) {

    protected val dialog: BottomSheetDialog

    init {
        val layoutInflater = LayoutInflater.from(context)
        val dialogView = layoutInflater.inflate(layoutId, null)
        dialog = RedRockBottomSheetDialog(context).apply {
            setContentView(dialogView)
        }
    }
}