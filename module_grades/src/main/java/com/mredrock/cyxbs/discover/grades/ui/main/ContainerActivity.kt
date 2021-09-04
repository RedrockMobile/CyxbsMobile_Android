package com.mredrock.cyxbs.discover.grades.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserService
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.config.DISCOVER_GRADES
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.pressToZoomOut
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.analyze.isSuccessful
import com.mredrock.cyxbs.discover.grades.ui.adapter.ExamAdapter
import com.mredrock.cyxbs.discover.grades.ui.fragment.GPAFragment
import com.mredrock.cyxbs.discover.grades.ui.fragment.NoBindFragment
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

    private lateinit var viewModel: ContainerViewModel
    private val user: IUserService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserService()
    }
    private lateinit var mAdapter: ExamAdapter
    private val data = mutableListOf<Exam>()

    //grades
    private lateinit var parent: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grades_activity_container)
        common_toolbar.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    this@ContainerActivity,
                    R.color.common_mine_sign_store_bg
                )
            )
            initWithSplitLine(
                "考试与成绩",
                false
            )
            setTitleLocationAtLeft(true)
        }
        viewModel = ViewModelProvider(this@ContainerActivity).get(ContainerViewModel::class.java)
        init()
    }

    private fun init() {
        if (!ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()) {
            return
        }
        initExam()
        initBottomSheet()
        initObserver()
        viewModel.isContainerActivity()
        viewModel.getAnalyzeData()
    }

    override fun onNewIntent(intent: Intent?) {
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
        viewModel.replaceBindFragmentToGPAFragment.observe(this@ContainerActivity, Observer {
            if (it == true) {
                viewModel.getAnalyzeData()
            }
        })
        viewModel.bottomStateListener.observe(this@ContainerActivity, Observer {
            if (it == true) {
                val behavior = BottomSheetBehavior.from(fl_grades_bottom_sheet)
                if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
        viewModel.analyzeData.observe(this@ContainerActivity, Observer {
            if (it != null && it.isSuccessful) {
                if (typeOfFragment != IS_GPA_FRAGMENT) {
                    // 绑定成功，可解除绑定
                    typeOfFragment = IS_GPA_FRAGMENT
                    tv_grades_no_bind.text = getString(R.string.grades_unbind_stdNum)
                    tv_grades_no_bind.setOnSingleClickListener {
                        viewModel.unbindIds {
                            // 解绑成功后的操作
                            tv_grades_no_bind.text = getString(R.string.grades_no_bind_stdNum)
                            initHeader()
                            replaceFragment(NoBindFragment())
                        }
                    }
                    replaceFragment(GPAFragment())
                }
            } else {
                if (typeOfFragment != IS_BIND_FRAGMENT) {
                    // 未绑定
                    typeOfFragment = IS_BIND_FRAGMENT
                    tv_grades_no_bind.text = getString(R.string.grades_no_bind_stdNum)
                    replaceFragment(NoBindFragment())
                }
            }

        })
    }

    private fun initExam() {
        viewModel.getStatus()
        mAdapter = ExamAdapter(
            this@ContainerActivity,
            data,
            intArrayOf(R.layout.grades_item_init, R.layout.grades_item_exam)
        )
        rv_exam_main.adapter = mAdapter
        rv_exam_main.layoutManager = LinearLayoutManager(this@ContainerActivity)

        //观察数据
        viewModel.examData.observe(this@ContainerActivity, Observer { list ->
            data.addAll(list)
            mAdapter.notifyDataSetChanged()
        })

        viewModel.nowStatus.observe(this@ContainerActivity, Observer { status ->
            if (status.examModel == "magipoke") {
                rv_exam_main.visibility = View.VISIBLE
                wv_exam_main.visibility = View.GONE
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
        parent.tv_grades_name.text = user.getRealName()
        tv_grades_no_bind.setOnSingleClickListener { v ->
            v.pressToZoomOut()
            val intent = Intent(this, BindActivity::class.java)
            this.startActivity(intent)
        }
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadH5(baseUrl: String) {
        rv_exam_main.visibility = View.GONE
        wv_exam_main.visibility = View.VISIBLE
        //H5使用Vue，需要开启js
        wv_exam_main.setBackgroundColor(0)
        wv_exam_main.settings.apply {
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
                BaseApp.context.resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK
                == Configuration.UI_MODE_NIGHT_YES
            ) 1 else 0
        val url = "$baseUrl/?stuNum=$stuNum&uiType=$uiType"
        wv_exam_main.loadUrl(url)
    }
}
