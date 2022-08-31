package com.mredrock.cyxbs.noclass.page.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.adapter.NoClassGroupAdapter
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
    private val mTitleText : TextView by R.id.tv_noclass_detail_title.view()

    /**
     * RV
     */
    private val mRecyclerView : RecyclerView by R.id.rv_noclass_group_detail_container.view()

    /**
     * 按钮
     */
    private val mBtnSave : Button by R.id.btn_noclass_group_detail_save.view()

    /**
     * 该界面是否改变
     */
    private var mHasChanged : Boolean = false

    /**
     * 设置list
     */
    private var mGroupList : MutableList<NoclassGroup> = mutableListOf()

    /**
     * 设置position
     */
    private var mPosition : Int = -1

    /**
     * 预增加用户
     */
    private var mToAddSet : MutableSet<Int> = mutableSetOf()

    /**
     * 预删除用户
     */
    private var mToDeleteSet : MutableSet<Int> = mutableSetOf()

    /**
     * 当前选择的NoclassGroup
     */
    private lateinit var mCurrentNoclassGroup : NoclassGroup

    /**
     * 当前选择的members
     */
    private lateinit var mCurrentMembers : MutableList<NoclassGroup.Member>

    /**
     * private val
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_group_detail)
        getList()
        initRv()
        initTextView()
        initEditText()
        initBtn()
    }

    /**
     * 接受外部传来的数据
     */
    private fun getList(){
        val extra = intent.getSerializableExtra("GroupList")
        mPosition = intent.getIntExtra("GroupPosition",-1)
        mGroupList = extra as MutableList<NoclassGroup>
        mCurrentNoclassGroup = mGroupList[mPosition]
        mCurrentMembers = mCurrentNoclassGroup.members.toMutableList()
        mTitleText.text = mCurrentNoclassGroup.name
    }

    /**
     * 初始化RV
     */
    private fun initRv(){
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = NoClassGroupAdapter().apply {
            if (mPosition != -1){
                submitList(mCurrentMembers)
            }
            setOnItemDelete {
                mCurrentMembers.remove(it)
                mHasChanged = true
            }
        }
    }

    /**
     * 完成上方标题点击初始化
     */
    private fun initTextView(){
        mTitleText.setOnSingleClickListener {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            RenameGroupDialog(this)
                .setOnGroupRename {
                    for (i in mGroupList){//判断是否有重复值
                        if (it == i.name){
                            return@setOnGroupRename false
                        }
                    }
                    viewModel.updateGroup(mCurrentNoclassGroup.id,it,mCurrentNoclassGroup.isTop.toString())
                    true
                }
                .show()
        }
    }

    /**
     * 初始化保留按钮
     */
    private fun initBtn(){
        mBtnSave.setOnClickListener {
            if (mToAddSet.isEmpty() && mToDeleteSet.isEmpty()){
                return@setOnClickListener
            }
            //TODO
            //保存变化
        }
    }

    /**
     * 设置按钮状态
     */
    private fun setBtnState(unSaveState : Boolean){
        if (unSaveState){//没有保存的状态
            mBtnSave.setBackgroundResource(R.drawable.noclass_shape_button_save_default_bg)
        }else{//需要保存的状态
            mBtnSave.setBackgroundResource(R.drawable.noclass_shape_button_common_bg)
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
        if (mToAddSet.isEmpty() && mToDeleteSet.isEmpty()){
            if (mHasChanged){
                setResult(RESULT_OK, Intent().apply {
                    putExtra("GroupDetailResult",true)
                })
            }
            super.onBackPressed()
        }else{
            ConfirmReturnDialog(this).setOnReturnClick {
                this@GroupDetailActivity.onBackPressed()
            }.show()
        }
    }
}