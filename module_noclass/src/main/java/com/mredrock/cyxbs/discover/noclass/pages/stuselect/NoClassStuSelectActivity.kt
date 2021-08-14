package com.mredrock.cyxbs.discover.noclass.pages.stuselect

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.Window
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.network.Student
import kotlinx.android.synthetic.main.noclass_activity_stu_select.*
import com.mredrock.cyxbs.common.utils.extensions.*


class NoClassStuSelectActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.noclass_activity_stu_select)


        initClickListener()
        setList()

        initAnimation()
    }

    private fun setList() {
        val mList = intent.getSerializableExtra("stu_list") as List<Student>
        noclass_rv_stu_select.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        noclass_rv_stu_select.adapter = NoClassStuSelectRvAdapter(mList)
    }

    fun initClickListener() {

        tv_noclass_stu_select_cancel.setOnSingleClickListener {
            finish()
        }
    }

    private fun initAnimation() {
        window.enterTransition = Slide(Gravity.BOTTOM).apply {
            duration = 1000
            excludeTarget(cl_noclass_stu_select, true)
            addTarget(R.id.ll_stu_select)
        }
    }
}
