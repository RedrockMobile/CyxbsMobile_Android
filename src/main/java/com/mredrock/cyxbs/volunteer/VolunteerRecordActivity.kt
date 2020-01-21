package com.mredrock.cyxbs.volunteer

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.volunteer.adapter.VolunteerFragmentAdapter
import com.mredrock.cyxbs.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.volunteer.fragment.AllVolunteerFragment
import com.mredrock.cyxbs.volunteer.fragment.FirstVolunteerTimeFragment
import com.mredrock.cyxbs.volunteer.fragment.NoTimeVolunteerFragment
import com.mredrock.cyxbs.volunteer.network.ApiService
import com.mredrock.cyxbs.volunteer.widget.EncryptPassword
import com.mredrock.cyxbs.volunteer.widget.VolunteerTimeSP
import kotlinx.android.synthetic.main.activity_volunteer_record.*
import java.util.*
import kotlin.collections.ArrayList

//TODO:重构为MVVM 2020.1.20
class VolunteerRecordActivity : BaseActivity(), TabLayout.OnTabSelectedListener, androidx.viewpager.widget.ViewPager.OnPageChangeListener {
    override val isFragmentActivity: Boolean = false

    private lateinit var uid: String
    private lateinit var account: String
    private lateinit var password: String
    private lateinit var adapter: VolunteerFragmentAdapter
    private lateinit var yearList: MutableList<String>
    private lateinit var allYearList: MutableList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var volunteerSP: VolunteerTimeSP
    private lateinit var animationDrawable: AnimationDrawable
    private lateinit var firstYear: MutableList<VolunteerTime.RecordBean>
    private lateinit var secondYear: MutableList<VolunteerTime.RecordBean>
    private lateinit var thirdYear: MutableList<VolunteerTime.RecordBean>
    private lateinit var lastYear: MutableList<VolunteerTime.RecordBean>
    private lateinit var yearMap: TreeMap<Int, MutableList<VolunteerTime.RecordBean>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_record)

        initToolbar()
        initOnClickLisenter()
        initData()
    }

    private fun initToolbar() {
        if (volunteer_time_toolbar != null) {
            volunteer_time_toolbar.title = ""
            setSupportActionBar(volunteer_time_toolbar)
        }
    }

    /**
     * 对点击事件进行绑定
     */
    private fun initOnClickLisenter() {
        volunteer_unbind.setOnClickListener {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                MaterialDialog.Builder(this)
                        .title("解除账号绑定？")
                        .content("亲，真的要取消已绑定的账号吗？")
                        .positiveText("确定")
                        .negativeText("取消")
                        .callback(object : MaterialDialog.ButtonCallback() {
                            override fun onPositive(dialog: MaterialDialog?) {
                                super.onPositive(dialog)
                                volunteerSP.unBindVolunteerInfo()
                                val intent = Intent(this@VolunteerRecordActivity, VolunteerLoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            override fun onNegative(dialog: MaterialDialog?) {
                                super.onNegative(dialog)
                                dialog!!.dismiss()
                            }
                        }).show()
            }
        }

        volunteer_time_title.setOnClickListener {
            if (volunteer_time_tab.visibility == View.VISIBLE) {
                volunteer_time_tab.visibility = View.GONE
                volunteer_unshow_image.visibility = View.VISIBLE
//                volunteer_unshow_image.visibility = View.GONE
            } else if (volunteer_time_tab.visibility == View.GONE) {
                volunteer_time_tab.visibility = View.VISIBLE
                volunteer_unshow_image.visibility = View.GONE
//                volunteer_unshow_image.visibility = View.VISIBLE
            }
        }

        volunteer_time_back.setOnClickListener { finish() }

        volunteer_show_image.setOnClickListener {
            if (volunteer_time_tab.visibility == View.VISIBLE) {
                volunteer_time_tab.visibility = View.GONE
                volunteer_unshow_image.visibility = View.VISIBLE
//                volunteer_unshow_image.visibility = View.GONE
            } else if (volunteer_time_tab.visibility == View.GONE) {
                volunteer_time_tab.visibility = View.VISIBLE
                volunteer_unshow_image.visibility = View.GONE
//                volunteer_unshow_image.visibility = View.VISIBLE
            }
        }

        volunteer_unshow_image.setOnClickListener {
            if (volunteer_time_tab.visibility == View.VISIBLE) {
                volunteer_time_tab.visibility = View.GONE
                volunteer_unshow_image.visibility = View.VISIBLE
//                volunteer_unshow_image.visibility = View.GONE
            } else if (volunteer_time_tab.visibility == View.GONE) {
                volunteer_time_tab.visibility = View.VISIBLE
                volunteer_unshow_image.visibility = View.GONE
//                volunteer_unshow_image.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 判断用户是否绑定了账号，如果绑定，则请求接口面，没有则返回至登录界面
     */
    private fun initData() {
        //在shearedPreference里获取用户绑定的数据
        volunteerSP = VolunteerTimeSP(this)
        uid = volunteerSP.volunteerUid
        account = volunteerSP.volunteerAccount
        password = volunteerSP.volunteerPassword
        animationDrawable = volunteer_refresh.drawable as AnimationDrawable

        if (uid == "404" || volunteerSP.volunteerAccount == "404" ||
                volunteerSP.volunteerPassword == "404") {
            //没有绑定数据，先登录
            Toast.makeText(this, "请先登录绑定账号哦", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, VolunteerLoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            //绑定数据，直接请求接口
            animationDrawable.start()
            loadVolunteerTime(EncryptPassword.encrypt(uid))
        }
    }

    /**
     * 请求用户时长具体信息接口，并将数据处理
     *
     * @param uid 用户学号
     */
    private fun loadVolunteerTime(uid: String) {
        val apiService = ApiGenerator.getApiService(ApiService::class.java)
        fragmentList = ArrayList()
        apiService.getVolunteerRecord("Basic enNjeTpyZWRyb2Nrenk=", uid)
                .setSchedulers()
                .safeSubscribeBy(onNext = { volunteerTime: VolunteerTime ->
                    allYearList = mutableListOf()
                    //请求加载动画停止，并设置不可见
                    animationDrawable.stop()
                    volunteer_refresh.visibility = View.GONE
                    //进行UI显示处理
                    runOnUiThread {
                        initializeYears()
                        initFragmentList(volunteerTime)
                        setTabLayout()
                    }
                }, onError = {
                    LogUtils.d("volunteer", it.toString())
                })
    }

    /**
     * 适配TabLayout
     */
    private fun setTabLayout() {
        adapter = VolunteerFragmentAdapter(supportFragmentManager, fragmentList, yearList)
        volunteer_view_pager.adapter = adapter
        volunteer_time_tab.setupWithViewPager(volunteer_view_pager)
        volunteer_time_tab.tabMode = TabLayout.MODE_FIXED
        volunteer_time_tab!!.addOnTabSelectedListener(this)
    }

    /**
     * 处理请求到的每条时长信息的时间（年份）
     * 初始化显示在TabLayout上的年份标签（全部，今年，去年，前年， 大前年）
     * 一共有全部（指该此时的年份再加上之前的三年） + 四年
     */
    private fun initializeYears() {
        yearList = mutableListOf()
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        yearList.add("全部")
        for (i in 0..3) {
            yearList.add((year - i).toString() + "")
        }
    }


    /**
     * 初始化FragmentList，将每一年的数据传入fragment：
     * 如果该年份无时长记录，传入无时长fragment；若有时长记录传入，有时长对应的fragment
     *
     * @param dataBean 接口请求到的用户时长数据
     */
    private fun initFragmentList(dataBean: VolunteerTime?) {
        if (dataBean!!.record!!.isEmpty()) {
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
        //每个key都对应的该用户key所对应的那一年的所有时长信息
        yearMap = TreeMap()
        //用来加入每一年的信息
        firstYear = mutableListOf()
        secondYear = mutableListOf()
        thirdYear = mutableListOf()
        lastYear = mutableListOf()

        //用来储存年份
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


    override fun onTabSelected(tab: TabLayout.Tab) {
        volunteer_view_pager.currentItem = tab.position

        if (volunteer_time_toolbar != null) {
            volunteer_time_title.text = yearList[tab.position]
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {

    }

    override fun onTabReselected(tab: TabLayout.Tab) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (volunteer_time_toolbar != null)
            volunteer_time_title.text = yearList[position]
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}
