package com.mredrock.cyxbs.noclass.page.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.adapter.NoClassSolidAdapter
import com.mredrock.cyxbs.noclass.page.adapter.SearchStudentAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchAllDialog
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
                    //todo 需要删除，更新云端状态
                }
                setOnClickGroupIsTop {
                    //todo 置顶之后需要更新云端状态
                }
            }
        }
    }

    private fun initObserver() {
        var searchStudentDialog: SearchStudentDialog?
        mViewModel.searchStudent.observe(viewLifecycleOwner) {
            searchStudentDialog = SearchStudentDialog(it) {
                // 仅需要添加到当前，dialog删除item的逻辑在dialog里面

            }
            searchStudentDialog!!.show(childFragmentManager, "SearchStudentDialog")
        }
        mViewModel.groupList.observe(viewLifecycleOwner){
            mAdapter.submitListToOrder(it)
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