package com.mredrock.cyxbs.discover.pages.discover

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
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.DISCOVER_ENTRY
import com.mredrock.cyxbs.common.config.DISCOVER_NEWS
import com.mredrock.cyxbs.common.config.DISCOVER_NEWS_ITEM
import com.mredrock.cyxbs.common.config.MINE_CHECK_IN
import com.mredrock.cyxbs.common.event.CurrentDateInformationEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.electricity.IElectricityService
import com.mredrock.cyxbs.discover.pages.discover.adapter.DiscoverMoreFunctionRvAdapter
import com.mredrock.cyxbs.discover.utils.BannerAdapter
import com.mredrock.cyxbs.discover.utils.MoreFunctionProvider
import com.mredrock.cyxbs.volunteer.IVolunteerService
import kotlinx.android.synthetic.main.discover_home_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @author zixuan
 * 2019/11/20
 */

@Route(path = DISCOVER_ENTRY)
class DiscoverHomeFragment : BaseViewModelFragment<DiscoverHomeViewModel>(), EventBusLifecycleSubscriber {
    companion object {
        const val DISCOVER_FUNCTION_RV_STATE = "discover_function_rv_state"
    }

    override val viewModelClass: Class<DiscoverHomeViewModel> = DiscoverHomeViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discover_home_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            initFeeds()
        }
        initJwNews(vf_jwzx_detail, fl_discover_home_jwnews)
        initViewPager()
        viewModel.getRollInfo()
        iv_check_in.setOnSingleClickListener {
            context?.doIfLogin("签到") {
                ARouter.getInstance().build(MINE_CHECK_IN).navigation()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        initFunctions()
        viewModel.startSwitchViewPager()
        if (viewModel.functionRvState != null) {
            rv_discover_more_function.layoutManager?.onRestoreInstanceState(viewModel.functionRvState)
        }

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
        vp_discover_home?.adapter = context?.let { BannerAdapter(it, vp_discover_home) }
        viewModel.viewPagerInfo.observe {
            if (it != null && context != null) {
                (vp_discover_home?.adapter as BannerAdapter).apply {
                    urlList.clear()
                    urlList.addAll(it)
                    notifyDataSetChanged()
                }
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
                vf_jwzx_detail.startFlipping()
            }
        }

        viewFlipper.setOnSingleClickListener {
            ARouter.getInstance().build(DISCOVER_NEWS_ITEM).withString("id", viewFlipper.focusedChild.tag as String).navigation()
        }

        viewFlipper.setFlipInterval(6555)
        viewFlipper.setInAnimation(context, R.anim.discover_text_in_anim)
        viewFlipper.setOutAnimation(context, R.anim.discover_text_out_anim)
        viewModel.getJwNews(1)

        frameLayout.setOnSingleClickListener {
            ARouter.getInstance().build(DISCOVER_NEWS).navigation()
        }
    }

    private fun getTextView(info: String, id: String): TextView {
        return TextView(context).apply {
            text = info
            maxLines = 1
            overScrollMode = OVER_SCROLL_IF_CONTENT_SCROLLS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextColor(context.resources.getColor(R.color.common_menu_font_color_found, context.theme))
            } else {
                setTextColor(ContextCompat.getColor(context, R.color.common_menu_font_color_found))
            }
            textSize = 15f
            setOnSingleClickListener {
                ARouter.getInstance().build(DISCOVER_NEWS_ITEM).withString("id", id).navigation()
            }
        }
    }

    //加载发现首页中跳转按钮
    private fun initFunctions() {
        val functions = MoreFunctionProvider.functions
        val picUrls = functions.map { it.resource }
        val texts = functions.map { getString(it.title) }
        rv_discover_more_function.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = DiscoverMoreFunctionRvAdapter(picUrls, texts) {
                if (it == functions.size - 1) {
                    CyxbsToast.makeText(context, R.string.discover_more_function_notice_text, Toast.LENGTH_SHORT).show()
                } else {
                    functions[it].activityStarter.startActivity(context)
                }
            }
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val offset = computeHorizontalScrollOffset().toFloat()
                    val range = computeHorizontalScrollRange().toFloat()
                    val extent = computeHorizontalScrollExtent().toFloat()
                    indicator_view_discover.doMove(offset / (range - extent))
                }
            })
        }
    }

    private fun initFeeds() {
        addFeedFragment(ServiceManager.getService(IElectricityService::class.java).getElectricityFeed())
        addFeedFragment(ServiceManager.getService(IVolunteerService::class.java).getVolunteerFeed())
        //处理手机屏幕过长导致feed无法填充满下方的情况
        ll_discover_feeds.post {
            context?.let {
                val point = Point()
                (it.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(point)
                ll_discover_feeds.minimumHeight = point.y - ll_discover_feeds.top
            }

        }

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
        viewModel.stopPageTurner()
        viewModel.functionRvState = rv_discover_more_function.layoutManager?.onSaveInstanceState()
    }

}
