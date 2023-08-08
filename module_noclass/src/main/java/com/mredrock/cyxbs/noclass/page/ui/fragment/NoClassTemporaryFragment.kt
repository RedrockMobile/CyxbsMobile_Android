package com.mredrock.cyxbs.noclass.page.ui.fragment


import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassSpareTime
import com.mredrock.cyxbs.noclass.page.adapter.NoClassTemporaryAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchAllDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchDoneDialog
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.TemporaryViewModel

/**
 * 临时分组的fragment
 */
class NoClassTemporaryFragment : BaseFragment(R.layout.noclass_fragment_temporary) {

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
     * 是否弹出下次不在勾选这个框框
     */
    private var mNeedShow = true

    /**
     * 临时分组界面展示人员的adapter
     */
    private lateinit var mAdapter : NoClassTemporaryAdapter

    private val mViewModel by viewModels<TemporaryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initSearchEvent()
    }


    /**
     * 执行查询课程的操作
     */
    private fun doSearchCourse() {
        mViewModel.getLessons(mAdapter.currentList.map { it.stuNum }, mAdapter.currentList)
    }

    /**
     * 初始化观察者
     */
    private fun initObserver() {
        var searchAllDialog: SearchAllDialog? = null
        mViewModel.searchAll.observe(viewLifecycleOwner) {
            if (it.info == "success") {
                val result = it.data
                if (result.isExist) {
                    searchAllDialog = SearchAllDialog(it).apply {
                        setOnClickClass {

                        }
                        setOnClickStudent {
                            //todo 添加到当前list
                        }
                        setOnClickGroup {

                        }
                    }
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
        //防止软键盘弹起导致视图错位
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        mCourseSheetBehavior = BottomSheetBehavior.from(mCourseContainer)
        mCourseSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> { //展开操作
                        mEditTextView.apply {
                            clearFocus()
                            isFocusable = false
                            isFocusableInTouchMode = false
                        }
                    }

                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> { //折叠操作
                        mEditTextView.apply {
                            isFocusable = true
                            isFocusableInTouchMode = true
                            requestFocus()
                        }
                        if (mNeedShow) {
                            if (defaultSp.getBoolean("NeverRemindNextOnNoClass", false)) {
                                return
                            }
                            SearchDoneDialog(requireContext()).apply {
                                setOnReturnClick { searchDoneDialog, remind ->
                                    if (remind) {
                                        defaultSp.edit {
                                            putBoolean("NeverRemindNextOnNoClass", remind)
                                        }
                                    }
                                    searchDoneDialog.dismiss()
                                }
                                setOnContinueClick { searchDoneDialog, remind ->
                                    if (remind) {
                                        defaultSp.edit {
                                            putBoolean("NeverRemindNextOnNoClass", remind)
                                        }
                                    }
                                    searchDoneDialog.dismiss()
                                }
                            }.show()
                        }
                        mNeedShow = false
                    }

                    else -> {
                        //忽略
                    }
                }
            }
        })

        replaceFragment(R.id.noclass_course_bottom_sheet_container) {
            NoClassCourseVpFragment.newInstance(NoClassSpareTime.EMPTY_PAGE)
        }

        mCourseSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //设置查询课表
        mBtnQuery.setOnSingleClickListener {
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
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        mViewModel.getSearchAllResult(content)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            NoClassTemporaryFragment().apply {}
    }
}