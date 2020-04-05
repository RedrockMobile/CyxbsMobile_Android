package com.mredrock.cyxbs.discover.pages.discover

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OVER_SCROLL_IF_CONTENT_SCROLLS
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.CurrentDateInformationEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.utils.BannerAdapter
import com.mredrock.cyxbs.discover.utils.MoreFunctionProvider
import kotlinx.android.synthetic.main.discover_home_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.displayManager
import org.jetbrains.anko.sp

/**
 * @author zixuan
 * 2019/11/20
 */

@Route(path = DISCOVER_ENTRY)
class DiscoverHomeFragment : BaseViewModelFragment<DiscoverHomeViewModel>() {
    override val viewModelClass: Class<DiscoverHomeViewModel> = DiscoverHomeViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discover_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null){
            initFeeds()
            viewModel.startSwitchViewPager()
        }
        initJwNews(vf_jwzx_detail, fl_discover_home_jwnews)

        initViewPager()
        viewModel.getRollInfos()
        iv_check_in.setOnClickListener {
            ARouter.getInstance().build(MINE_CHECK_IN).navigation()
        }
    }


    override fun onResume() {
        super.onResume()
        initFunctions()
        vf_jwzx_detail.startFlipping()
    }

    //加载轮播图
    private fun initViewPager() {
        vp_discover_home.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING)
                    vp_discover_home.adapter?.notifyDataSetChanged()
                super.onPageScrollStateChanged(state)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.scrollFlag = false
            }
        })
        viewModel.viewPagerInfos.observe {
            if (it != null && context != null) {
                vp_discover_home?.adapter = BannerAdapter(context!!, it, vp_discover_home)
            }
        }
        viewModel.viewPagerTurner.observe {
            if (viewModel.scrollFlag) {
                vp_discover_home.currentItem += 1
            }
            viewModel.scrollFlag = true
        }
    }

    private fun initJwNews(viewFlipper: ViewFlipper, frameLayout: FrameLayout) {
        viewModel.jwNews.observe {
            if (it != null) {
                viewFlipper.removeAllViews()
                for (item in it) {
                    viewFlipper.addView(getTextView(item.title, item.id))
                }
            }
        }

        viewFlipper.setOnClickListener {
            ARouter.getInstance().build(DISCOVER_NEWS_ITEM).withString("id", viewFlipper.focusedChild.tag as String).navigation()
        }

        viewFlipper.setFlipInterval(6555)
        viewFlipper.setInAnimation(context, R.anim.discover_text_in_anim)
        viewFlipper.setOutAnimation(context, R.anim.discover_text_out_anim)
        viewModel.getJwNews(1)

        frameLayout.setOnClickListener {
            ARouter.getInstance().build(DISCOVER_NEWS).navigation()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun getTextView(info: String, id: String): TextView {
        return TextView(context).apply {
            text = info
            maxLines = 1
            overScrollMode = OVER_SCROLL_IF_CONTENT_SCROLLS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextColor(context.resources.getColor(R.color.menuFontColorFound, context.theme))
            } else {
                setTextColor(ContextCompat.getColor(context,R.color.menuFontColorFound))
            }
            textSize = 15f
//            textColor = R.color.menuFontColorFound
            setOnClickListener {
                ARouter.getInstance().build(DISCOVER_NEWS_ITEM).withString("id", id).navigation()
            }
//            typeface = Typeface.DEFAULT_BOLD
        }
    }

    //加载发现首页中跳转按钮
    private fun initFunctions() {
        val functions = MoreFunctionProvider.getHomePageFunctions()
        val imageViewList = mutableListOf<AppCompatImageView>(iv_discover_1, iv_discover_2, iv_discover_3, iv_discover_4)
        val textViewList = mutableListOf<AppCompatTextView>(tv_discover_1, tv_discover_2, tv_discover_3, tv_discover_4)
        for ((index, imageView) in imageViewList.withIndex()) {
            imageView.setImageResource(functions[index].resource)
            imageView.setOnClickListener {
                functions[index].activityStarter.startActivity()
            }
        }
        for ((index, textView) in textViewList.withIndex()) {
            textView.text = context?.getText(functions[index].title)
            textView.setOnClickListener {
                functions[index].activityStarter.startActivity()
            }
        }
    }

    private fun initFeeds() {
        addFeedByRoute(DISCOVER_ELECTRICITY_FEED)
        addFeedByRoute(DISCOVER_VOLUNTEER_FEED)
        //处理手机屏幕过长导致feed无法填充满下方的情况
        ll_discover_feeds.post {
            val point = Point()
            (context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(point)
            ll_discover_feeds.minimumHeight = point.y - ll_discover_feeds.top
        }

    }

    private fun addFeedByRoute(route: String) {
        addFeedFragment(ARouter.getInstance().build(route).navigation() as Fragment)
    }

    private fun addFeedFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().add(R.id.ll_discover_feeds, fragment).commit()

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onGetData(dataEvent: CurrentDateInformationEvent) {
        tv_day.text = dataEvent.time
    }

    override fun onPause() {
        super.onPause()
        vf_jwzx_detail.stopFlipping()
    }

}
