package com.mredrock.cyxbs.discover.grades.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserService
import com.mredrock.cyxbs.common.config.DISCOVER_GRADES
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.webView.LiteJsWebView
import com.mredrock.cyxbs.config.route.LOGIN_BIND_IDS
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.analyze.isSuccessful
import com.mredrock.cyxbs.discover.grades.ui.adapter.ExamAdapter
import com.mredrock.cyxbs.discover.grades.ui.fragment.GPAFragment
import com.mredrock.cyxbs.discover.grades.ui.fragment.NoBindFragment
import com.mredrock.cyxbs.discover.grades.ui.viewModel.ContainerViewModel
import de.hdodenhof.circleimageview.CircleImageView


/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */

@Route(path = DISCOVER_GRADES)
class ContainerActivity : BaseViewModelActivity<ContainerViewModel>() {

    private val user: IUserService by lazy {
        ServiceManager(IAccountService::class).getUserService()
    }
    private lateinit var mAdapter: ExamAdapter
    private val data = mutableListOf<Exam>()


    private val mTvGradesNoBind by R.id.tv_grades_no_bind.view<TextView>()
    private val mRvExamMain by R.id.rv_exam_main.view<RecyclerView>()
    private val mTvGradesStuNum by R.id.tv_grades_stuNum.view<TextView>()
    private val parent by R.id.fl_grades_bottom_sheet.view<FrameLayout>()
    private val mTvGradesName by R.id.tv_grades_name.view<TextView>()
    private val mWvExamMain by R.id.wv_exam_main.view<LiteJsWebView>()
    private val mLlGradesHeader by R.id.ll_grades_header.view<LinearLayout>()
    private val mIvGradesAvatar by R.id.iv_grades_avatar.view<CircleImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grades_activity_container)
        common_toolbar.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    this@ContainerActivity,
                    com.mredrock.cyxbs.common.R.color.common_mine_sign_store_bg
                )
            )
            initWithSplitLine(
                "考试与成绩",
                false
            )
            setTitleLocationAtLeft(true)
        }
        init()
    }

    private fun init() {
        if (!ServiceManager(IAccountService::class).getVerifyService().isLogin()) {
            return
        }
        initExam()
        initBottomSheet()
        initObserver()
        viewModel.isContainerActivity()
        viewModel.getAnalyzeData()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initExam()
        initBottomSheet()
        initObserver()
        viewModel.isContainerActivity()
        viewModel.getAnalyzeData()
    }

    override fun onStart() {
        super.onStart()
        initBottomSheet()
        initObserver()
        viewModel.getAnalyzeData()
    }

    private fun initObserver() {
        viewModel.analyzeData.observe {
            if (it.isSuccessful) {
                mTvGradesNoBind.text = "已绑定"
                replaceFragment(GPAFragment())
            } else {
                // 未绑定
                mTvGradesNoBind.text = getString(R.string.grades_no_bind_stdNum)
                replaceFragment(NoBindFragment())
            }
        }
        // 监听isBinding的变化 改变按钮点击事件
        viewModel.isBinding.observe {
            if (!it) {
                // 当前未绑定账号，点击前往绑定账号界面
                mTvGradesNoBind.setOnSingleClickListener { v ->
                    v.pressToZoomOut()
                    ARouter.getInstance().build(LOGIN_BIND_IDS).navigation()
                }
            }
        }
    }

    private fun initExam() {
        viewModel.getStatus()
        mAdapter = ExamAdapter(data)
        mRvExamMain.adapter = mAdapter
        mRvExamMain.layoutManager = LinearLayoutManager(this@ContainerActivity)

        //观察数据
        viewModel.examData.observe(this@ContainerActivity, Observer { list ->
            data.addAll(list)
            mAdapter.notifyDataSetChanged()
        })

        viewModel.nowStatus.observe(this@ContainerActivity, Observer { status ->
            if (status.examModel == "magipoke") {
                mRvExamMain.visibility = View.VISIBLE
                mWvExamMain.visibility = View.GONE
                loadExam()
            } else {
                loadH5(status.url)
            }
        })

    }

    private fun loadExam() {
        viewModel.loadData(user.getStuNum())
    }

    private fun initBottomSheet() {
        initHeader()
    }

    private fun initHeader() {
        Glide.with(this).load(user.getAvatarImgUrl()).into(mIvGradesAvatar)
        mTvGradesStuNum.text = user.getStuNum()
        mTvGradesName.text = user.getRealName()
        val behavior = BottomSheetBehavior.from(parent)
        parent.post {
            // 动态设置 peekHeight，因为内布局需要显示在状态栏之下，使用 fitsSystemWindows 会出现部分手机不兼容
            // 所以就直接用 View 来占位
            behavior.peekHeight = mLlGradesHeader.height
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.grades_bottom_sheet_frame_layout, fragment)
        transaction.commit()
    }


    override fun onBackPressed() {
        val behavior = BottomSheetBehavior.from(parent)
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadH5(baseUrl: String) {
        mRvExamMain.visibility = View.GONE
        mWvExamMain.visibility = View.VISIBLE
        //H5使用Vue，需要开启js
        mWvExamMain.setBackgroundColor(0)
        mWvExamMain.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            mediaPlaybackRequiresUserGesture = false
        }
        val stuNum = user.getStuNum()
        val uiType =
            if (
                this.resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK
                == Configuration.UI_MODE_NIGHT_YES
            ) 1 else 0
        val url = "$baseUrl/?stuNum=$stuNum&uiType=$uiType"
        mWvExamMain.loadUrl(url)
    }
}
