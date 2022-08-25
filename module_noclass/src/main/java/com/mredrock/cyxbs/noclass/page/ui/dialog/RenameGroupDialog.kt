package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import com.mredrock.cyxbs.noclass.R

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      RenameGroupDialog
 * @Author:         Yan
 * @CreateDate:     2022年08月25日 05:41:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    重命名的dialog
 */
class RenameGroupDialog (context: Context) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_group_rename)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initView()
    }

    private fun initView(){

        findViewById<ImageView>(R.id.iv_noclass_group_rename_dismiss).apply {
            setOnClickListener {
                dismiss()
            }
        }

    }

}