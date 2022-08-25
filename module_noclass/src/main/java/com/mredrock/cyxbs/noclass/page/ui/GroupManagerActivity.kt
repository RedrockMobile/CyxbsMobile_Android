package com.mredrock.cyxbs.noclass.page.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.adapter.GroupManagerAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.ConfirmDeleteDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.CreateGroupDialogFragment
import com.mredrock.cyxbs.noclass.page.viewmodel.GroupManagerViewModel

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      GroupManagerActivity
 * @Author:         Yan
 * @CreateDate:     2022年08月15日 03:07:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    分组管理中心Activity
 */
class GroupManagerActivity : BaseVmActivity<GroupManagerViewModel>(){

    /**
     * 分组管理界面 新建/删除 按钮
     */
    private val mBtnRight: Button by R.id.btn_noclass_group_create.view()

    /**
     * 分组管理界面 删除/取消 按钮
     */
    private val mBtnLeft : Button by R.id.btn_noclass_group_delete.view()

    /**
     * 分组管理界面的RV
     */
    private val mRecyclerView : RecyclerView by R.id.rv_noclass_group_container.view()

    /**
     * 没有数据容器
     */
    private val mEmptyContainer : LinearLayout by R.id.ll_noclass_empty_container.view()

    /**
     * rv的adapter
     */
    private lateinit var mRvAdapter : GroupManagerAdapter

    /**
     * 分组管理界面的状态
     */
    private var mGroupState : GroupState = GroupState.NORMAL

    /**
     * 选中删除的item
     */
    private var mSelectedItems : MutableSet<Int> = mutableSetOf()

    /**
     * 整个分组数据
     */
    private lateinit var mGroupList : List<NoclassGroup>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_group_manager)
        getList()
        initBtn()
        initRv()
    }

    /**
     * 接受外部传来的数据
     */
    private fun getList(){
        val extra = intent.getSerializableExtra("GroupList")
        mGroupList = extra as List<NoclassGroup>
         if (mGroupList.isEmpty()){
             mEmptyContainer.visibility = View.VISIBLE
         }
    }

    /**
     * 初始化Rv
     */
    private fun initRv(){
        mRvAdapter = GroupManagerAdapter (mGroupList){
            mSelectedItems = it
            if (it.isNotEmpty()) {
                mBtnRight.text = "删除 (${it.size})"
                mBtnRight.setBackgroundResource(R.drawable.noclass_shape_button_common_bg)
            } else {
                mBtnRight.text = "删除"
                mBtnRight.setBackgroundResource(R.drawable.noclass_shape_button_delete_default_bg)
            }
        }
        mRecyclerView.adapter = mRvAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        (mRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }

    /**
     * 初始化下方按钮
     */
    private fun initBtn(){
        //防止软键盘弹起导致视图错位
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        mBtnRight.apply {
            setOnSingleClickListener {
                onRightBtnClick(mGroupState)
            }
        }
        mBtnLeft.apply {
            setOnSingleClickListener {
                onLeftBtnClick(mGroupState)
            }
        }
    }

    /**
     * 右侧按钮点击事件
     */
    private fun onRightBtnClick(state: GroupState){
        when(state){
            GroupState.NORMAL -> {//新建
                val mBottomSheetDialog = CreateGroupDialogFragment()
                mBottomSheetDialog.show(supportFragmentManager,"createGroupDialog")
            }
            GroupState.DELETE -> {//删除
                if (mSelectedItems.isEmpty()){
                    return
                }
                val confirmDeleteDialog = ConfirmDeleteDialog(this)

                confirmDeleteDialog.setConfirmSelected {
                    toast("删除")
                    it.cancel()
                }.show()
            }
        }
    }

    /**
     * 左侧按钮点击事件
     */
    private fun onLeftBtnClick(state: GroupState){
        when(state){
            GroupState.NORMAL -> {//删除
                mGroupState = GroupState.DELETE
                changeState(mGroupState)
                mRvAdapter.onStateChange(mGroupState)
            }
            GroupState.DELETE -> {//取消
                mGroupState = GroupState.NORMAL
                changeState(mGroupState)
                mRvAdapter.onStateChange(mGroupState)
            }
        }
    }

    /**
     * 改变了分组管理的状态 [GroupState]
     */
    private fun changeState(state: GroupState){
        when(state){
            GroupState.NORMAL -> {
                mBtnRight.setBackgroundResource(R.drawable.noclass_shape_button_common_bg)
                mBtnLeft.text = "删除"
                mBtnRight.text = "新建"
            }
            GroupState.DELETE -> {
                mBtnRight.setBackgroundResource(R.drawable.noclass_shape_button_delete_default_bg)
                mBtnLeft.text = "取消"
                mBtnRight.text = "删除"
            }
        }
    }

    enum class GroupState{
        NORMAL,
        DELETE
    }


}