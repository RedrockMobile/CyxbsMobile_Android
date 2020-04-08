package com.mredrock.cyxbs.discover.grades.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.config.DISCOVER_GRADES
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.service.account.IUserService
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.analyze.isSuccessful
import com.mredrock.cyxbs.discover.grades.ui.adapter.ExamAdapter
import com.mredrock.cyxbs.discover.grades.ui.adapter.GradesShowAdapter
import com.mredrock.cyxbs.discover.grades.ui.fragment.BindFragment
import com.mredrock.cyxbs.discover.grades.ui.fragment.GPAFragment
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
import com.mredrock.cyxbs.discover.grades.utils.extension.dp2px
import kotlinx.android.synthetic.main.grades_activity_container.*
import kotlinx.android.synthetic.main.grades_bottom_sheet.*
import kotlinx.android.synthetic.main.grades_bottom_sheet.view.*

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */

@Route(path = DISCOVER_GRADES)
class ContainerActivity : BaseActivity() {
    companion object {
        @JvmStatic
        val UNDEFINED = 1

        @JvmStatic
        val IS_BIND_FRAGMENT = 2

        @JvmStatic
        val IS_GPA_FRAGMENT = 3
    }

    override val loginConfig = LoginConfig(isFinish = true)

    //区分FrameLayout内的fragment的type：未确定，BindFragment，或GPAFragment
    private var typeOfFragment = UNDEFINED

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
    private lateinit var adapter: GradesShowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grades_activity_container)
        common_toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(this@ContainerActivity, R.color.whiteBackground))
            initWithSplitLine("考试与成绩",
                    false)
            setTitleLocationAtLeft(true)
        }
        viewModel = ViewModelProvider(this@ContainerActivity).get(ContainerViewModel::class.java)
        init()
    }

    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        super.onLoginStateChangeEvent(event)
        init()
    }

    private fun init() {
        if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            return
        }
        initExam()
        initBottomSheet()
        initObserver()
        viewModel.getAnalyzeData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initExam()
        initBottomSheet()
        initObserver()
        viewModel.getAnalyzeData()
    }

    private fun initObserver() {
        viewModel.replaceBindFragmentToGPAFragment.observe(this@ContainerActivity, Observer {
            if (it == true) {
                viewModel.getAnalyzeData()
            }
        })
        viewModel.analyzeData.observe(this@ContainerActivity, Observer {
            if (it != null && it.isSuccessful) {
                if (typeOfFragment != IS_GPA_FRAGMENT) {
                    typeOfFragment = IS_GPA_FRAGMENT
                    replaceFragment(GPAFragment())
                }
            } else {
                if (typeOfFragment != IS_BIND_FRAGMENT) {
                    typeOfFragment = IS_BIND_FRAGMENT
                    replaceFragment(BindFragment())
                }
            }

        })

    }

    private fun initExam() {
        mAdapter = ExamAdapter(this@ContainerActivity, data, intArrayOf(R.layout.grades_item_init, R.layout.grades_item_exam))
        rv_exam_main.adapter = mAdapter
        rv_exam_main.layoutManager = LinearLayoutManager(this@ContainerActivity)

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

    private fun initBottomSheet() {
        parent = fl_grades_bottom_sheet
        initHeader()
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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.grades_bottom_sheet_frame_layout, fragment)
        transaction.commit()
    }


    override fun onBackPressed() {
        val behavior = BottomSheetBehavior.from(fl_grades_bottom_sheet)
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}
