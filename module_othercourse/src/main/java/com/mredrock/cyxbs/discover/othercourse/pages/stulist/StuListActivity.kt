package com.mredrock.cyxbs.discover.othercourse.pages.stulist

import android.os.Bundle
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.databinding.OthercourseDiscoverActivityStuListBinding
import com.mredrock.cyxbs.discover.othercourse.network.Person
import com.mredrock.cyxbs.discover.othercourse.room.HistoryDatabase
import com.mredrock.cyxbs.discover.othercourse.room.STUDENT_TYPE
import kotlinx.android.synthetic.main.othercourse_discover_activity_stu_list.*

class StuListActivity : BaseActivity() {
    var title: ObservableField<String> = ObservableField("")
    lateinit var mDataBinding: OthercourseDiscoverActivityStuListBinding
    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    val database: HistoryDatabase by lazy { HistoryDatabase.getDatabase(BaseApp.context) }
    var historyId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.othercourse_discover_activity_stu_list)
        mDataBinding.stuListActivity = this
        bottomSheetBehavior = BottomSheetBehavior.from(course_bottom_sheet_content)
        historyId = intent.getIntExtra("history_id", -1)
        val mStuList = intent.getSerializableExtra("stu_list") as List<Person>
        if (mStuList.isNotEmpty()) {
            if (mStuList[0].type == STUDENT_TYPE) {
                title.set("同学课表")
            } else {
                title.set("老师课表")
            }
        }

        discover_other_course_rv_stu_list.layoutManager = LinearLayoutManager(this)
        discover_other_course_rv_stu_list.adapter = StuListAdapter(this, mStuList)
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }

}
