package com.mredrock.cyxbs.mine.page.mine.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.account.IUserService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.adapter.FanPagerAdapter
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.FollowFragment
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.FanFragment
import kotlinx.android.synthetic.main.mine_activity_fan.*

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FanActivity : BaseActivity() {
    private var userId = 0;

    private val userService: IUserService by lazy {
        ServiceManager.getService(IAccountService::class.java).getUserService()
    }

    companion object {
        fun activityStart(activity: Activity, userId: Int) {
            activity.startActivity(Intent(activity, FanActivity::class.java).apply {
                putExtra("userId", userId)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_fan)
        userId = intent.getIntExtra("userId", 0)
        initPager()
    }

    private fun initPager() {
        mine_fan_vp2.apply {
            adapter = FanPagerAdapter(this@FanActivity).apply {
                addFragment(FanFragment())
                addFragment(FollowFragment())
            }

            //viewpager切换动画
            setPageTransformer { view, position ->
                if (position < -1 || position > 1) {
                    view.alpha = 0f
                } else if (position > 0) {
                    view.apply {
                        scaleX = 0.4f * (position - 1 / 2f) * (position - 1 / 2f) + 0.9f
                        scaleY = 0.4f * (position - 1 / 2f) * (position - 1 / 2f) + 0.9f
                    }
                } else {
                    view.apply {
                        scaleX = 0.4f * (-position - 1 / 2f) * (-position - 1 / 2f) + 0.9f
                        scaleY = 0.4f * (-position - 1 / 2f) * (-position - 1 / 2f) + 0.9f
                    }
                }
            }
        }

        TabLayoutMediator(mine_fan_tl, mine_fan_vp2) { tab, position ->
//            when (position) {
//                0 -> tab.text =
//                    if (userId == userService.getUid()) "我的粉丝"
//                    else "Ta的粉丝"
//                1 -> tab.text =
//                    if (userId == userService.getUid()) "我的关注"
//                    else "Ta的关注"
//            }
        }.attach()

    }
}