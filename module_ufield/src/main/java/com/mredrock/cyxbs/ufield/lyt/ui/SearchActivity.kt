package com.mredrock.cyxbs.ufield.lyt.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.room.util.query
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment.AllSearchFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment.CultureSearchFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment.EducationSearchFragment
import com.mredrock.cyxbs.ufield.lyt.fragment.searchfragment.SportsSearchFragment
import com.mredrock.cyxbs.ufield.lyt.ui.helper.SoftKeyBoardListener
import com.mredrock.cyxbs.ufield.lyt.viewmodel.ui.SearchViewModel
import com.mredrock.cyxbs.ufield.lyt.viewmodel.ui.UFieldViewModel


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
    private val mSearchText: ImageView by R.id.uField_search_text.view()


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

    /**
     * 监听搜索框,初始化搜索框
     */
    private fun iniSearch() {
        mSearch.queryHint="点我开始搜索吧"
        val mSearchEditText =
            mSearch.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        mSearchEditText.textSize = 16F
        mSearchEditText.setTextColor(Color.parseColor("#A1ADBD"))


        mCardView.setOnClickListener {
            mSearch.isIconified = false
        }

        mSearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mViewModel.iniSearchList(query)
                mSearch.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        })
        mSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (!imm.isAcceptingText) { // 如果软键盘已经关闭
                    mSearch.clearFocus() // 清除SearchView的焦点
                }
            }
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
                    //  Toast.makeText(this@SearchActivity, "输入法打开", Toast.LENGTH_SHORT).show()
                }

                override fun keyBoardHide(height: Int) {
                    //处理输入法关闭的逻辑
                    mSearch.clearFocus()
                    // Toast.makeText(this@SearchActivity, "输入法关闭", Toast.LENGTH_SHORT).show()
                }
            })
    }

}


