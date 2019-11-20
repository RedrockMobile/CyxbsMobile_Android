package com.mredrock.cyxbs.discover.pages.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OVER_SCROLL_IF_CONTENT_SCROLLS
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.DISCOVER_ENTRY
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.utils.BannerAdapter
import com.mredrock.cyxbs.discover.utils.MoreFunctionProvider
import kotlinx.android.synthetic.main.discover_home_fragment.*
import org.jetbrains.anko.textColor


@Route(path = DISCOVER_ENTRY)
class DiscoverHomeFragment : BaseViewModelFragment<DiscoverHomeViewModel>() {
    //标记是否未经被滑动，被滑动就取消下一次自动滚动

    override val viewModelClass: Class<DiscoverHomeViewModel> = DiscoverHomeViewModel::class.java


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discover_home_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initViewPager(vp_discover_home)
        initViewFlipper(vf_jwzx_detail)
        viewModel.getRollInfos()
        initFunctions()
        super.onActivityCreated(savedInstanceState)

    }

    private fun initViewPager(viewPager2: ViewPager2) {
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                vp_discover_home.adapter?.notifyDataSetChanged()
                super.onPageScrollStateChanged(state)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.scrollFlag = false
            }
        })
        viewModel.startSwitchViewPager {
            if (viewModel.scrollFlag) {
                viewPager2.currentItem += 1
            }
            viewModel.scrollFlag = true
        }

        viewModel.viewPagerInfos.observe {
            if (it != null && context != null) {
                vp_discover_home?.adapter = BannerAdapter(context!!, it, vp_discover_home)
            }

        }
    }

    private fun initViewFlipper(viewFlipper: ViewFlipper?) {
        viewModel.jwNews.observe {
            if (it != null) {
                viewFlipper?.removeAllViews()
                for (item in it) {
                    viewFlipper?.addView(getTextView(item.title))
                }
            }
        }
        viewFlipper?.setFlipInterval(6555)
        viewFlipper?.setInAnimation(context, R.anim.discover_text_in_anim)
        viewFlipper?.setOutAnimation(context, R.anim.discover_text_out_anim)
        viewFlipper?.startFlipping()
        viewModel.getJwNews(1)
    }

    private fun getTextView(info: String): TextView {
        return TextView(context).apply {
            text = info
            maxLines = 1
            overScrollMode = OVER_SCROLL_IF_CONTENT_SCROLLS
            textColor = R.color.levelTwoFontColor
//            typeface = Typeface.DEFAULT_BOLD
        }
    }

    fun initFunctions() {
        val functions = context?.let {
            MoreFunctionProvider.getHomePageFunctions()
        }
        functions ?: return
        val imageViewList = mutableListOf<AppCompatImageView>(img_discover_1, img_discover_2, img_discover_3, img_discover_4)
        val textViewList = mutableListOf<AppCompatTextView>(tv_discover_1, tv_discover_2, tv_discover_3, tv_discover_4)
        for ((index, imageView) in imageViewList.withIndex()) {
            imageView.setImageResource(functions[index].resource)
            imageView.setOnClickListener {
                functions[index].startActivityAble.startActivity()
            }
        }
        for ((index, textView) in textViewList.withIndex()) {
            textView.text = context?.getText(functions[index].title)
        }

    }

}
