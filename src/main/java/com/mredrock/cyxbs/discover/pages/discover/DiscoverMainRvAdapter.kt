package com.mredrock.cyxbs.discover.pages.discover

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.discover.R
import kotlinx.android.synthetic.main.discover_rv_item.view.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.imageResource

/**
 * Created by zxzhu
 *   2018/9/15.
 *   enjoy it !!
 */
class DiscoverMainRvAdapter(private val context: Context): RecyclerView.Adapter<DiscoverMainRvAdapter.MyViewHolder>() {
    private var mListHide: List<Boolean>? = null
    private var iconResIds = mutableListOf<Int>()
    private var titleRes = mutableListOf<String>()

    private fun initLists() {
        titleRes = mutableListOf(
                "没课约", "空教室", "成绩单",
                "志愿时长", "重邮地图", "校车查询",
                "校历", "查电费", "关于红岩",
                "教务新闻", "同学课表")
        iconResIds = mutableListOf(
                R.drawable.ic_discover_no_course, R.drawable.ic_discover_empty_room, R.drawable.ic_discover_grade,
                R.drawable.ic_discover_volunteer_time, R.drawable.ic_discover_map, R.drawable.ic_discover_school_car,
                R.drawable.ic_discover_calendar, R.drawable.ic_discover_electric, R.drawable.ic_discover_about,
                R.drawable.ic_discover_news, R.drawable.ic_discover_stu_schedule
        )
    }

    private fun startActivity(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

    private fun startActivityAfterLogin(str: String, path: String) {
        checkLoginBeforeAction (str) { startActivity(path) }
    }

    private fun onClick(iconPosition: Int) {
        when (iconPosition) {
            R.drawable.ic_discover_no_course -> startActivityAfterLogin("没课约", DISCOVER_NO_CLASS)
            R.drawable.ic_discover_empty_room -> startActivity(DISCOVER_EMPTY_ROOM)
            R.drawable.ic_discover_grade -> startActivityAfterLogin("成绩查询", DISCOVER_GRADES)
            R.drawable.ic_discover_volunteer_time -> startActivity(DISCOVER_VOLUNTEER)
            R.drawable.ic_discover_map -> startActivity(DISCOVER_MAP)
            R.drawable.ic_discover_school_car -> startActivity(DISCOVER_SCHOOL_CAR)
            R.drawable.ic_discover_calendar -> startActivity(DISCOVER_CALENDER)
            R.drawable.ic_discover_electric -> startActivity(DISCOVER_ELECTRICITY)
            R.drawable.ic_discover_about -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wx.redrock.team/app/")))
            R.drawable.ic_discover_news -> startActivity(DISCOVER_NEWS)
            R.drawable.ic_discover_stu_schedule -> startActivity(DISCOVER_OTHER_COURSE)
        }
    }

    private fun checkLoginBeforeAction(msg: String, action: () -> Unit) {
        if (BaseApp.isLogin) {
            action.invoke()
        } else {
            EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用${msg}哦~"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.discover_rv_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount() = titleRes.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.discover_grid_item_title.text = titleRes[position]
        holder.itemView.discover_grid_item_icon.imageResource = iconResIds[position]
        holder.itemView.setOnClickListener { onClick(iconResIds[position]) }
    }

    fun refreshData(mListHide: MutableList<Boolean>) {
        this.mListHide = mListHide
        initLists()
        for (i in 10 downTo 0) {
            if (!mListHide[i]) {
                iconResIds.removeAt(i)
                titleRes.removeAt(i)
            }
        }
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}