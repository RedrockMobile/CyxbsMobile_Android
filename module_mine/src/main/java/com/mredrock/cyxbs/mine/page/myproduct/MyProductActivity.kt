package com.mredrock.cyxbs.mine.page.myproduct

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.ui.TabPagerAdapter
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*


class MyProductActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setColor(window, ContextCompat.getColor(this, R.color.common_grades_fragment_text_color_black))
        setContentView(R.layout.mine_activity_tablayout_my_product)
        common_toolbar.initWithSplitLine("", false, R.drawable.mine_ic_arrow_left_my_product)
        val fragmentList = listOf<Fragment>(MyProductFragment(MyProductFragment.UNCLAIMED), MyProductFragment(MyProductFragment.CLAIMED))
        val titleList = listOf("未领取", "已领取")
        init(fragmentList, titleList)
    }

    private fun init(fragmentList: List<Fragment>, titleList: List<String>) {

        //设置viewPager
        mine_tl_view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        mine_tl_view_pager.offscreenPageLimit = fragmentList.size

        mine_tl_tablayout.tabMode = TabLayout.MODE_FIXED
        mine_tl_tablayout.setupWithViewPager(mine_tl_view_pager)

    }

    //设置StatusBar颜色
    private fun setColor(window: Window, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = color
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.common_scale_fade_in_with_bezier, R.anim.common_slide_fade_out_to_bottom_with_bezier)
    }
}
