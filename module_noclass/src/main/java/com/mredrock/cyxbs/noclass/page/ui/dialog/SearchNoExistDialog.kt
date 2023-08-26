package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import com.mredrock.cyxbs.noclass.R

class SearchNoExistDialog (context: Context) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_search_not_exist)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initView()
    }

    private fun initView() {
        findViewById<Button>(R.id.noclass_dialog_search_not_exist_confirm).setOnClickListener {
            dismiss()
        }
    }

}