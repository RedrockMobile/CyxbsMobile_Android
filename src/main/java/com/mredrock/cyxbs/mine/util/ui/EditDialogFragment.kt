package com.mredrock.cyxbs.mine.util.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_dialog_edit_exit.*

/**
 * Created by roger on 2020/2/2
 */
class EditDialogFragment() : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.mine_dialog_edit_exit, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mine_dialog_btn_exit.setOnClickListener {
            activity?.finish()
        }
    }
}