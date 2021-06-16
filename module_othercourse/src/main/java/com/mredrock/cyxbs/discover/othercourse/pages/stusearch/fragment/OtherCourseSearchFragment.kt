package com.mredrock.cyxbs.discover.othercourse.pages.stusearch.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.COURSE_ENTRY
import com.mredrock.cyxbs.common.config.OTHERS_STU_NUM
import com.mredrock.cyxbs.common.config.OTHERS_TEA_NAME
import com.mredrock.cyxbs.common.config.OTHERS_TEA_NUM
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.othercourse.AutoWrapAdapter
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.pages.stulist.StuListActivity
import com.mredrock.cyxbs.discover.othercourse.pages.stusearch.viewmodel.OtherCourseSearchViewModel
import com.mredrock.cyxbs.discover.othercourse.room.History
import com.mredrock.cyxbs.discover.othercourse.room.STUDENT_TYPE
import com.mredrock.cyxbs.discover.othercourse.snackbar
import kotlinx.android.synthetic.main.othercourse_discover_activity_other_course.*
import kotlinx.android.synthetic.main.othercourse_discover_activity_stu_list.*
import kotlinx.android.synthetic.main.othercourse_other_course_search_fragment.*

/**
 * Created by yyfbe, Date on 2020/8/14.
 * 抽出公共，分为两个fragment，避免fragment传参的各种问题
 */
abstract class OtherCourseSearchFragment<T : OtherCourseSearchViewModel> : BaseViewModelFragment<T>() {
    private var lastSearch: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.othercourse_other_course_search_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserve()
        setSearch()
        initHistory()
    }

    private fun initObserve() {
        viewModel.mList.observeNotNull {
            if (it.isNotEmpty()) {
                lastSearch?.let { text ->
                    val history = History(text)
                    viewModel.addHistory(history){
                        context?.startActivity<StuListActivity>(("stu_list" to it), ("history_id" to viewModel.curHistoryId))
                    }
                }
            } else {
                context?.let { it1 -> CyxbsToast.makeText(it1, "查无此人", Toast.LENGTH_SHORT).show() }
            }
        }
        viewModel.mListFromHistory.observeNotNull {
            if (it.isNotEmpty()) {
                context?.startActivity<StuListActivity>(("stu_list" to it), ("history_id" to viewModel.curHistoryId))
            } else {
                context?.let { it1 -> CyxbsToast.makeText(it1, "查无此人", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun setSearch() {
        et_discover_other_course_search.setOnEditorActionListener { v, actionId, event ->
            val text = et_discover_other_course_search.text.toString()
            if (text.isEmpty()) {
                snackbar("输入为空")
                return@setOnEditorActionListener false
            } else {
                viewModel.getPerson(text)
                lastSearch = text
                et_discover_other_course_search.setText("")
                return@setOnEditorActionListener true
            }
        }
    }

    private fun initHistory() {

        viewModel.getHistory()
        viewModel.mHistory.observe {
            aw_other_course_fragment.adapter = AutoWrapAdapter(it ?: listOf(),
                    //这里是历史记录的点击位置
                    onTextClickListener = { history ->
                        if (history.verify != "") {
                            if (history.type == STUDENT_TYPE)
                                openCourseFragment(OTHERS_STU_NUM, history.verify)
                            else
                                openCourseFragment(OTHERS_TEA_NUM, history.verify, history.info)
                        } else {
                            //viewModel更新当前id
                            viewModel.curHistoryId = history.historyId
                            viewModel.getPerson(history.info, true)
                        }
                    },
                    onDeleteClickListener = { id ->
                        viewModel.deleteHistory(id)
                    }
            )
            when {
                it.isNullOrEmpty() -> {
                    tv_other_course_history.gone()
                    aw_other_course_fragment.refreshData()
                }
                else -> {
                    tv_other_course_history.visible()
                }
            }

        }
    }

    //需要传递key（用来区分学生或者老师）以及两个verify进来，对于学生，只需要学号就好，对于老师，则需要额外传递姓名进来
    private fun openCourseFragment(key: String, verify: String, name: String = "") {
        val fragment = (ARouter.getInstance().build(COURSE_ENTRY).navigation() as Fragment).apply {
            arguments = Bundle().apply {
                putString(key, verify)
                if (key == OTHERS_TEA_NUM){
                    putString(OTHERS_TEA_NAME, name)
                }
            }
        }
        //在滑动下拉课表容器it中添加整个课表
        this.activity?.let {
            it.supportFragmentManager.beginTransaction().replace(R.id.course_bottom_sheet_search, fragment).apply {
                commit()
            }
            BottomSheetBehavior.from(it.course_bottom_sheet_search).state = BottomSheetBehavior.STATE_EXPANDED
        }

    }

}