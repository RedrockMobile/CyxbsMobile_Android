package com.mredrock.cyxbs.volunteer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.volunteer.R
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFragmentAdapter
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.event.VolunteerLoginEvent
import com.mredrock.cyxbs.volunteer.viewmodel.VolunteerRecordViewModel
import kotlinx.android.synthetic.main.volunteer_activity_record.*
import kotlinx.android.synthetic.main.volunteer_fragment_record.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * Created by yyfbe, Date on 2020/9/4.
 */
class VolunteerRecordFragment : BaseFragment(), EventBusLifecycleSubscriber {
    private val yearList by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<String>() }

    private val allYearList by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<String>() }

    private val fragmentList by lazy(LazyThreadSafetyMode.NONE) { ArrayList<Fragment>() }

    private var volunteerTime : VolunteerTime? = null
    //每个key都对应的该用户key所对应的那一年的所有时长信息
    //用来加入每一年的信息
    //用来储存年份
    private val firstYear by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<VolunteerTime.RecordBean>() }
    private val secondYear by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<VolunteerTime.RecordBean>() }
    private val thirdYear by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<VolunteerTime.RecordBean>() }
    private val lastYear by lazy(LazyThreadSafetyMode.NONE) { mutableListOf<VolunteerTime.RecordBean>() }
    private val yearMap by lazy(LazyThreadSafetyMode.NONE) { TreeMap<Int, MutableList<VolunteerTime.RecordBean>>() }

    private var viewModel: VolunteerRecordViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.volunteer_fragment_record, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = activity?.let { ViewModelProvider(it).get(VolunteerRecordViewModel::class.java) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeYears()
        initObserve()
        setTabLayout()
    }

    /**
     * 对点击事件进行绑定
     */
//    private fun initOnClickListener() {
//        volunteer_unbind.setOnClickListener {
//            val handler = Handler(Looper.getMainLooper())
//            handler.post {
//                context?.let { mContext ->
//                    MaterialDialog(mContext).show {
//                        title(text = "解除账号绑定？")
//                        message(text = "亲，真的要取消已绑定的账号吗？")
//                        positiveButton(text = "确定") {
//                            VolunteerTimeSP(mContext as Activity).unBindVolunteerInfo()
//                            val intent = Intent(mContext, VolunteerLoginActivity::class.java)
//                            startActivity(intent)
//                        }
//                        negativeButton(text = "取消") {
//                            dismiss()
//                        }
//                        cornerRadius(res = R.dimen.common_corner_radius)
//                    }
//                }
//            }
//        }

//        volunteer_time_title.setOnClickListener {
//            if (volunteer_time_tab.visibility == View.VISIBLE) {
//                volunteer_time_tab.visibility = View.GONE
//                volunteer_unshow_image.visibility = View.VISIBLE
////                volunteer_unshow_image.visibility = View.GONE
//            } else if (volunteer_time_tab.visibility == View.GONE) {
//                volunteer_time_tab.visibility = View.VISIBLE
//                volunteer_unshow_image.visibility = View.GONE
////                volunteer_unshow_image.visibility = View.VISIBLE
//            }
//        }

//        volunteer_time_back.setOnClickListener { finish() }

//        volunteer_show_image.setOnClickListener {
//            if (volunteer_time_tab.visibility == View.VISIBLE) {
//                volunteer_time_tab.visibility = View.GONE
//                volunteer_unshow_image.visibility = View.VISIBLE
////                volunteer_unshow_image.visibility = View.GONE
//            } else if (volunteer_time_tab.visibility == View.GONE) {
//                volunteer_time_tab.visibility = View.VISIBLE
//                volunteer_unshow_image.visibility = View.GONE
////                volunteer_unshow_image.visibility = View.VISIBLE
//            }
//        }
//
//        volunteer_unshow_image.setOnClickListener {
//            if (volunteer_time_tab.visibility == View.VISIBLE) {
//                volunteer_time_tab.visibility = View.GONE
//                volunteer_unshow_image.visibility = View.VISIBLE
////                volunteer_unshow_image.visibility = View.GONE
//            } else if (volunteer_time_tab.visibility == View.GONE) {
//                volunteer_time_tab.visibility = View.VISIBLE
//                volunteer_unshow_image.visibility = View.GONE
////                volunteer_unshow_image.visibility = View.VISIBLE
//            }
//        }
//    }

    private fun initializeYears() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        yearList.add("全部")
        for (i in 0..3) {
            yearList.add((year - i).toString() + "")
        }
    }

    private fun initFragmentList(dataBean: VolunteerTime?) {
        dataBean ?: return
        dataBean.record ?: return
        if (dataBean.record?.isEmpty() == true) {
            // 如果用户的志愿时长记录为0，则所5个（"全部" + 四年，和year对应）fragment都为无时长 fragment
            for (i in 0..5) {
                fragmentList.add(NoTimeVolunteerFragment())
            }
        } else {
            var recordBeen: MutableList<VolunteerTime.RecordBean>?      //单个年份的时长信息
            //用户所有的时长信息
            val allRecordList = mutableListOf<MutableList<VolunteerTime.RecordBean>>()

            //跳过yearList的第一个数据（因为第一个为用户全部年份的时长总和），从第一个年份开始（当前年份）
            for (i in 1 until yearList.size) {
                //在dealYear里面对信息进行归类处理
                dealYear(dataBean.record!!, i - 1)
                recordBeen = yearMap[Integer.parseInt(yearList[i])]
                //再循环中，遍历四年数据的同时，对记录"全部"的List进行初始化加入数据
                if (recordBeen != null && recordBeen.size != 0) allRecordList.add(recordBeen)

                if (recordBeen == null || recordBeen.isEmpty()) {
                    //如果该年数据为空，则加入无时长fragment
                    fragmentList.add(NoTimeVolunteerFragment())
                } else if (recordBeen.isNotEmpty()
                        && yearMap[Integer.parseInt(yearList[i])]!!.isNotEmpty()
                        && yearList[i] != "全部") {
                    //将有时长加fragment入fragmentLList，并对fragment的data和year进行绑定
                    val fragment = FirstVolunteerTimeFragment()
                    fragment.setRecordBeanList(recordBeen)
                    fragment.setYear(yearList[i])
                    fragmentList.add(fragment)
                }
            }

            //将所有志愿时长的fragment加入到fragmentList中的第0个元素（"全部"为界面显示的第一个fragment），并对data和year绑定
            val allVolunteerFragment = AllVolunteerFragment()
            allVolunteerFragment.updateData(dataBean.hours.toString(), allYearList, allRecordList)
            fragmentList.add(0, allVolunteerFragment)
        }
    }

    private fun initObserve() {
        viewModel!!.volunteerTime.observe(activity!!, Observer {
            initFragmentList(it ?: return@Observer)
        })
    }

    /**
     * 对请求回来的用户的所有时长进行按年份划分
     *
     * @param dataBean 接口请求的用户的所有的时长的List
     * @param nowYear 需要进行时长信息归类处理的年份
     */
    private fun dealYear(dataBean: MutableList<VolunteerTime.RecordBean>, nowYear: Int) {
        val calendar = Calendar.getInstance()

        var year: Int
        val yearCalendar = calendar.get(Calendar.YEAR)

        val yearListInt = mutableListOf<Int>()
        for (x in 0..3) {
            yearListInt.add(yearCalendar - x)
        }

        //把相应年份的时长信息加到对应年份
        for (x in dataBean.indices) {
            //截取时长的date参数中的年份（前四位为年份）， 并加入到对应年份的List中
            year = Integer.parseInt(dataBean[x].start_time!!.substring(0, 4))
            if (year == yearListInt[nowYear]) {
                when (nowYear) {
                    0 -> firstYear.add(dataBean[x])
                    1 -> secondYear.add(dataBean[x])
                    2 -> thirdYear.add(dataBean[x])
                    3 -> lastYear.add(dataBean[x])
                }
            }
        }

        //将每一年的时长list作为value值传入，年份作为key值绑定
        when (nowYear) {
            0 -> {
                yearMap[yearListInt[nowYear]] = firstYear
                if (firstYear.size != 0) allYearList.add(yearListInt[nowYear].toString() + "")
            }
            1 -> {
                yearMap[yearListInt[nowYear]] = secondYear
                if (secondYear.size != 0) allYearList.add(yearListInt[nowYear].toString() + "")
            }
            2 -> {
                yearMap[yearListInt[nowYear]] = thirdYear
                if (thirdYear.size != 0) allYearList.add(yearListInt[nowYear].toString() + "")
            }
            3 -> {
                yearMap[yearListInt[nowYear]] = lastYear
                if (lastYear.size != 0) allYearList.add(yearListInt[nowYear].toString() + "")
            }
        }
    }

    private fun setTabLayout() {
        volunteer_view_pager.adapter = activity?.supportFragmentManager?.let { VolunteerFragmentAdapter(it, fragmentList, yearList) }
        volunteer_time_tab.setupWithViewPager(volunteer_view_pager)
        volunteer_time_tab.tabMode = TabLayout.MODE_FIXED
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun getVolunteerTime(volunteerLoginEvent: VolunteerLoginEvent) {
        viewModel?.volunteerTime?.value = volunteerLoginEvent.volunteerTime
    }
}