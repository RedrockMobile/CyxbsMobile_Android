package com.mredrock.cyxbs.mine.page.sign

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.util.user
import com.mredrock.cyxbs.mine.util.widget.SpaceDecoration
import com.mredrock.cyxbs.mine.util.widget.Stick
import kotlinx.android.synthetic.main.mine_activity_daily_sign.*
import kotlinx.android.synthetic.main.mine_layout_store_sign.*
import org.jetbrains.anko.toast


/**
 * Created by zzzia on 2018/8/14.
 * origin by jay86
 * 每日签到
 */
class DailySignActivity(override val viewModelClass: Class<DailyViewModel> = DailyViewModel::class.java
                        , override val isFragmentActivity: Boolean = false)
    : BaseViewModelActivity<DailyViewModel>() {

    private lateinit var dividerResArr: Array<Stick>
    private lateinit var imageViewResArr: Array<ImageView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_daily_sign)
        if (!isLogin()) return

        initView()
        dealBottomSheet()
        viewModel.loadAllData(user!!)
        viewModel.fakeStatus.postValue(arrayOf(1, 1, 1, 0, 1, 0, 0))
    }

    private fun initView() {
        dividerResArr = arrayOf(mine_daily_v_divider_mon_tue,
                mine_daily_v_divider_tue_wed,
                mine_daily_v_divider_wed_thurs,
                mine_daily_v_divider_thurs_fri,
                mine_daily_v_divider_fri_sat,
                mine_daily_v_divider_sat_sun)
        imageViewResArr = arrayOf(mine_daily_iv_mon,
                mine_daily_iv_tue,
                mine_daily_iv_wed,
                mine_daily_iv_thurs,
                mine_daily_iv_fri,
                mine_daily_iv_sat,
                mine_daily_iv_sun)
        val adapter = ProductAdapter()
        mine_store_rv.adapter = adapter
        val fakeItem = Product("redrock限量手环", 555, 13, "https://raw.githubusercontent.com/roger1245/ImgBed/master/img/rgview_2.jpg")
        val list = mutableListOf<Product>()
        for (i in 0..8) {
            list.add(fakeItem)
        }
        adapter.submitList(list)

        mine_store_rv.addItemDecoration(SpaceDecoration(dp2px(8f)))
        viewModel.fakeStatus.observe(this, Observer {
            freshSignView(it)
        })


    }

    /**
     * 刷新签到页面
     */
    @SuppressLint("SetTextI18n")
    private fun freshSignView(weekArr: Array<Int>) {
        changeWeekImageView(weekArr)
        val dividerArr = WeekGenerator.getDividerArr(weekArr)
        paintDivider(dividerArr)
        mine_daily_sign.setOnClickListener { checkIn() }
        moveBubble()
    }

    private fun checkIn() {
        viewModel.checkIn(user!!) { viewModel.loadAllData(user!!) }
        viewModel.fakeStatus.postValue(arrayOf(1, 1, 1, 0, 1, 1, 0))
    }

    private fun isLogin(): Boolean {
        if (!BaseApp.isLogin) {
            toast("还没有登录...")
        }
        return BaseApp.isLogin
    }

    private fun dealBottomSheet() {
        val behavior = BottomSheetBehavior.from(mine_sign_fl)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                mine_store_arrow_left.alpha = p1
                mine_store_myproduct.alpha = p1
                mine_store_line.alpha = 1 - p1
                mine_store_tv_title.x = mine_store_arrow_left.x + p1 * dp2px(38f)
            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                    mine_store_arrow_left.visibility = View.GONE
                    mine_store_myproduct.visibility = View.GONE
                } else if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                    mine_store_arrow_left.visibility = View.VISIBLE
                    mine_store_myproduct.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun changeWeekImageView(weekArr: Array<Int>) {
        for (i in weekArr.indices) {
            when {
                WeekGenerator.isToDayOrSunDay(i) -> {
                    imageViewResArr[i].setImageResource(R.drawable.mine_ic_sign_diamond)
                }
                weekArr[i] == WeekGenerator.WEEK_HAS_SIGN -> {
                    imageViewResArr[i].setImageResource(R.drawable.mine_shape_circle_src_activity_sign)
                }
                else -> {
                    imageViewResArr[i].setImageResource(R.drawable.mine_shape_circle_src_activity_sign_grey)
                }
            }
        }
    }

    private fun moveBubble() {
        val toDay = WeekGenerator.getToDay()
        val centerX = imageViewResArr[toDay].x + imageViewResArr[toDay].width / 2
        mine_daily_tv_bubble.x = centerX - mine_daily_tv_bubble.width / 2
    }


    //接下来主要是一些修改divider颜色的方法
    private fun paintDivider(dividerArr: Array<Int>) {
        for (i in 0..5) {
            if (getDividerColor(dividerArr[i]) != null) {
                dividerResArr[i].color.color = getDividerColor(dividerArr[i])!!
                if (!WeekGenerator.isToDay(i)) {
                    dividerResArr[i].progress = 1f
                } else {
                    //如果是今天已签到的话，今天之后的那个divider会有个动画
                    startAnimator(i, dividerArr)
                }
            }
        }
    }

    private fun startAnimator(index: Int, dividerArr: Array<Int>) {
        if (index > dividerArr.size - 1) return
        if (getDividerColor(dividerArr[index]) != null) {
            dividerResArr[index].color.color = getDividerColor(dividerArr[index])!!
            dividerResArr[index].progress = 1f
            val animator = ObjectAnimator.ofFloat(dividerResArr[index], "progress", 0f, 1f)
            animator.duration = 500
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
        }
    }

    private fun getDividerColor(color: Int): Int? {
        if (color == WeekGenerator.COLOR_GREY) {
            return ContextCompat.getColor(this, R.color.mine_sign_divider_grey)
        } else if (color == WeekGenerator.COLOR_BLUE) {
            return ContextCompat.getColor(this, R.color.mine_sign_divider_blue)
        }
        return null
    }

}
