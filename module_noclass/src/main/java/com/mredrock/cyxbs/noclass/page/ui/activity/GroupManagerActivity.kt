package com.mredrock.cyxbs.noclass.page.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.adapter.GroupManagerAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.ConfirmDeleteDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.CreateGroupDialogFragment
import com.mredrock.cyxbs.noclass.page.viewmodel.GroupManagerViewModel
import java.io.Serializable

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
class GroupManagerActivity : BaseActivity() {
    
    private val mViewModel by viewModels<GroupManagerViewModel>()

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
    private lateinit var mGroupList : MutableList<NoclassGroup>
    
    /**
     * 这个界面是否有改变
     */
    private var mHasChanged : Boolean = false

    /**
     * 是否有更改值
     */
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val extra = intent?.getBooleanExtra("GroupDetailResult",false)
            if (extra == true){
                mHasChanged = true
                reGetGroup()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_group_manager)
        getList()
        initBtn()
        initRv()
        initObserve()
    }

    /**
     * 接受外部传来的数据
     */
    private fun getList(){
        var mDefaultList : MutableList<NoclassGroup.Member>? = null
        val create = intent.getBooleanExtra("CreateGroup",false)
        if (create) mDefaultList = intent.getSerializableExtra("DefaultList") as MutableList<NoclassGroup.Member>
        val extra = intent.getSerializableExtra("GroupList")
        mGroupList = extra as MutableList<NoclassGroup>
         if (mGroupList.isEmpty()){
             mEmptyContainer.visibility = View.VISIBLE
         }
        if (create){
            createGroup(mDefaultList)
        }
    }

    /**
     * 初始化Rv
     */
    private fun initRv(){
        mRvAdapter = GroupManagerAdapter {
            mSelectedItems = it
            if (it.isNotEmpty()) {
                mBtnRight.text = "删除 (${it.size})"
                mBtnRight.setBackgroundResource(R.drawable.noclass_shape_button_common_bg)
            } else {
                mBtnRight.text = "删除"
                mBtnRight.setBackgroundResource(R.drawable.noclass_shape_button_delete_default_bg)
            }
         }.apply {
            submitList(mGroupList)
            setOnGroupDetailStart {
                startForResult.launch(Intent(this@GroupManagerActivity, GroupDetailActivity::class.java).apply {
                    putExtra("GroupList",mGroupList as Serializable)
                    putExtra("GroupPosition",it)
                })
            }
            setOnTopClick {
                val selectedGroup = mGroupList[it]
                mViewModel.updateGroup(selectedGroup.id, selectedGroup.name, (!selectedGroup.isTop).toString())
            }
            setOnDeleteClick { index ->
                val selectedGroup = mGroupList[index]
                val confirmDeleteDialog = ConfirmDeleteDialog(this@GroupManagerActivity)
                confirmDeleteDialog.setConfirmSelected {
                    mViewModel.deleteGroup(selectedGroup.id)
                    it.cancel()
                }.show()
            }
         }
        mRecyclerView.adapter = mRvAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.itemAnimator = null
    }

    /**
     * 网络请求获得分组
     * 因为更新分组后的分组顺序跟创建时间相关
     * 本地并没有保存下来
     * 所有更新分组后需要重新请求获得分组
     */
    private fun reGetGroup(){
        mViewModel.getNoclassGroupDetail()
    }

    /**
     * 对viewmodel的livedata的监听
     */
    private fun initObserve(){
        //是否更新成功
        mViewModel.isUpdateSuccess.observe(this){
            if (it.second){
                for (group in mGroupList){
                    if (group.id == it.first){
                        //完成更新
                        mHasChanged = true
                        reGetGroup()
                    }
                }
            }else{
                toast("似乎出现了什么问题呢，请稍后再试")
            }
        }

        //是否删除成功
        mViewModel.isDeleteSuccess.observe(this){
            if (it.second){
                val groupIds = it.first
                val ids = groupIds.split(",")
                for (idInGroup in ids){//每个id
                    for (group in mGroupList){//删除组中该id
                        if (group.id == idInGroup){
                            //完成更新
                            mHasChanged = true
                            reGetGroup()
                            if (mGroupState === GroupState.DELETE){
                                mGroupState = GroupState.NORMAL
                                changeState(mGroupState)
                                mRvAdapter.onStateChange(mGroupState)
                            }
                            toast("删除成功")
                            return@observe
                        }
                    }
                }
            }else{
                toast("似乎出现了什么问题呢，请稍后再试")
            }
        }

        var mLastCreateId : Int = -1
        
        //是否创建成功
        mViewModel.isCreateSuccess.observe(this){
            if (it.id != -1){
                mHasChanged = true
                mLastCreateId = it.id
                reGetGroup()
            }
        }

        //重新获得分组
        mViewModel.groupList.observe(this){
            mEmptyContainer.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            mGroupList = it as MutableList<NoclassGroup>
            mRvAdapter.submitList(mGroupList)
            if (mLastCreateId != -1){
                var index = -1
                for(i in 0 until mGroupList.size){
                    if (mLastCreateId.toString() == mGroupList[i].id){
                        index = i
                        break
                    }
                }
                startForResult.launch(Intent(this@GroupManagerActivity, GroupDetailActivity::class.java).apply {
                    putExtra("GroupList",mGroupList as Serializable)
                    putExtra("GroupPosition",index)
                })
                mLastCreateId = -1
            }
        }

    }

    /**
     * 初始化下方按钮
     */
    private fun initBtn(){
        findViewById<ImageView>(R.id.iv_noclass_group_manager_return).apply {
            setOnClickListener {
                onBackPressed()
            }
        }
        
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
     * 新建分组
     */
    private fun createGroup(extraList : MutableList<NoclassGroup.Member>? = null){
        val mNameList : MutableList<String> = mutableListOf() //记录所有分组名字
        for (i in mGroupList){
            mNameList.add(i.name)
        }
        val mBottomSheetDialog = CreateGroupDialogFragment(mNameList).apply {
            if (extraList?.isNotEmpty() == true){
                var extraNums = ""
                extraList.forEachIndexed { index, member ->
                    extraNums += member.stuNum
                    if (index != extraList.size -1){
                        extraNums += ","
                    }
                }
                setExtraNumbers(extraNums)
            }
        }
        mBottomSheetDialog.show(supportFragmentManager,"createGroupDialog")
    }

    /**
     * 右侧按钮点击事件
     */
    private fun onRightBtnClick(state: GroupState){
        when(state){
            GroupState.NORMAL -> {//新建
                createGroup()
            }
            GroupState.DELETE -> {//删除
                if (mSelectedItems.isEmpty()){
                    return
                }
                val confirmDeleteDialog = ConfirmDeleteDialog(this)

                confirmDeleteDialog.setConfirmSelected {
                    var ids = ""
                    for ((cur, position) in mSelectedItems.withIndex()){
                        ids += mGroupList[position].id
                        if (cur != mSelectedItems.size - 1){
                            ids += ","
                        }
                    }
                    mViewModel.deleteGroup(groupIds=ids)
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
        //todo 这里不再需要额外的按钮，只需要一个按钮
    }

    /**
     * 改变了分组管理的状态 [GroupState]
     */
    private fun changeState(state: GroupState){
        when(state){
            GroupState.NORMAL -> {
                mSelectedItems.clear()
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

    override fun onBackPressed() {
        if (mHasChanged){
            setResult(RESULT_OK,Intent().apply {
                putExtra("GroupListResult",mGroupList as Serializable)
            })
        }
        super.onBackPressed()
    }

}