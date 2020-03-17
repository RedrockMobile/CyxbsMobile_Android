package com.mredrock.cyxbs.discover.grades.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.DISCOVER_GRADES
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.Grade
import com.mredrock.cyxbs.discover.grades.ui.adapter.ExamAdapter
import com.mredrock.cyxbs.discover.grades.ui.adapter.GradesShowAdapter
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
import com.mredrock.cyxbs.discover.grades.utils.extension.dp2px
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
    private val user: IUserService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserService()
    }
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
        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@ContainerActivity, R.color.whiteBackground))
            initWithSplitLine("考试与成绩",
                    false)
            setTitleLocationAtLeft(true)
        }
        viewModel = ViewModelProviders.of(this@ContainerActivity).get(ContainerViewModel::class.java)
        initExam()
        initGrades()

        //初始化数据和绑定CoordinatorLayout(必须，内部处理了BottomSheet的事件分发)
        gpa_graph.array = arrayListOf(4.0F, 3.5F, 3F, 2.5F, 2F, 1.8F, 1.5F, 1.2F)
        gpa_graph.bindCoordinator(coordinator)
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
        viewModel.loadData(user.getStuNum())
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
            behavior.peekHeight = fl_grades_header.height + dp2px(45)
            rv_exam_main.setPadding(0, 0, 0, fl_grades_header.height)
        }
    }

    private fun initHeader() {
        Glide.with(BaseApp.context).load(user.getAvatarImgUrl()).into(parent.iv_grades_avatar)
        parent.tv_grades_stuNum.text = user.getStuNum()
        parent.tv_grades_college.text = user.getCollege()
        parent.tv_grades_name.text = user.getRealName()
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
        val idNum = BaseApp.context.defaultSharedPreferences.getString("SP_KEY_ID_NUM", "")
                ?: return
        viewModel.loadGrades(user.getStuNum(), idNum)
    }

}
