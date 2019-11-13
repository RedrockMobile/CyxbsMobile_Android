package com.mredrock.cyxbs.discover.pages.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.DISCOVER_ENTRY
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.utils.BannerAdapter
import kotlinx.android.synthetic.main.discover_home_fragment.*


@Route(path = DISCOVER_ENTRY)
class DiscoverHomeFragment : BaseViewModelFragment<DiscoverHomeViewModel>() {

    override val viewModelClass: Class<DiscoverHomeViewModel> = DiscoverHomeViewModel::class.java


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.discover_home_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initViewPager()
        viewModel.getRollInfos()
        discover_framelayout.setOnClickListener {
            vp_discover_home.setCurrentItem(1,true)
        }
        super.onActivityCreated(savedInstanceState)

    }
    private fun initViewPager(){
//        discover_framelayout.setOnClickListener {
//            vp_discover_home.setCurrentItem(1,true)
//        }
        vp_discover_home.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                vp_discover_home.adapter?.notifyDataSetChanged()
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })


        viewModel.viewPagerInfos.observe{
            if(it != null && context!= null){

                vp_discover_home?.adapter = BannerAdapter(context!!,it,vp_discover_home)

            }

        }
    }

}
