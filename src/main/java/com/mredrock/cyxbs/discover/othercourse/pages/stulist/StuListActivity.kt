package com.mredrock.cyxbs.discover.othercourse.pages.stulist

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.network.Person
import com.mredrock.cyxbs.discover.othercourse.room.STUDENT_TYPE
import kotlinx.android.synthetic.main.discover_activity_stu_list.*

class StuListActivity : BaseActivity() {
    override val isFragmentActivity: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_activity_stu_list)

        val mStuList = intent.getSerializableExtra("stu_list") as List<Person>
        var title = ""
        if(mStuList.size>1){
            if(mStuList[0].type == STUDENT_TYPE){
                title = "选择同学"
            }else{
                title = "选择老师"
            }
        }
        common_toolbar.init(title)

        discover_other_course_rv_stu_list.layoutManager = LinearLayoutManager(this)
        discover_other_course_rv_stu_list.adapter = StuListAdater(mStuList)
    }
}
