package com.mredrock.cyxbs.volunteer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_RECORD
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import com.mredrock.cyxbs.volunteer.adapter.VolunteerMainFragmentAdapter
import com.mredrock.cyxbs.volunteer.fragment.VolunteerAffairFragment
import com.mredrock.cyxbs.volunteer.fragment.VolunteerRecordFragment
import com.mredrock.cyxbs.volunteer.viewmodel.VolunteerRecordViewModel
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP
import kotlinx.android.synthetic.main.volunteer_activity_record.*
import kotlinx.android.synthetic.main.volunteer_layout_record_view.*

@Route(path = DISCOVER_VOLUNTEER_RECORD)
class VolunteerRecordActivity : BaseActivity() {
    override val isFragmentActivity: Boolean = false


    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity<VolunteerRecordActivity>()
        }
    }

    private lateinit var viewModel: VolunteerRecordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.volunteer_activity_record)
        viewModel = ViewModelProvider(this).get(VolunteerRecordViewModel::class.java)
        isSlideable = false
        initData()
        initObserve()
        initView()
    }

    private fun initView() {
        vp_volunteer_category.adapter = VolunteerMainFragmentAdapter(supportFragmentManager, listOf(VolunteerRecordFragment(), VolunteerAffairFragment()), listOf("志愿记录", "校内志愿"))
        tl_volunteer_category.setupWithViewPager(vp_volunteer_category)
        tv_volunteer_logout.setOnClickListener {
            VolunteerTimeSP(this).unBindVolunteerInfo()
            ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
            finish()
        }
    }


    /**
     * 判断用户是否绑定了账号，如果绑定，则请求接口面，没有则返回至登录界面
     */
    private fun initData() {
        //在shearedPreference里获取用户绑定的数据
        if (!VolunteerTimeSP(this).isBind()) {
            //没有绑定数据，先登录
            CyxbsToast.makeText(this, "请先登录绑定账号哦", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, VolunteerLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initObserve() {
        viewModel.volunteerTime.observe(this, Observer {
            it ?: return@Observer
            //请求加载动画停止，并设置不可见
            //进行UI显示处理
        })

    }


}
