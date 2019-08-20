package com.mredrock.cyxbs.freshman.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseFragment
import com.mredrock.cyxbs.freshman.bean.ExpressText
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentExpressModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentExpressPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentExpressView
import com.mredrock.cyxbs.freshman.presenter.FragmentExpressPresenter
import com.mredrock.cyxbs.freshman.view.adapter.CampusGuidelinesPagerAdapter
import com.mredrock.cyxbs.freshman.view.widget.CustomSecondTabLayout

class ExpressFragment :
        BaseFragment<IFragmentExpressView, IFragmentExpressPresenter, IFragmentExpressModel>(),
        IFragmentExpressView {
    private lateinit var mTab: CustomSecondTabLayout
    private lateinit var mViewPager: ViewPager
    private lateinit var mData: List<ExpressText>
    private lateinit var mAdapter: CampusGuidelinesPagerAdapter
    private lateinit var mTabView: FrameLayout

    override fun onCreateView(view: View, savedInstanceState: Bundle?) {
        mTabView = view.findViewById(R.id.include_custom_second_tab_layout)
        mTab = CustomSecondTabLayout(view.findViewById(R.id.rv_custom_second_tab_layout))
        mTabView.gone()
        mViewPager = view.findViewById(R.id.vp_campus_guidelines_express)
        initViewPager()
        presenter?.getExpress()
    }

    override fun getLayoutRes() = R.layout.freshman_fragment_campus_guidelines_express

    override fun getViewToAttach() = this

    override fun createPresenter() = FragmentExpressPresenter()

    override fun showExpress(express: List<ExpressText>) {
        mData = express
        initTabLayout()
        mTabView.visible()
        mAdapter.refreshData(List(mData.size) { ExpressDetailFragment(mData[it]) })
    }

    private fun initTabLayout() {
        if (mData.size > 4) mTab.mTabMode = TabLayout.MODE_SCROLLABLE
        for (text in mData) {
            mTab.addTab(text.name)
        }
        mTab.commit()
        mTab.addOnTabSelectedListener { mViewPager.currentItem = it }
    }

    private fun initViewPager() {
        mAdapter = CampusGuidelinesPagerAdapter(childFragmentManager)
        mViewPager.adapter = mAdapter
        mViewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {  }
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {  }
                    override fun onPageSelected(position: Int) {
                        mTab.select(position)
                    }
                }
        )
    }
}