package com.mredrock.cyxbs.discover.noclass.pages.stuselect

import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.network.Student
import kotlinx.android.synthetic.main.discover_noclass_activity_stu_select.*

class NoClassStuSelectActivity : BaseActivity() {
    override val isFragmentActivity = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_noclass_activity_stu_select)

        initClickListener()
        setList()

        initAnimation()
    }

    private fun setList() {
        val mList = intent.getSerializableExtra("stu_list") as List<Student>
        noclass_rv_stu_select.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        noclass_rv_stu_select.adapter = NoClassStuSelectRvAdapter(mList)
    }

   fun initClickListener(){

        tv_noclass_stu_select_cancel.setOnClickListener {
            finish()
        }
   }
    fun initAnimation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition = Slide(Gravity.BOTTOM).apply { duration = 1000
            excludeTarget(cl_noclass_stu_select,true)}
        }
    }
}
