package com.mredrock.cyxbs.volunteer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import com.mredrock.cyxbs.volunteer.adapter.VolunteerMainFragmentAdapter
import com.mredrock.cyxbs.volunteer.event.VolunteerLoginEvent
import com.mredrock.cyxbs.volunteer.fragment.AllVolunteerFragment
import com.mredrock.cyxbs.volunteer.fragment.NoTimeVolunteerFragment
import com.mredrock.cyxbs.volunteer.fragment.VolunteerRecordFragment
import com.mredrock.cyxbs.volunteer.viewmodel.VolunteerRecordViewModel
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP
import kotlinx.android.synthetic.main.volunteer_activity_record.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        initVP()
    }

    private fun initVP() {
        vp_volunteer_category.adapter = VolunteerMainFragmentAdapter(supportFragmentManager, listOf(VolunteerRecordFragment(), NoTimeVolunteerFragment()), listOf("志愿记录", "校内志愿"))
        tl_volunteer_category.setupWithViewPager(vp_volunteer_category)
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
//        val apiService = ApiGenerator.getApiService(ApiService::class.java)
//        apiService.getVolunteerRecord(Authorization, uid)
//                .setSchedulers()
//                .safeSubscribeBy(onNext = { volunteerTime: VolunteerTime ->
        viewModel.volunteerTime.observe(this, Observer {
            it ?: return@Observer
            //请求加载动画停止，并设置不可见
            //进行UI显示处理
        })

//                }, onError = {
//                    LogUtils.d("volunteer", it.toString())
//                })
    }

    /**
     * 适配TabLayout
     */


    /**
     * 处理请求到的每条时长信息的时间（年份）
     * 初始化显示在TabLayout上的年份标签（全部，今年，去年，前年， 大前年）
     * 一共有全部（指该此时的年份再加上之前的三年） + 四年
     */


    /**
     * 初始化FragmentList，将每一年的数据传入fragment：
     * 如果该年份无时长记录，传入无时长fragment；若有时长记录传入，有时长对应的fragment
     *
     * @param dataBean 接口请求到的用户时长数据
     */



}
