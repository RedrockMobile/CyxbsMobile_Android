package com.mredrock.cyxbs.mine.page.sign

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
import com.mredrock.cyxbs.common.component.CommonDialogFragment
import com.mredrock.cyxbs.common.config.MINE_CHECK_IN
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.Product
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import com.mredrock.cyxbs.mine.util.widget.*
import kotlinx.android.synthetic.main.mine_activity_daily_sign.*
import kotlin.math.abs

/**
 * Created by zzzia on 2018/8/14.
 * origin by jay86
 * 每日签到
 */
@Route(path = MINE_CHECK_IN)
class DailySignActivity : BaseViewModelActivity<DailyViewModel>() {

    private var objectAnimator: ObjectAnimator? = null

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

    //通过一个标志位，来判断刷新divider的时候是否需要动画，
    //当为true时，表明用户点击了签到按钮导致的UI刷新，这时候需要动画，
    //当为false时，表明为正常的进入Activity刷新
    private var isChecking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransparent(window)
        setContentView(R.layout.mine_activity_daily_sign)
        common_toolbar.initWithSplitLine("", false)
        initView()
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
        viewModel.status.observe(this, Observer {
            refreshUI(it)
        })
        viewModel.isInVacation.observe(this, Observer {
            if (it == true) {
                toast("寒暑假不可签到")
            }
        })
    }

    private fun initView() {
        mine_daily_sign.setOnClickListener { checkIn() }
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
                    background = SkinManager.getDrawable("common_dialog_btn_positive_blue", R.drawable.common_dialog_btn_positive_blue)
                setTextColor(SkinManager.getColor("common_white_font_color", R.color.common_white_font_color))
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

    /**
     * 设置签到的七个圆点的Image
     * 圆点有三种状态：蓝色，钻石，灰色
     */
    private fun changeWeekImage() {
        val state = weekGenerator.getWeekImageStateArr()
        for (i in 0..6) {
            when {
                state[i] == ImageState.IMAGE_BLUE -> {
                    imageViewResArr[i].setImageDrawable(SkinManager.getDrawable("mine_shape_circle_src_activity_sign",
                            R.drawable.mine_shape_circle_src_activity_sign))
                }
                state[i] == ImageState.IMAGE_DIAMOND -> {
                    imageViewResArr[i].setImageDrawable(SkinManager.getDrawable("mine_ic_sign_diamond",
                            R.drawable.mine_ic_sign_diamond))
                }
                else -> {
                    imageViewResArr[i].setImageDrawable(SkinManager.getDrawable("mine_shape_circle_src_activity_sign_grey",
                            R.drawable.mine_shape_circle_src_activity_sign_grey))
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
                dividerResArr[i].color.color = SkinManager.getColor("common_mine_sign_divider_grey", R.color.common_mine_sign_divider_grey)
            }
            ColorState.COLOR_BLUE -> {
                dividerResArr[i].color.color = SkinManager.getColor("common_mine_sign_divider_grey", R.color.common_mine_sign_divider_blue)
            }
            else -> {
                dividerResArr[i].color.color = SkinManager.getColor("common_mine_sign_divider_grey", R.color.common_mine_sign_divider_blue_light)
            }
        }
    }
}
