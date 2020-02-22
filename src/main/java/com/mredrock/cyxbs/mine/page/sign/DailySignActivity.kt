package com.mredrock.cyxbs.mine.page.sign

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.MINE_CHECK_IN
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Product
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.page.myproduct.MyProductActivity
import com.mredrock.cyxbs.mine.util.ui.ProductAdapter
import com.mredrock.cyxbs.mine.util.widget.*
import kotlinx.android.synthetic.main.mine_activity_daily_sign.*
import kotlinx.android.synthetic.main.mine_layout_store_sign.*
import org.jetbrains.anko.textColor


/**
 * Created by zzzia on 2018/8/14.
 * origin by jay86
 * 每日签到
 */
@Route(path = MINE_CHECK_IN)
class DailySignActivity(override val viewModelClass: Class<DailyViewModel> = DailyViewModel::class.java
                        , override val isFragmentActivity: Boolean = false)
    : BaseViewModelActivity<DailyViewModel>() {

    private val dividerResArr: Array<Stick> by lazy {
        arrayOf(mine_daily_v_divider_mon_tue,
                mine_daily_v_divider_tue_wed,
                mine_daily_v_divider_wed_thurs,
                mine_daily_v_divider_thurs_fri,
                mine_daily_v_divider_fri_sat,
                mine_daily_v_divider_sat_sun)
    }
    private val imageViewResArr: Array<ImageView> by lazy {
        arrayOf(mine_daily_iv_mon,
                mine_daily_iv_tue,
                mine_daily_iv_wed,
                mine_daily_iv_thurs,
                mine_daily_iv_fri,
                mine_daily_iv_sat,
                mine_daily_iv_sun)
    }
    private val spaceResArr: Array<Space> by lazy {
        arrayOf(mine_daily_space_mon,
                mine_daily_space_tue,
                mine_daily_space_wed,
                mine_daily_space_thurs,
                mine_daily_space_fri,
                mine_daily_space_sat,
                mine_daily_space_sun)
    }
    private val weekGenerator: WeekGenerator by lazy {
        WeekGenerator()
    }

    private val adapter: ProductAdapter by lazy {
        ProductAdapter()
    }


    //通过一个标志位，来判断刷新divider的时候是否需要动画，
    //当为true时，表明用户点击了签到按钮导致的UI刷新，这时候需要动画，
    //当为false时，表明为正常的进入Activity刷新
    private var isChecking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置导航栏字体颜色为白色
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setContentView(R.layout.mine_activity_daily_sign)

        initView()
        initAdapter()
        dealBottomSheet()
        initData()
    }

    //ViewModel观察和网络请求
    private fun initData() {
        viewModel.loadAllData()
        viewModel.loadProduct()
        viewModel.status.observe(this, Observer {
            refreshUI(it)
        })
        viewModel.products.observe(this, Observer {

            if (it == null || it.isEmpty()) {
                return@Observer
            }
            mine_iv_empty_product.gone()

            adapter.submitList(it)
        })
        viewModel.isInVacation.observe(this, Observer {
            if (it == true) {
                toast("寒暑假不可签到")
            }
        })
        viewModel.exchangeEvent.observe(this, Observer {
            if (it == true) {
                toast("兑换成功， 如果是实体物品，请携带相关证件前往红岩网校工作站领取奖品")
            } else {
                toast("兑换失败")
            }
        })
    }

    private fun initView() {
        mine_daily_sign.setOnClickListener { checkIn() }
        mine_store_rv.addItemDecoration(SpaceDecoration(dp2px(8f)))
        mine_store_myproduct.setOnClickListener {
            startActivity<MyProductActivity>()
        }
    }

    /**
     * 刷新签到页面
     */
    @SuppressLint("SetTextI18n")
    private fun refreshUI(scoreStatus: ScoreStatus) {
        //后端的规则是低位字节代表星期一，高位字节代表星期日，这里做一个翻转处理
        val weekState = scoreStatus.weekInfo.map(Character::getNumericValue).reversed()
        weekGenerator.weekSignStateArr = weekState
        val schoolCalendarPlus = SchoolCalendarPlus()
        val pair = schoolCalendarPlus.getYearPair()
        mine_daily_tv_year.text = "${pair.first}-${pair.second}"
        mine_daily_tv_week.text = "上学期第${schoolCalendarPlus.getChineseWeek()}周"
        mine_daily_dayCount.text = "已连续打卡${scoreStatus.serialDays}天"
        mine_daily_tv_ranking_percentage.text = "超过${scoreStatus.percent}的邮子"
        if (scoreStatus.isChecked) {
            mine_daily_tv_ranking.text = "今日第${scoreStatus.rank}位打卡"
        }
        mine_store_tv_integral.text = "${scoreStatus.integral}"
        if (scoreStatus.isChecked) {
            mine_daily_sign.apply {
                isClickable = false
                background = ResourcesCompat.getDrawable(resources, R.drawable.mine_bg_round_corner_grey, null)
                textColor = ContextCompat.getColor(context, R.color.greyButtonText)
                text = "已签到"
            }
        } else {
            mine_daily_sign.apply {
                isClickable = true
                background = ResourcesCompat.getDrawable(resources, R.drawable.common_dialog_btn_positive_blue, null)
                textColor = ContextCompat.getColor(context, R.color.mine_white)
                text = "签到"
            }
        }
        changeWeekImage()
        paintDivider(weekGenerator.getDividerColorArr())
        val today = weekGenerator.getToday()
        today.let {
            val centerX = spaceResArr[it].x
            mine_daily_tv_bubble.x = centerX - mine_daily_tv_bubble.width / 2
            mine_daily_tv_bubble.text = "${weekGenerator.getTodayScore()}积分"
        }
    }

    private fun checkIn() {
        isChecking = true
        viewModel.checkIn()
    }

    private fun dealBottomSheet() {
        val behavior = BottomSheetBehavior.from(mine_sign_fl)
        mine_store_arrow_left.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
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
                mine_store_tv_title.x = mine_store_arrow_left.x + p1 * dp2px(15f) + dp2px(17f)
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

    /**
     * 设置签到的七个圆点的Image
     * 圆点有三种状态：蓝色，钻石，灰色
     */
    private fun changeWeekImage() {
        val state = weekGenerator.getWeekImageStateArr()
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


    //修改divider颜色,如果isChecking == true 那么说明是用户点击了签到导致的UI刷新，此时相应的divider需要有一段动画
    private fun paintDivider(dividerColorArr: Array<ColorState>) {
        weekGenerator.let {
            for (i in 0..5) {
                if (i == it.getToday() && isChecking) {
                    isChecking = false
                    val index = it.getToday()
                    if (index > 5) return
                    setDividerColor(index, ColorState.COLOR_BLUE)
                    val animator = ObjectAnimator.ofFloat(dividerResArr[index], "progress", 0f, 1f)
                    animator.duration = 1000
                    animator.interpolator = AccelerateDecelerateInterpolator()
                    animator.start()
                } else {
                    setDividerColor(i, dividerColorArr[i])
                    dividerResArr[i].progress = 1f
                }

            }
        }

    }

    private fun setDividerColor(i: Int, color: ColorState) {
        when (color) {
            ColorState.COLOR_GREY -> {
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.mine_sign_divider_grey)
            }
            ColorState.COLOR_BLUE -> {
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.mine_sign_divider_blue)
            }
            else -> {
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.mine_sign_divider_blue_light)
            }
        }
    }

    //设置商品展示的Adapter，同时设置监听，弹出DialogFragment
    private fun initAdapter() {
        val click: ((Product, Int) -> Unit)? = { product, position ->
            val integral = viewModel.status.value?.integral

            integral?.let {
                //防止商品积分为空，同时需处理积分为小数的情况
                val productIntegral = if (product.integral.isEmpty()) 0 else product.integral.toFloat().toInt()
                //判断用户积分是否大于物品所需积分数 && 物品剩余数大于0
                if (integral >= productIntegral && product.count > 0) {
                    CommonDialogFragment().apply {
                        initView(
                                containerRes = R.layout.mine_layout_dialog_exchange,
                                positiveString = "确认兑换",
                                onPositiveClick = {
                                    viewModel.exchangeProduct(product, position)
                                    dismiss()
                                },
                                onNegativeClick = { dismiss() },
                                elseFunction = {
                                    it.findViewById<TextView>(R.id.mine_tv_exchange_for_sure_content).text = "这将消耗您的${product.integral}个积分，仍然要兑换吗？"
                                }
                        )
                    }.show(supportFragmentManager, "exchange")
                } else {
                    CommonDialogFragment().apply {
                        initView(
                                containerRes = R.layout.mine_layout_dialog_exchange,
                                positiveString = "确认",
                                onPositiveClick = { dismiss() },
                                elseFunction = {
                                    //区分是积分不足还是物品剩余数为0
                                    if (product.count <= 0) {
                                        it.findViewById<TextView>(R.id.mine_tv_exchange_for_sure_content).text = "物品被抢光了，明天再来吧"
                                    } else {
                                        it.findViewById<TextView>(R.id.mine_tv_exchange_for_sure_content).text = "积分不足"
                                    }
                                }
                        )
                    }.show(supportFragmentManager, "lack of integral")
                }
            }
        }
        adapter.setOnExChangeClick(click)
        mine_store_rv.adapter = adapter

    }


}
