package com.mredrock.cyxbs.discover.grades.ui.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.config.DISCOVER_GRADES
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.Grade
import com.mredrock.cyxbs.discover.grades.ui.adapter.ExamAdapter
import com.mredrock.cyxbs.discover.grades.ui.adapter.GradesShowAdapter
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
import kotlinx.android.synthetic.main.grades_activity_container.*
import kotlinx.android.synthetic.main.grades_fragment.*
import kotlinx.android.synthetic.main.grades_fragment.view.*

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */

@Route(path = DISCOVER_GRADES)
class ContainerActivity : BaseActivity() {
    //exam
    override val isFragmentActivity = true
    private lateinit var viewModel: ContainerViewModel
    private lateinit var user: User
    private lateinit var mAdapter: ExamAdapter
    private val data = mutableListOf<Exam>()

    //grades
    private lateinit var parent: View
    private var gradesData: MutableList<Grade> = ArrayList()
    private lateinit var adapter: GradesShowAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grades_activity_container)
        common_toolbar.init("考试与成绩")

        if (BaseApp.user == null) {
            toast("无法获取到用户信息")
            return
        } else {
            user = BaseApp.user!!
            viewModel = ViewModelProviders.of(this@ContainerActivity).get(ContainerViewModel::class.java)
            initExam()
            initGrades()
        }
    }

    private fun initExam() {
        mAdapter = ExamAdapter(this@ContainerActivity, data, intArrayOf(R.layout.grades_item_init, R.layout.grades_item_exam))
        rv_exam_main.adapter = mAdapter
        rv_exam_main.layoutManager = LinearLayoutManager(this@ContainerActivity)
        data.add(0, Exam())//这里在首位加空Exam是为了给Adapter里的Header占位

        //观察数据
        viewModel.examData.observe(this@ContainerActivity, Observer { list ->
            data.addAll(list)
            mAdapter.notifyDataSetChanged()
        })

        loadExam()
    }

    private fun loadExam() {
        viewModel.loadData(user.stuNum)
    }

    private fun initGrades() {
        parent = fl_grades_bottom_sheet
        recyclerView = parent.rv_grades
        initHeader()
        initRv()
        initBehavior()
    }

    private fun initBehavior() {
        val behavior = BottomSheetBehavior.from(parent)
        parent.post {
            behavior.isHideable = false
            behavior.peekHeight = fl_grades_header.height
            rv_exam_main.setPadding(0, 0, 0, fl_grades_header.height)
        }
    }

    private fun initHeader() {
        val userService = ServiceManager.getService(IUserService::class.java)
        Glide.with(BaseApp.context).load(userService.getAvatarImgUrl()).into(parent.iv_grades_avatar)
        parent.tv_grades_stuNum.text = userService.getStuNum()
//        parent.tv_grades_college.text = userService.get ?: "未设置学院"
        parent.tv_grades_name.text = userService.getRealName()?: "未设置姓名"
    }

    private fun initRv() {
        adapter = GradesShowAdapter(gradesData, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        viewModel.gradesData.observe(this@ContainerActivity, Observer {
            gradesData.clear()
            gradesData.addAll(it as MutableList<Grade>)
            adapter.notifyDataSetChanged()
        })
        viewModel.loadGrades(user.stuNum, user.idNum!!)
    }

}
