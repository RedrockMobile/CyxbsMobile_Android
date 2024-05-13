package com.mredrock.cyxbs.ufield.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.helper.SoftKeyBoardListener
import com.mredrock.cyxbs.ufield.ui.fragment.searchfragment.AllSearchFragment
import com.mredrock.cyxbs.ufield.ui.fragment.searchfragment.CultureSearchFragment
import com.mredrock.cyxbs.ufield.ui.fragment.searchfragment.EducationSearchFragment
import com.mredrock.cyxbs.ufield.ui.fragment.searchfragment.SportsSearchFragment
import com.mredrock.cyxbs.ufield.viewmodel.SearchViewModel


/**
 * description ：负责搜索功能的activity
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/8 16:34
 * version: 1.0
 */
class SearchActivity : BaseActivity() {

    private val mTabLayout: TabLayout by R.id.uField_search_tabLayout.view()
    private val mVp: ViewPager2 by R.id.uField_search_viewpager2.view()
    private val mSearch: androidx.appcompat.widget.SearchView by R.id.uField_searchView.view()
    private val mCardView: CardView by R.id.uField_cardView.view()
    private val mBack: ImageView by R.id.uField_back_search.view()
    private val mSearchText: TextView by R.id.uField_search_text.view()


    private val mViewModel by viewModels<SearchViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ufield_activity_search)
        iniTab()
        iniSearch()
        iniClick()
        iniSoftKeyBoard()


    }

    /**
     * 初始化tabLayout
     */
    private fun iniTab() {
        mVp.adapter = FragmentVpAdapter(this)
            .add { AllSearchFragment() }
            .add { CultureSearchFragment() }
            .add { SportsSearchFragment() }
            .add { EducationSearchFragment() }
        TabLayoutMediator(mTabLayout, mVp) { tab, position ->
            when (position) {
                0 -> tab.text = "全部活动"
                1 -> tab.text = "文娱活动"
                2 -> tab.text = "体育活动"
                else -> tab.text = "教育活动"
            }
        }.attach()
        //让初始化的第一个先变色
        (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(0)
            .setBackgroundResource(R.drawable.ufield_ic_tab_shape_selected)
        //设置其他三个icon颜色
        for (i in 1 until mTabLayout.tabCount) {
            val tab = (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            tab.setBackgroundResource(R.drawable.ufield_ic_tab_shape)
        }
        for (i in 0 until mTabLayout.tabCount) {
            val tab = (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val params = tab.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(10, params.topMargin, 10, params.bottomMargin)
            tab.requestLayout()
        }
        /**
         * 实现滑动到特定区域和没有的颜色区分
         */
        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.ufield_ic_tab_shape_selected)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.ufield_ic_tab_shape)

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun iniSearch() {

        mSearch.apply {
            findViewById<EditText>(androidx.appcompat.R.id.search_src_text).apply {
                setTextColor(getColor(com.mredrock.cyxbs.config.R.color.config_level_three_font_color))
                setHintTextColor(getColor(R.color.uField_activity_style_color))
                textSize = 16F
                (this.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(28.dp2px, 0, 0, 0)
                }
            }
            //把SearchView输入文字后自带的删除图标设置为null
            findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn).apply {
                setImageDrawable(null)
            }
            queryHint = "点我开始搜索吧"
            setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (query.isNotEmpty()) {
                        mViewModel.iniSearchList(query)
                        mSearch.clearFocus()
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
            setBackgroundColor(Color.TRANSPARENT)
        }
        mCardView.setOnClickListener {
            mSearch.isIconified = false
        }

    }

    private fun iniClick() {
        mBack.setOnClickListener { finish() }
        mSearchText.setOnClickListener {
            if (mSearch.query.isEmpty()) {
                toast("输入不能为空哦")
                mSearch.isIconified = false
            } else {
                mViewModel.iniSearchList(mSearch.query.toString())
                mSearch.clearFocus()
            }
        }
    }

    private fun iniSoftKeyBoard() {
        SoftKeyBoardListener.setListener(
            this@SearchActivity,
            object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                    //处理输入法打开的逻辑
                }

                override fun keyBoardHide(height: Int) {
                    //处理输入法关闭的逻辑
                    mSearch.clearFocus()
                }
            })
    }


}


