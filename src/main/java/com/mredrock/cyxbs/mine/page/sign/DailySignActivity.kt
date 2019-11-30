package com.mredrock.cyxbs.mine.page.sign

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Space
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Product
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
    private lateinit var spaceResArr: Array<Space>
    private var weekGenerator: WeekGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置导航栏字体颜色为白色
        window.decorView.systemUiVisibility = View.SYSTEM_UI_LAYOUT_FLAGS

        setContentView(R.layout.mine_activity_daily_sign)
//        if (!isLogin()) return

        initView()
        dealBottomSheet()
        user?.let { viewModel.loadAllData(it) }
        viewModel.fakeStatus.postValue(arrayOf(1, 1, 1, 0, 1, 0, 0))
        mine_daily_sign.setOnClickListener { checkIn() }
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
        spaceResArr = arrayOf(mine_daily_space_mon,
                mine_daily_space_tue,
                mine_daily_space_wed,
                mine_daily_space_thurs,
                mine_daily_space_fri,
                mine_daily_space_sat,
                mine_daily_space_sun)

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
            weekGenerator = WeekGenerator(it)
            refreshUI()
        })


    }

    /**
     * 刷新签到页面
     */
    private fun refreshUI() {
        changeWeekImage()
        weekGenerator?.let { paintDivider(it.getDividerColorArr()) }
        placeBubble()
    }

    private fun checkIn() {
//        viewModel.checkIn(user!!) {
        //创建今天到明天的divider的动画
        animateUI()
//        viewModel.fakeStatus.postValue(arrayOf(1, 1, 1, 0, 1, 1, 0))
//        }
    }

    private fun animateUI() {
        weekGenerator?.let {
            animateDivider(it.getToDay())
            animateImage(it.getToDay())
        }


    }

    private fun animateImage(weekDay: Int) {
        imageViewResArr[weekDay].setImageResource(R.drawable.mine_shape_circle_src_activity_sign)
    }

    private fun isLogin(): Boolean {
        if (!BaseApp.isLogin) {
            toast("还没有登录...")
        }
        return BaseApp.isLogin
    }

    private fun dealBottomSheet() {
        val behavior = BottomSheetBehavior.from(mine_sign_fl)
        var statusBarHeight = -1
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        behavior.peekHeight = dp2px(95f) + (if (statusBarHeight > 0) statusBarHeight else 0)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                mine_store_arrow_left.alpha = p1
                mine_store_myproduct.alpha = p1
                mine_store_line.alpha = 1 - p1
                mine_store_tv_title.x = mine_store_arrow_left.x + p1 * dp2px(38f)
            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                    mine_store_arrow_left.invisible()
                    mine_store_myproduct.invisible()
                } else {
                    mine_store_arrow_left.visible()
                    mine_store_myproduct.visible()
                }
            }
        })
    }

    private fun changeWeekImage() {
        weekGenerator ?: return
        val state = weekGenerator!!.getWeekImageStateArr()
        for (i in 0..6) {
            when {
                state[i] == ImageState.IMAGE_BLUE -> {
                    imageViewResArr[i].setImageResource(R.drawable.mine_shape_circle_src_activity_sign)
                }
                state[i] == ImageState.IMAGE_DIAMOND -> {
                    imageViewResArr[i].setImageResource(R.drawable.mine_ic_sign_diamond)
                }
                else -> {
                    imageViewResArr[i].setImageResource(R.drawable.mine_shape_circle_src_activity_sign_grey)
                }
            }
        }
    }

    private fun placeBubble() {
        val toDay = weekGenerator?.getToDay()
        toDay?.let {
            val centerX = spaceResArr[it].x
            mine_daily_tv_bubble.x = centerX - mine_daily_tv_bubble.width / 2
        }
    }


    //接下来主要是一些修改divider颜色的方法
    private fun paintDivider(dividerColorArr: Array<ColorState>) {
        for (i in 0..5) {
            setDividerColor(i, dividerColorArr[i])
            dividerResArr[i].progress = 1f
        }
    }

    private fun setDividerColor(i: Int, color: ColorState) {
        when (color) {
            ColorState.COLOR_GREY -> {
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.mine_sign_divider_grey)
                dividerResArr[i].alpha = 1f
            }
            ColorState.COLOR_BLUE -> {
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.mine_sign_divider_blue)
                dividerResArr[i].alpha = 1f
            }
            else -> {
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.mine_sign_divider_blue)
                dividerResArr[i].alpha = 0.31f
            }
        }
    }


    //开启一段divider的颜色填充动画,默认颜色为蓝色
    private fun animateDivider(index: Int, color: ColorState = ColorState.COLOR_BLUE) {
        if (index > 5) return
        setDividerColor(index, color)
        val animator = ObjectAnimator.ofFloat(dividerResArr[index], "progress", 0f, 1f)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }


}
