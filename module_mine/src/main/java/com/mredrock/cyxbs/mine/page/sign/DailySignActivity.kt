package com.mredrock.cyxbs.mine.page.sign

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
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
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Product
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.page.myproduct.MyProductActivity
import com.mredrock.cyxbs.mine.util.ui.ProductAdapter
import com.mredrock.cyxbs.mine.util.widget.*
import kotlinx.android.synthetic.main.mine_activity_daily_sign.*
import kotlinx.android.synthetic.main.mine_layout_store_sign.*
import kotlin.math.abs


/**
 * Created by zzzia on 2018/8/14.
 * origin by jay86
 * 每日签到
 */
@Route(path = MINE_CHECK_IN)
class DailySignActivity : BaseViewModelActivity<DailyViewModel>() {

    private var objectAnimator: ObjectAnimator? = null
    private var onStartBottomStatus = BottomSheetBehavior.STATE_COLLAPSED
    private var requestPointStore = false

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

    companion object{
        fun actionStart(context: Context, bottomSheetStatus: Int){
            val intent = Intent(context, DailySignActivity::class.java)
            intent.putExtra("status", bottomSheetStatus)
            context.startActivity(intent)
        }
    }


    //通过一个标志位，来判断刷新divider的时候是否需要动画，
    //当为true时，表明用户点击了签到按钮导致的UI刷新，这时候需要动画，
    //当为false时，表明为正常的进入Activity刷新
    private var isChecking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.apply {
            onStartBottomStatus = getIntExtra("status", BottomSheetBehavior.STATE_COLLAPSED)
            requestPointStore = onStartBottomStatus == BottomSheetBehavior.STATE_EXPANDED
        }
        setTransparent(window)
        setContentView(R.layout.mine_activity_daily_sign)
        common_toolbar.initWithSplitLine("", false)
        initView()
        initAdapter()
        dealBottomSheet()
        initData()
    }

    private fun setTransparent(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 需要处理属性动画内存泄露的问题
     */
    override fun onDestroy() {
        objectAnimator?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }
        super.onDestroy()
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
        mine_store_my_product.setOnClickListener {
            startActivity<MyProductActivity>()
            overridePendingTransition(R.anim.common_slide_in_from_bottom_with_bezier, R.anim.common_scale_fade_out_with_bezier)
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
        val isFirstSemester = if (schoolCalendarPlus.isFirstSemester()) "秋" else "春"
        val dayOfTerm = schoolCalendarPlus.dayOfTerm
        if (dayOfTerm < 0) {
            mine_daily_tv_week.text = "距离${isFirstSemester}学期开学还有${abs(dayOfTerm)}天"
        } else {
            mine_daily_tv_week.text = "${isFirstSemester}学期第${schoolCalendarPlus.getChineseWeek()}周"
        }
        mine_daily_dayCount.text = "已连续打卡${scoreStatus.serialDays}天"
        mine_daily_tv_ranking_percentage.text = "超过${scoreStatus.percent}的邮子"
        if (scoreStatus.canCheckIn && scoreStatus.isChecked) {
            mine_daily_tv_ranking.text = "今日第${scoreStatus.rank}位打卡"
        } else if (scoreStatus.canCheckIn && !scoreStatus.isChecked) {
            mine_daily_tv_ranking.text = "还没有打卡哦"
        } else {
            mine_daily_tv_ranking.text = "寒暑假不可签到呢(●'ᴗ'σ)σணღ*"
        }
        mine_store_tv_integral.text = "${scoreStatus.integral}"
        if (scoreStatus.isChecked or !scoreStatus.canCheckIn) {
            mine_daily_sign.apply {
                isClickable = false
                background = ResourcesCompat.getDrawable(resources, R.drawable.mine_bg_round_corner_grey, null)
                setTextColor(ContextCompat.getColor(context, R.color.common_grey_button_text))
                text = if (scoreStatus.canCheckIn) "已签到" else "签到"
            }
        } else {
            mine_daily_sign.apply {
                isClickable = true
                background = ResourcesCompat.getDrawable(resources, R.drawable.common_dialog_btn_positive_blue, null)
                setTextColor(ContextCompat.getColor(context, R.color.common_white_font_color))
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
        behavior.state = onStartBottomStatus//设置bottomSheet的展开状态
        behavior.peekHeight = dp2px(95f + 30f)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                mine_store_arrow_left.alpha = p1
                mine_store_my_product.alpha = p1
                mine_store_line.alpha = 1 - p1
                mine_store_tv_title.x = mine_store_arrow_left.x + p1 * dp2px(15f) + dp2px(17f)
            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                    mine_store_arrow_left.invisible()
                    mine_store_my_product.invisible()
                } else {
                    mine_store_arrow_left.visible()
                    mine_store_my_product.visible()
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
                if (i == it.getToday() && isChecking && dividerColorArr[i] == ColorState.COLOR_BLUE) {
                    isChecking = false
                    val index = it.getToday()
                    if (index > 5) return
                    setDividerColor(index, ColorState.COLOR_BLUE)
                    val animator = ObjectAnimator.ofFloat(dividerResArr[index], "progress", 0f, 1f)
                    //先让objectAnimator保存animator的引用，方便退出Activity时cancel掉阻止内存泄漏
                    objectAnimator = animator

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
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.common_mine_sign_divider_grey)
            }
            ColorState.COLOR_BLUE -> {
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.common_mine_sign_divider_blue)
            }
            else -> {
                dividerResArr[i].color.color = ContextCompat.getColor(this, R.color.common_mine_sign_divider_blue_light)
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
                    val tag = "exchange"
                    if (supportFragmentManager.findFragmentByTag(tag) == null) {
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
                                        val str = "这将消耗您的${product.integral}个积分，仍然要兑换吗？"
                                        it.findViewById<TextView>(R.id.mine_tv_exchange_for_sure_content).text = str
                                    }
                            )
                        }.show(supportFragmentManager, tag)
                    }
                } else {
                    val tag = "lack of integral"
                    //防止连续两次快速点击两次重复创建相同tag的DialogFragment
                    if (supportFragmentManager.findFragmentByTag(tag) == null) {
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
                        }.show(supportFragmentManager, tag)
                    }
                }
            }
        }
        adapter.setOnExChangeClick(click)
        mine_store_rv.adapter = adapter

    }


    override fun onBackPressed() {
        val behavior = BottomSheetBehavior.from(mine_sign_fl)
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED && !requestPointStore) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}
