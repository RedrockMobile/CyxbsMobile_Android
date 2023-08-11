package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup
import com.mredrock.cyxbs.noclass.page.adapter.NoClassSolidAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.CreateGroupDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchStudentDialog
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.SolidViewModel

/**
 * 固定分组的fragment
 */
class NoClassSolidFragment : BaseFragment(R.layout.noclass_fragment_solid) {

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView: EditText by R.id.noclass_solid_et_add_classmate.view()

    /**
     * 界面的rv
     */
    private val mRecyclerView : RecyclerView by R.id.noclass_solid_rv_show.view()

    /**
     * 界面rv的adapter
     */
    private val mAdapter : NoClassSolidAdapter by lazy { NoClassSolidAdapter() }

    /**
     * 创建按钮
     */
    private val mBtnCreate : Button by R.id.noclass_solid_btn_create.view()

    /**
     * 删除缓冲区：可能想要删除，但是云端没删除成功，所以本地也不能删掉。必须云端删除成功本地才能删除成功
     */
    private var waitDeleteGroup : NoclassGroup? = null

    /**
     * 更新缓冲区
     */
    private var waitUpDate : NoclassGroup? = null

    private val mViewModel by viewModels<SolidViewModel>()

    /**
     * 进入组内管理界面，判断是否有更改值
     */
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val extra = intent?.getSerializableExtra("GroupListResult")
                if (extra != null) {
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        getAllGroup()
        initSearchEvent()
        initClickCreate()
    }

    /**
     * 初始化创建的点击事件
     */
    private fun initClickCreate() {
        mBtnCreate.setOnClickListener {
            //弹出创建分组的弹窗
            val existNames = mAdapter.currentList.map { it.name }
            CreateGroupDialog(existNames){
                //这里是创建分组之后的事
                val orderList = mAdapter.currentList.toMutableList()
                orderList.add(it)
                mAdapter.submitListToOrder(orderList)
            }.show(childFragmentManager,"SolidCreateGroupDialog")
        }
    }

    private fun getAllGroup() {
        mViewModel.getAllGroup()
    }

    private fun initView() {
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter.apply {
                setOnClickGroupName {
                    //todo 进入组内，需要跳转，通过registerforresult来跳
                }
                setOnClickGroupDelete {
                    //删除：加入缓冲区，并且更新云端状态
                    waitDeleteGroup = it
                    mViewModel.deleteGroup(it.id)
                }
                setOnClickGroupIsTop {
                    //置顶：加入缓冲区，更新云端置顶状态
                    mViewModel.updateGroup(it.id,it.name,it.isTop.toString())
                }
            }
        }
    }

    private fun initObserver() {
        var searchStudentDialog: SearchStudentDialog?
        mViewModel.searchStudent.observe(viewLifecycleOwner) {
            searchStudentDialog = SearchStudentDialog(it) {
                //点击加号之后的逻辑，需要弹窗选择分组加入
                
            }
            searchStudentDialog!!.show(childFragmentManager, "SearchStudentDialog")
        }
        mViewModel.groupList.observe(viewLifecycleOwner){
            mAdapter.submitListToOrder(it)
        }
        mViewModel.isDeleteSuccess.observe(viewLifecycleOwner){
            //todo 删除成功或者失败之后的操作
        }
        mViewModel.isCreateSuccess.observe(viewLifecycleOwner){
            if(it.id ==  -1){
                toast("创建失败")
            }
            //todo 创建成功或者失败之后的操作
        }
        mViewModel.isUpdateSuccess.observe(viewLifecycleOwner){
            //todo 更新成功或者失败之后的操作
        }
    }

    /**
     * 搜索操作的初始化
     */
    private fun initSearchEvent() {
        //防止软键盘弹起导致视图错位
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        //设置键盘上点击搜索的监听
        mEditTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearchStu()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    //开始搜索
    private fun doSearchStu() {
        val content = mEditTextView.text.toString().trim()
        if (TextUtils.isEmpty(content)) {
            toast("输入为空")
            return
        }
        mEditTextView.setText("")
        mViewModel.getSearchResult(content)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NoClassSolidFragment().apply {}
    }
}