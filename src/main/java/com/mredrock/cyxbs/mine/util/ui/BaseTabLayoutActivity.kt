package com.mredrock.cyxbs.mine.util.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*
import kotlinx.android.synthetic.main.mine_item_tablayout_with_point.view.*
import kotlin.math.sqrt

/**
 * Created by roger on 2019/12/3
 */
abstract class BaseTabLayoutActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_tablayout_common)
        mine_tl_toolbar.init("")
    }



    fun init(fragmentList: List<Fragment>, titleList: List<String>) {

        //设置viewPager
        mine_tl_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        mine_tl_view_pager.offscreenPageLimit = fragmentList.size

        mine_tl_tablayout.tabMode = TabLayout.MODE_FIXED
        mine_tl_tablayout.setupWithViewPager(mine_tl_view_pager)

        //设置tabLayout，隐藏小红点
        for (i in 0 until mine_tl_tablayout.tabCount) {
            val view = LayoutInflater.from(this).inflate(R.layout.mine_item_tablayout_with_point, null)
            view.mine_item_tv_tab_title.text = titleList[i]
            view.mine_item_iv_tab_red.visibility = View.GONE
            mine_tl_tablayout.getTabAt(i)!!.customView = view
        }
        dealWithIndicator()
        addTabListener()

    }

    //改变tablayout的title在选中状态和未选中状态下的alpha值
    private fun addTabListener() {
        mine_tl_tablayout.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(mine_tl_view_pager) {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                super.onTabSelected(tab)
                tab?.customView?.findViewById<TextView>(R.id.mine_item_tv_tab_title)?.alpha = 1f
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                super.onTabUnselected(tab)
                tab?.customView?.findViewById<TextView>(R.id.mine_item_tv_tab_title)?.alpha = 0.49f
            }
        })
    }


    //处理tablayout的indicator的问题
    private fun dealWithIndicator() {
        mine_tl_view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val frameLayoutParams = mine_tl_indicator.layoutParams as FrameLayout.LayoutParams
                val tabLeft = mine_tl_tablayout.getTabAt(position)?.customView?.findViewById<TextView>(R.id.mine_item_tv_tab_title)
                val tabRight = mine_tl_tablayout.getTabAt(position + 1)?.customView?.findViewById<TextView>(R.id.mine_item_tv_tab_title)
                if (tabLeft != null && tabRight != null) {
                    val width = getAbsoluteRightEdge(position, positionOffset, tabLeft, tabRight) - getAbsoluteLeftEdge(position, positionOffset)
                    frameLayoutParams.leftMargin = getAbsoluteLeftEdge(position, positionOffset)
                    frameLayoutParams.width = width
                }
                mine_tl_indicator.requestLayout()
            }
        })
    }

    //计算出tablayout中的R.id.mine_item_tv_tab_title这个textview相对tablayout所在父布局的位置
    private fun getAbsoluteStartX(i: Int): Int {
        val delta1 = mine_tl_tablayout.x.toInt()
        val delta2 = mine_tl_tablayout.getChildAt(0).x
        val delta3 = (mine_tl_tablayout.getChildAt(0) as ViewGroup).getChildAt(i).x
        val delta4 = mine_tl_tablayout.getTabAt(i)?.customView?.x?.toInt() ?: 0
        val delta5 = mine_tl_tablayout.getTabAt(i)?.customView?.findViewById<TextView>(R.id.mine_item_tv_tab_title)?.x?.toInt()
                ?: 0
        return (delta1 + delta2 + delta3 + delta4 + delta5).toInt()
    }

    /**
     * 获取indicator这个view通过计算得到的在父坐标的左边界的位置
     * @param position indicator的左边的tab是tablayout的第几个tab
     * @param percent indicator的左边的tab到右边的tab的滑动的完成度。范围在(0,1)
     */
    private fun getAbsoluteLeftEdge(position: Int, percent: Float): Int {
        return (getAbsoluteStartX(position) + (getAbsoluteStartX(position + 1) - getAbsoluteStartX(position)) * percent * percent).toInt()
    }

    /**
     * 获取indicator这个view通过计算得到的在父坐标的右边界的位置
     * @param position indicator的左边的tab是tablayout的第几个tab
     * @param percent indicator的左边的tab到右边的tab的滑动的完成度。范围在(0,1)
     */
    private fun getAbsoluteRightEdge(position: Int, percent: Float, tabLeft: TextView, tabRight: TextView): Int {
        val part1 = tabLeft.width + getAbsoluteStartX(position)
        val part2 = tabRight.width + getAbsoluteStartX(position + 1) - tabLeft.width - getAbsoluteStartX(position)
        return (part1 + part2 * sqrt(percent)).toInt()
    }

}