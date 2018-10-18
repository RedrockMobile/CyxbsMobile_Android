package com.mredrock.cyxbs.discover.noclass.pages.noclass

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_NO_CLASS
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.network.Student
import kotlinx.android.synthetic.main.discover_noclass_activity_no_class.*

class NoClassActivity : BaseViewModelActivity<NoClassViewModel>() {

    override val viewModelClass = NoClassViewModel::class.java

    override val isFragmentActivity = false

    private var mStuList: MutableList<Student>? = null
    private var mAdapter: NoClassRvAdapter? = null

    companion object {
        const val REQUEST_SELECT = 1
    }

    private fun initToolbar() {
        if (common_toolbar != null) {
            common_toolbar.init("没课约")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_noclass_activity_no_class)
        mStuList = ArrayList()
        initToolbar()
        initStuList()
        initBtn()
    }


    private fun initBtn() {
        noclass_btn_query.setOnClickListener {
            (noclass_rv.adapter as NoClassRvAdapter).getStuList()
        }
    }

    private fun initStuList() {
        val stu = Student()
        stu.name = "zzx"
        stu.stunum = "2016214049"
        mStuList!!.add(stu)
        mAdapter = NoClassRvAdapter(mStuList!!, this)
        noclass_rv.layoutManager = GridLayoutManager(this, 4)
        noclass_rv.adapter = mAdapter
    }

    fun addStu(stu: Student) {
        mAdapter!!.addStu(stu)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT && resultCode == Activity.RESULT_OK) {
            val stu = data!!.extras.getSerializable("stu") as Student
            mAdapter!!.addStu(stu)
        }
    }


}
