package com.mredrock.cyxbs.noclass.page.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.adapter.NoClassGroupAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.ConfirmReturnDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.RenameGroupDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchStudentDialog
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
class GroupDetailActivity : BaseActivity(){
    
    private val mViewModel by viewModels<GroupDetailViewModel>()

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
     * 该界面是否改变（是否发生网络请求为标准）
     */
    private var mHasChanged : Boolean = false
    
    /**
     * 需不需要保存(按钮颜色为标准)
     */
    private var mNeedSave : Boolean = false

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
    private var mToAddSet : MutableSet<NoclassGroup.Member> = mutableSetOf()

    /**
     * 预删除用户
     */
    private var mToDeleteSet : MutableSet<NoclassGroup.Member> = mutableSetOf()

    /**
     * 当前选择的NoclassGroup
     */
    private lateinit var mCurrentNoclassGroup : NoclassGroup

    /**
     * 当前选择的members
     */
    private lateinit var mCurrentMembers : MutableList<NoclassGroup.Member>
    
    /**
     * Adapter
     */
    private lateinit var mGroupAdapter : NoClassGroupAdapter

    /**
     * 预更改的标题
     */
    private var mTobeTitle = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_group_detail)
        getList()
        initObserve()
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
     * 初始化观测livedata
     */
    private fun initObserve(){
        mViewModel.isUpdateSuccess.observe(this){
            if (it){
                mHasChanged = true
                if (mTobeTitle != ""){
                    mTitleText.text = mTobeTitle
                    mTobeTitle = ""
                }
            }else{
                toast("网络请求错误,请稍后重试")
            }
        }
        
        var mSearchStudentDialog : SearchStudentDialog? = null
        mViewModel.students.observe(this){   students ->
            if(students.isEmpty()){
                toast("查无此人")
            }else{
                mSearchStudentDialog = SearchStudentDialog(students){
                        if (mCurrentNoclassGroup.id =="-1"){
                            return@SearchStudentDialog
                        }
                        if (mCurrentMembers.contains(it)){
                            toast("用户已经存在分组中了哦~")
                            return@SearchStudentDialog
                        }
                        mToAddSet.add(it)
                        mCurrentMembers.add(it)
                        mGroupAdapter.submitList(mCurrentMembers.toMutableList())
                        toast("添加成功")
                        setBtnState(true)
                        mSearchStudentDialog?.dismiss()
                    }.apply {
                        show(supportFragmentManager,"searchStudentDialog")
                    }
            }
        }
        mViewModel.saveState.observe(this){
            if (it){
                mToAddSet.clear()
                mToDeleteSet.clear()
                mHasChanged = true
                setBtnState(false)
                toast("保存成功")
            }else{
                toast("保存失败")
            }
        }
    }
    
    /**
     * 初始化RV
     */
    private fun initRv(){
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mGroupAdapter = NoClassGroupAdapter().apply {
            if (mPosition != -1){
                submitList(mCurrentMembers.toMutableList())
            }
            setOnItemDelete {
                mCurrentMembers.remove(it)
                mToDeleteSet.add(it)
                submitList(mCurrentMembers.toMutableList())
                setBtnState(true)
            }
        }
        mRecyclerView.adapter = mGroupAdapter
    }

    /**
     * 完成上方标题点击初始化
     */
    private fun initTextView(){
        findViewById<ImageView>(R.id.iv_noclass_group_detail_return).apply {
            setOnClickListener {
                onBackPressed()
            }
        }
        
        mTitleText.setOnSingleClickListener {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            RenameGroupDialog(this)
                .setOnGroupRename {
                    for (i in mGroupList){//判断是否有重复值
                        if (it == i.name){
                            return@setOnGroupRename false
                        }
                    }
                    mTobeTitle = it
                    mViewModel.updateGroup(mCurrentNoclassGroup.id, it, mCurrentNoclassGroup.isTop.toString())
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
            mViewModel.addAndDeleteStu(mCurrentNoclassGroup.id, mToAddSet, mToDeleteSet)
        }
    }

    /**
     * 设置按钮状态
     */
    private fun setBtnState(unSaveState : Boolean){
        if (unSaveState){//需要保存状态
            mBtnSave.isClickable = true
            mBtnSave.setBackgroundResource(R.drawable.noclass_shape_button_common_bg)
            mNeedSave = true
        }else{//已经保存的状态
            mBtnSave.isClickable = false
            mBtnSave.setBackgroundResource(R.drawable.noclass_shape_button_save_default_bg)
            mNeedSave = false
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
                doSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
    
    /**
     * 执行搜索操作
     */
    private fun doSearch(){
        val name = mEditTextView.text.toString().trim()
        if (TextUtils.isEmpty(name)) {
            toast("输入为空")
            return
        }
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        mEditTextView.setText("")
        mViewModel.searchStudent(stu = name)
    }

    override fun onBackPressed() {
        if (mToAddSet.isEmpty() && mToDeleteSet.isEmpty() && !mNeedSave){
            if (mHasChanged){
                setResult(RESULT_OK, Intent().apply {
                    putExtra("GroupDetailResult",true)
                })
            }
            super.onBackPressed()
        }else{
            ConfirmReturnDialog(this).setOnReturnClick {
                mToAddSet.clear()
                mToDeleteSet.clear()
                mNeedSave = false
                onBackPressed()
            }.show()
        }
    }
}