package com.mredrock.cyxbs.discover.noclass.pages.stuselect

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.network.Student
import kotlinx.android.synthetic.main.discover_noclass_activity_stu_select.*

class NoClassStuSelectActivity : BaseActivity() {
    override val isFragmentActivity = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_noclass_activity_stu_select)
        initToolbar()
        setList()
    }

    private fun setList() {
        val mList = intent.getSerializableExtra("stu_list") as List<Student>
        noclass_rv_stu_select.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        noclass_rv_stu_select.adapter = NoClassStuSelectRvAdapter(mList)
    }

    private fun initToolbar() {
        if (common_toolbar != null) {
            common_toolbar.init("选 择")
        }
    }
}
