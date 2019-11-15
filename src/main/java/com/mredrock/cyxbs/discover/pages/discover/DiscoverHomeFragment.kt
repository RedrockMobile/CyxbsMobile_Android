package com.mredrock.cyxbs.discover.pages.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OVER_SCROLL_IF_CONTENT_SCROLLS
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_ENTRY
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.utils.BannerAdapter
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
        super.onActivityCreated(savedInstanceState)

    }
    private fun initViewPager(viewPager2: ViewPager2){
        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                vp_discover_home.adapter?.notifyDataSetChanged()
                super.onPageScrollStateChanged(state)
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.scrollFlag = false
            }
        })
        viewModel.startSwitchViewPager {
            if(viewModel.scrollFlag){
                viewPager2.currentItem += 1
            }
            viewModel.scrollFlag = true
        }

        viewModel.viewPagerInfos.observe{
            if(it != null && context!= null){
                vp_discover_home?.adapter = BannerAdapter(context!!,it,vp_discover_home)
            }

        }
    }
    private fun initViewFlipper(viewFlipper: ViewFlipper?){
        viewModel.jwNews.observe {
            if (it != null) {
                viewFlipper?.removeAllViews()
                for(item in it){
                    viewFlipper?.addView(getTextView(item.title))
                }
            }
        }
        viewFlipper?.setFlipInterval(6555)
        viewFlipper?.setInAnimation(context,R.anim.discover_text_in_anim)
        viewFlipper?.setOutAnimation(context,R.anim.discover_text_out_anim)
        viewFlipper?.startFlipping()
        viewModel.getJwNews(1)
    }
    private fun getTextView(info:String):TextView{
        return TextView(context).apply {
            text = info
            maxLines = 1
            overScrollMode = OVER_SCROLL_IF_CONTENT_SCROLLS
            textColor = R.color.levelTwoFontColor
//            typeface = Typeface.DEFAULT_BOLD
        }
    }


}
