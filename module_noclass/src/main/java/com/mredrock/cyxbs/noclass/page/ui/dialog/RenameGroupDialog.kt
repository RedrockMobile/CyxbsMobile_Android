package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
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

    private var mOnGroupRename : ((String) -> Boolean)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_dialog_group_rename)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //软键盘设置，解决自定义dialog中不弹软键盘问题
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        setCanceledOnTouchOutside(false)
        initView()
    }

    private fun initView(){
        //右上角×
        findViewById<ImageView>(R.id.iv_noclass_group_rename_dismiss).apply {
            setOnClickListener {
                dismiss()
            }
        }
        //提示tv
        val mTvHint : TextView = findViewById(R.id.tv_noclass_group_rename_hint)
        //重命名EditText
        val mEtName : EditText = findViewById<EditText?>(R.id.et_noclass_create_group).apply {
            addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    if (s?.length == 0) {
                        mTvHint.text = "请输入你的分组名称"
                        mTvHint.visibility = View.VISIBLE
                    } else if (s?.length == 12) {
                        mTvHint.postDelayed(3000) {
                            mTvHint.visibility = View.INVISIBLE
                        }
                        mTvHint.text = "分组名称不能超过12个字符"
                        mTvHint.visibility = View.VISIBLE
                    } else {
                        mTvHint.visibility = View.INVISIBLE
                    }
                }
            )
        }
        //确定按钮
        val mBtnDone : Button = findViewById<Button?>(R.id.btn_noclass_dialog_rename_confirm).apply {
            setOnClickListener {
                if (mOnGroupRename?.invoke(mEtName.text.toString()) == true){
                    mTvHint.visibility = View.INVISIBLE
                    cancel()
                }else{
                    mTvHint.text = "和已有分组重名，再想想吧"
                    mTvHint.visibility = View.VISIBLE
                }
            }
        }
    }

    fun setOnGroupRename(listener : (String) -> Boolean) : RenameGroupDialog{
        mOnGroupRename = listener
        return this
    }

}