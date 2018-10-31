package com.mredrock.cyxbs.discover.othercourse.pages.stusearch

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_OTHER_COURSE
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.pages.stulist.StuListActivity
import com.mredrock.cyxbs.discover.othercourse.snackbar
import kotlinx.android.synthetic.main.discover_activity_other_course.*
import org.jetbrains.anko.startActivity

@Route(path = DISCOVER_OTHER_COURSE)
class OtherCourseActivity : BaseViewModelActivity<OtherCourseViewModel>() {
    override val viewModelClass = OtherCourseViewModel::class.java

    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_activity_other_course)
        common_toolbar.init("同学课表")
        initObserve()
        setEdit()
        setSearch()
    }

    private fun initObserve() {
        viewModel.mStuList.observe( this, Observer {
            if (it!!.isNotEmpty()) {
                startActivity<StuListActivity>("stu_list" to it)
            } else {
                snackbar("查无此人")
            }
        })
    }

    private fun setSearch() {
        discover_other_course_btn_search.setOnClickListener {
            val text = discover_other_course_edit_search.text.toString()
            if (text.isEmpty()) snackbar("输入为空")
            else {
                viewModel.getStudent(text)
            }
        }
    }

    private fun setEdit() {
        discover_other_course_edit_search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0) discover_other_course_delete_edit_search.gone()
                else discover_other_course_delete_edit_search.visible()
            }

        })
        discover_other_course_delete_edit_search.setOnClickListener { discover_other_course_edit_search.text.clear() }
    }
}
