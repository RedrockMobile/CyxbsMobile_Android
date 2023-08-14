package com.mredrock.cyxbs.noclass.page.ui.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.adapter.NoClassTemporaryAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchAllDialog
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.CourseViewModel
import com.mredrock.cyxbs.noclass.page.viewmodel.activity.NoClassViewModel
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.TemporaryViewModel

/**
 * 临时分组的fragment
 */
class NoClassTemporaryFragment : BaseFragment(R.layout.noclass_fragment_temporary) {

    /**
     * 用户名称
     */
    private lateinit var mUserName: String

    /**
     * 用户id
     */
    private lateinit var mUserId: String

    /**
     * 查询按钮
     */
    private val mBtnQuery: Button by R.id.noclass_temporary_btn_query.view()

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView: EditText by R.id.noclass_temporary_et_add_classmate.view()

    /**
     * 临时分组界面展示人员的Rv和adapter
     */
    private val mRecyclerView: RecyclerView by R.id.noclass_temporary_rv_show.view()
    private val mAdapter by lazy { NoClassTemporaryAdapter() }

    private val mViewModel by viewModels<TemporaryViewModel>()
    private val mCourseViewModel by activityViewModels<CourseViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInfo()
        initRv()
        initObserver()
        initSearchEvent()
        initFindCourse()
    }

    private fun initFindCourse() {
        //设置查询课表
        mBtnQuery.setOnSingleClickListener(interval = 1000) {
            doSearchCourse()
        }
    }

    /**
     * 初始化临时分组页面的rv
     */
    private fun initRv() {
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter.apply {
                //加入本人
//                val mRvList = ArrayList<Student>()
//                mRvList.add(Student("","","","",mUserId,mUserName))
//                submitList(mRvList)
                setOnItemDelete {
                    deleteMember(it)
                }
            }
        }
    }

    /**
     * 初始化用户信息
     */
    private fun initUserInfo() {
        ServiceManager.invoke(IAccountService::class).getUserService().apply {
            mUserName = this.getRealName()
            mUserId = this.getStuNum()
        }
    }


    /**
     * 执行查询空闲课程的操作
     */
    private fun doSearchCourse() {
        mCourseViewModel.getLessons(mAdapter.currentList.map { it.id }, mAdapter.currentList)
    }

    /**
     * 初始化观察者
     */
    private fun initObserver() {
        var searchAllDialog: SearchAllDialog?
        mViewModel.searchAll.observe(viewLifecycleOwner) {
            val result = it
            if (result.isExist) {
                searchAllDialog = SearchAllDialog(it).apply {
                    setOnClickClass { cls ->
                        val clsList = mAdapter.currentList.toMutableSet()
                        clsList.addAll(cls.members)
                        mAdapter.submitList(clsList.toList())
                    }
                    setOnClickStudent { stu ->
                        val stuList = mAdapter.currentList.toMutableSet()
                        stuList.add(stu)
                        mAdapter.submitList(stuList.toList())
                    }
                    setOnClickGroup { group ->
                        val groupList = mAdapter.currentList.toMutableSet()
                        groupList.addAll(group.members)
                        mAdapter.submitList(groupList.toList())
                    }
                }
                searchAllDialog!!.show(childFragmentManager, "SearchAllDialog")
            } else {
                toast("查无此人")
            }
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

    /**
     * 执行查找学生操作
     */
    private fun doSearchStu() {
        val content = mEditTextView.text.toString().trim()
        if (TextUtils.isEmpty(content)) {
            toast("输入为空")
            return
        }
        mViewModel.getSearchAllResult(content)
    }


    companion object {
        @JvmStatic
        fun newInstance() = NoClassTemporaryFragment().apply {}
    }
}