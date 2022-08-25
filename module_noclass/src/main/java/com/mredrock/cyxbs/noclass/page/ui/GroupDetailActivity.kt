package com.mredrock.cyxbs.noclass.page.ui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmActivity
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.ui.dialog.ConfirmReturnDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.RenameGroupDialog
import com.mredrock.cyxbs.noclass.page.viewmodel.GroupDetailViewModel

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      GroupDetailActivity
 * @Author:         Yan
 * @CreateDate:     2022年08月25日 04:34:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    具体分组页面
 */
class GroupDetailActivity : BaseVmActivity<GroupDetailViewModel>(){

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView : EditText by R.id.et_noclass_group_add_classmate.view()

    /**
     * 标题文字
     */
    private val mTitleText : TextView by R.id.tv_noclass_rename_title.view()

    /**
     * 是否有未保存信息
     */
    private var mNotSaveInfo : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_group_detail)
        initTextView()
        initEditText()
    }

    /**
     * 完成上方标题点击初始化
     */
    private fun initTextView(){
        mTitleText.setOnClickListener {
            RenameGroupDialog(this).show()
        }
    }

    /**
     * 上方编辑框的初始化
     */
    private fun initEditText(){
        //防止软键盘弹起导致视图错位
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        //设置键盘上点击搜索的监听
        mEditTextView.setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = mEditTextView.text.toString().trim()
                if (TextUtils.isEmpty(key)) {
                    toast("输入为空")
                    return@setOnEditorActionListener true
                }
//                TODO  完成搜索操作
//                doSearch(key)
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                mEditTextView.setText("")
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }


    override fun onBackPressed() {
        if (mNotSaveInfo){
            ConfirmReturnDialog(this).setOnReturnClick {
                mNotSaveInfo = false
                this@GroupDetailActivity.onBackPressed()
            }.show()
        }else{
            super.onBackPressed()
        }
    }
}