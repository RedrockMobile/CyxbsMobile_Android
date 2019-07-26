package com.mredrock.cyxbs.discover.othercourse.pages.stulist

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.network.Student
import kotlinx.android.synthetic.main.discover_activity_stu_list.*

class StuListActivity : BaseActivity() {
    override val isFragmentActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_activity_stu_list)
        common_toolbar.init("选择同学")
        val mStuList = intent.getSerializableExtra("stu_list") as List<Student>
        discover_other_course_rv_stu_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        discover_other_course_rv_stu_list.adapter = StuListAdater(mStuList)
    }
}
