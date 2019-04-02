package com.mredrock.cyxbs.discover.noclass.pages.noclass

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.model.RouteMeta.build
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.COURSE_NO_COURSE_INVITE
import com.mredrock.cyxbs.common.config.DISCOVER_NO_CLASS
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.discover.noclass.R
import com.mredrock.cyxbs.discover.noclass.network.Student
import com.mredrock.cyxbs.discover.noclass.pages.stuselect.NoClassStuSelectActivity
import kotlinx.android.synthetic.main.discover_noclass_activity_no_class.*
import java.io.Serializable

@Route(path = DISCOVER_NO_CLASS)
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
        initObserver()
    }

    private fun initObserver() {
        viewModel.mStuList.observe(this, Observer {
            when {
                it!!.size > 1 -> {
                    val bundle = Bundle()
                    bundle.putSerializable("stu_list", it as Serializable)
                    val intent = Intent(this, NoClassStuSelectActivity::class.java)
                    intent.putExtras(bundle)
                    startActivityForResult(intent, REQUEST_SELECT)
                }
                it.size == 1 -> {
                    addStu(it[0])
                }
            }
        })
    }

    fun doSearch(str: String) {
        viewModel.getStudent(str)
    }

    private fun initBtn() {
        noclass_btn_query.setOnClickListener {
            val students = (noclass_rv.adapter as NoClassRvAdapter).getStuList()
            val nameList = arrayListOf<String>()
            val numList = arrayListOf<String>()
            students.forEach {
                numList.add(it.stunum?:"")
                nameList.add(it.name?:"")
            }
            Log.d("fxy","stuNum $numList")
            ARouter.getInstance()
                    .build(COURSE_NO_COURSE_INVITE)
                    .withStringArrayList("stuNumList",numList)
                    .withStringArrayList("stuNameList",nameList)
                    .navigation()
        }
    }

    private fun initStuList() {
        BaseApp.user?.apply {
            val stu = Student()
            stu.name = name
            stu.stunum = stunum
            mStuList!!.add(stu)
        }
        mAdapter = NoClassRvAdapter(mStuList!!, this)
        noclass_rv.layoutManager = GridLayoutManager(this, 4)
        noclass_rv.adapter = mAdapter
    }

    private fun addStu(stu: Student) {
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
