package com.mredrock.cyxbs.discover.pages.hiding

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.discover.KEY_SP_HIDING_DISCOVER_UPDATE
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.SP_HIDING_DISCOVER
import kotlinx.android.synthetic.main.discover_rv_hiding_item.view.*
import org.jetbrains.anko.imageResource

/**
 * Created by zxzhu
 *   2018/9/16.
 *   enjoy it !!
 */
class DiscoverHidingRvAdapter(val mHideList: MutableList<Boolean>) : RecyclerView.Adapter<DiscoverHidingRvAdapter.HidingRvHolder>() {

    private val iconResIds = mutableListOf(
            R.drawable.ic_discover_no_course, R.drawable.ic_discover_empty_room, R.drawable.ic_discover_grade,
            R.drawable.ic_discover_volunteer_time, R.drawable.ic_discover_map, R.drawable.ic_discover_school_car,
            R.drawable.ic_discover_calendar, R.drawable.ic_discover_electric, R.drawable.ic_discover_about,
            R.drawable.ic_discover_news, R.drawable.ic_discover_stu_schedule
    )

    private val titleRes = mutableListOf(
            "没课约", "空教室", "成绩单",
            "志愿时长", "重邮地图", "校车查询",
            "校历", "查电费", "关于红岩", "教务新闻", "同学课表"
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HidingRvHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.discover_rv_hiding_item, parent, false)
        return HidingRvHolder(view)
    }

    override fun getItemCount() = titleRes.size

    override fun onBindViewHolder(holder: HidingRvHolder, position: Int) {
        holder.view.discover_tx_hiding_item.text = titleRes[position]
        holder.view.discover_icon_hiding_item.imageResource = iconResIds[position]
        holder.view.discover_switch_hiding_item.isChecked = mHideList[position]
        holder.view.discover_switch_hiding_item.setOnCheckedChangeListener { _, isChecked ->
            mHideList[position] = isChecked
            holder.view.context.sharedPreferences(SP_HIDING_DISCOVER).editor {
                putBoolean(KEY_SP_HIDING_DISCOVER_UPDATE, true)
                commit()
            }
        }
    }


    class HidingRvHolder(val view: View) : RecyclerView.ViewHolder(view)
}