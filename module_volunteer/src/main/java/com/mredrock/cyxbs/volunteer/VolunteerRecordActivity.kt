package com.mredrock.cyxbs.volunteer

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER
import com.mredrock.cyxbs.common.config.DISCOVER_VOLUNTEER_RECORD
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.startActivity
import com.mredrock.cyxbs.volunteer.adapter.VolunteerMainFragmentAdapter
import com.mredrock.cyxbs.volunteer.event.VolunteerLogoutEvent
import com.mredrock.cyxbs.volunteer.fragment.VolunteerAffairFragment
import com.mredrock.cyxbs.volunteer.fragment.VolunteerRecordFragment
import com.mredrock.cyxbs.volunteer.viewmodel.VolunteerRecordViewModel
import kotlinx.android.synthetic.main.volunteer_activity_record.*
import kotlinx.android.synthetic.main.volunteer_layout_record_view.*
import org.greenrobot.eventbus.EventBus


@Route(path = DISCOVER_VOLUNTEER_RECORD)
class VolunteerRecordActivity : BaseViewModelActivity<VolunteerRecordViewModel>() {
    override val isFragmentActivity: Boolean = false


    companion object {
        fun startActivity(activity: Activity) {
            activity.startActivity<VolunteerRecordActivity>()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.volunteer_activity_record)
        isSlideable = false
        initObserve()
        initView()
    }

    private fun initView() {
        vp_volunteer_category.adapter = VolunteerMainFragmentAdapter(supportFragmentManager, listOf(VolunteerRecordFragment(), VolunteerAffairFragment()), listOf("志愿记录", "校内志愿"))
        tl_volunteer_category.setupWithViewPager(vp_volunteer_category)
        tl_volunteer_category.setSelectedTabIndicator(R.drawable.volunteer_ic_question_tab_indicator)

        iv_back.setOnClickListener { finish() }
        tv_volunteer_logout.setOnClickListener {
            ARouter.getInstance().build(DISCOVER_VOLUNTEER).navigation()
            EventBus.getDefault().postSticky(VolunteerLogoutEvent())
            finish()
        }
    }


    private fun initObserve() {
        viewModel.volunteerTime.observe(this, Observer {
            it ?: return@Observer
            tv_volunteer_total_time.text = it.hours.toString()
            tv_volunteer_total_times.text = it.record?.size.toString()
        })

    }

    override val viewModelClass: Class<VolunteerRecordViewModel>
        get() = VolunteerRecordViewModel::class.java


}
