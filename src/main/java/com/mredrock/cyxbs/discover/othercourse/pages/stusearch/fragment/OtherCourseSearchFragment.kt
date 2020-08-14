package com.mredrock.cyxbs.discover.othercourse.pages.stusearch.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.discover.othercourse.AutoWrapAdapter
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.pages.stulist.StuListActivity
import com.mredrock.cyxbs.discover.othercourse.pages.stusearch.viewmodel.OtherCourseSearchViewModel
import com.mredrock.cyxbs.discover.othercourse.room.History
import com.mredrock.cyxbs.discover.othercourse.snackbar
import kotlinx.android.synthetic.main.othercourse_other_course_search_fragment.*
import org.jetbrains.anko.startActivity

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
                    viewModel.addHistory(History(text))

                }
                context?.startActivity<StuListActivity>("stu_list" to it)
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
                context?.doIfLogin {
                    viewModel.getPerson(text)
                    lastSearch = text
                }
                et_discover_other_course_search.setText("")
                return@setOnEditorActionListener true
            }
        }
    }

    private fun initHistory() {
        viewModel.getHistory()
        viewModel.mHistory.observe {
            it ?: return@observe
            aw_other_course_fragment.adapter = AutoWrapAdapter(it) { text ->
                viewModel.getPerson(text)
            }
        }
    }

}