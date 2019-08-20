package com.mredrock.cyxbs.freshman.view.activity

import android.os.Bundle
import android.view.View
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseActivity
import com.mredrock.cyxbs.freshman.bean.DormitoryAndCanteenText
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityCampusGuidelinesModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityCampusGuidelinesPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityCampusGuidelinesView
import com.mredrock.cyxbs.freshman.presenter.ActivityCampusGuidelinesPresenter
import com.mredrock.cyxbs.freshman.view.widget.CustomTabLayout
import com.mredrock.cyxbs.freshman.view.adapter.CampusGuidelinesPagerAdapter
import com.mredrock.cyxbs.freshman.view.fragment.CampusGuidelinesFragment
import com.mredrock.cyxbs.freshman.view.fragment.DataDisclosureFragment
import com.mredrock.cyxbs.freshman.view.fragment.ExpressFragment
import kotlinx.android.synthetic.main.freshman_activity_campus_guidelines.*
import kotlinx.android.synthetic.main.freshman_custom_tab_layout.*

/**
 * Create by yuanbing
 * on 2019/8/2
 * 学校指引
 */
class CampusGuidelinesActivity :
        BaseActivity<IActivityCampusGuidelinesView, IActivityCampusGuidelinesPresenter,
                IActivityCampusGuidelinesModel>(), IActivityCampusGuidelinesView {
    override val isFragmentActivity: Boolean
        get() = true
    private lateinit var mAdapter: CampusGuidelinesPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_campus_guidelines)

        initToolbar()
        initTabLayout()
        initViewPager()

        presenter?.getDormitoryAndCanteen()
    }

    private fun initViewPager() {
        mAdapter = CampusGuidelinesPagerAdapter(supportFragmentManager)
        vp_campus_guidelines.adapter = mAdapter
    }

    private fun initTabLayout() {
        val tabManager = CustomTabLayout(cl_custom_tab_layout)
        tabManager.addOnTabSelectedListener {
            vp_campus_guidelines.currentItem = it
        }
    }

    private fun initToolbar() {
        common_toolbar.init(
                title = resources.getString(R.string.freshman_campus_guidelines),
                listener = View.OnClickListener { finish() }
        )
    }

    override fun getViewToAttach() = this

    override fun createPresenter() = ActivityCampusGuidelinesPresenter()

    override fun showDormitoryAndCanteen(dormitoryAndCanteenText: List<DormitoryAndCanteenText>) {
        val fragments = listOf(
                CampusGuidelinesFragment(dormitoryAndCanteenText[0]),
                CampusGuidelinesFragment(dormitoryAndCanteenText[1]),
                ExpressFragment(),
                DataDisclosureFragment()
        )
        mAdapter.refreshData(fragments)
    }
}