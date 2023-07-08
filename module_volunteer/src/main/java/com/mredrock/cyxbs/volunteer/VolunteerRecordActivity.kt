package com.mredrock.cyxbs.volunteer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_RECORD
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.volunteer.adapter.VolunteerMainFragmentAdapter
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.event.VolunteerLogoutEvent
import com.mredrock.cyxbs.volunteer.fragment.VolunteerAffairFragment
import com.mredrock.cyxbs.volunteer.fragment.VolunteerRecordFragment
import com.mredrock.cyxbs.volunteer.viewmodel.VolunteerRecordViewModel
import com.mredrock.cyxbs.volunteer.widget.LogoutDialog
import org.greenrobot.eventbus.EventBus


@Route(path = DISCOVER_VOLUNTEER_RECORD)
class VolunteerRecordActivity : BaseViewModelActivity<VolunteerRecordViewModel>() {

    private val vp_volunteer_category by R.id.vp_volunteer_category.view<ViewPager>()
    private val tl_volunteer_category by R.id.tl_volunteer_category.view<TabLayout>()
    private val iv_back by R.id.iv_back.view<AppCompatImageView>()
    private val tv_volunteer_logout by R.id.tv_volunteer_logout.view<TextView>()
    private val tv_volunteer_total_time by R.id.tv_volunteer_total_time.view<AppCompatTextView>()
    private val tv_volunteer_total_times by R.id.tv_volunteer_total_times.view<AppCompatTextView>()

    companion object {
        fun startActivity(activity: Activity, volunteerTime: VolunteerTime) {
            activity.startActivity(
                Intent(activity, VolunteerRecordActivity::class.java).apply {
                    putExtra("volunteerTime", Gson().toJson(volunteerTime))
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.volunteer_activity_record)
        initObserve()
        initView()
    }

    private fun initView() {
        val vtJson = intent.getStringExtra("volunteerTime")
        viewModel.volunteerTime.value =
            Gson().fromJson<VolunteerTime>(vtJson, VolunteerTime::class.java)
        val volunteerRecordFragment = VolunteerRecordFragment().apply {
            arguments = Bundle().apply {
                putString("volunteerTime", vtJson)
            }
        }
        vp_volunteer_category.adapter = VolunteerMainFragmentAdapter(
            supportFragmentManager,
            listOf(volunteerRecordFragment, VolunteerAffairFragment()),
            listOf(
                getString(R.string.volunteer_string_tab_record),
                getString(R.string.volunteer_string_tab_activity)
            )
        )
        tl_volunteer_category.setupWithViewPager(vp_volunteer_category)
        tl_volunteer_category.setSelectedTabIndicator(R.drawable.volunteer_ic_question_tab_indicator)

        iv_back.setOnClickListener { finish() }
        tv_volunteer_logout.setOnClickListener {
            LogoutDialog.show(this, {}, {
                viewModel.unBindAccount()
                ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
                EventBus.getDefault().postSticky(VolunteerLogoutEvent())
                finish()
            })
        }
    }


    private fun initObserve() {
        viewModel.volunteerTime.observe(this, Observer {
            it ?: return@Observer
            tv_volunteer_total_time.text = it.hours.toString()
            tv_volunteer_total_times.text = it.record?.size.toString()
        })

    }

}
