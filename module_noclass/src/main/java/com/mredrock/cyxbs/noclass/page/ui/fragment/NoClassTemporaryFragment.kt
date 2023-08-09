package com.mredrock.cyxbs.noclass.page.ui.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.adapter.NoClassTemporaryAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchAllDialog
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.TemporaryViewModel

/**
 * 临时分组的fragment
 */
class NoClassTemporaryFragment : BaseFragment(R.layout.noclass_fragment_temporary) {

    /**
     * 用户名称
     */
    private lateinit var mUserName : String

    /**
     * 用户id
     */
    private lateinit var mUserId : String

    /**
     * 查询按钮
     */
    private val mBtnQuery: Button by R.id.noclass_temporary_btn_query.view()

    /**
     * 底部查询fragment的container
     */
    private val mCourseContainer: FrameLayout by R.id.noclass_temporary_bottom_sheet_container.view()

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView: EditText by R.id.noclass_temporary_et_add_classmate.view()

    /**
     * bottom dialog 查询结果
     */
    private lateinit var mCourseSheetBehavior: BottomSheetBehavior<FrameLayout>

    /**
     * 临时分组界面展示人员的Rv和adapter
     */
    private val mRecyclerView : RecyclerView by R.id.noclass_temporary_rv_show.view()
    private val mAdapter by lazy { NoClassTemporaryAdapter() }

    private val mViewModel by viewModels<TemporaryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInfo()
        initRv()
        initObserver()
        initSearchEvent()
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
            }
        }
    }

    /**
     * 初始化用户信息
     */
    private fun initUserInfo(){
        ServiceManager.invoke(IAccountService::class).getUserService().apply {
            mUserName = this.getRealName()
            mUserId = this.getStuNum()
        }
    }


    /**
     * 执行查询课程的操作
     */
    private fun doSearchCourse() {
        mViewModel.getLessons(mAdapter.currentList.map { it.id }, mAdapter.currentList)
    }

    /**
     * 初始化观察者
     */
    private fun initObserver() {
        var searchAllDialog: SearchAllDialog?
        mViewModel.searchAll.observe(viewLifecycleOwner) {
            if (it.info == "success") {
                val result = it.data
                if (result.isExist) {
                    searchAllDialog = SearchAllDialog(it).apply {
                        setOnClickClass {cls ->
                            val clsList = mAdapter.currentList.toMutableSet()
                            clsList.addAll(cls.members)
                            mAdapter.submitList(clsList.toList())
                        }
                        setOnClickStudent {stu ->
                            val stuList = mAdapter.currentList.toMutableSet()
                            stuList.add(stu)
                            mAdapter.submitList(stuList.toList())
                        }
                        setOnClickGroup {group ->
                            val groupList = mAdapter.currentList.toMutableSet()
                            groupList.addAll(group.members)
                            mAdapter.submitList(groupList.toList())
                        }
                    }
                    searchAllDialog!!.show(childFragmentManager,"SearchAllDialog")
                } else {
                    toast("查无此人")
                }
            } else {
                toast("网络错误")
            }
        }
    }

    /**
     * 搜索操作的初始化
     */
    private fun initSearchEvent() {
        mCourseSheetBehavior = BottomSheetBehavior.from(mCourseContainer)
        mCourseSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> { //展开操作
                    }

                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> { //折叠操作
                    }

                    else -> {}
                }
            }
        })

        replaceFragment(R.id.noclass_temporary_bottom_sheet_container) {
            NoClassCourseVpFragment.newInstance(NoClassSpareTime.EMPTY_PAGE)
        }

        mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //设置查询课表
        mBtnQuery.setOnSingleClickListener(interval = 1000) {
            doSearchCourse()
        }
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
        fun newInstance() =
            NoClassTemporaryFragment().apply {}
    }
}