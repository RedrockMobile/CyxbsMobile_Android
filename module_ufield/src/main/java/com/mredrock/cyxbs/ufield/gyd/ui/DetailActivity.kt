package com.mredrock.cyxbs.ufield.gyd.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.config.route.COURSE_POS_TO_MAP
import com.mredrock.cyxbs.config.route.DISCOVER_MAP
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.base.ui.viewModelBy
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.gyd.viewmodel.DetailViewModel
import com.mredrock.cyxbs.ufield.gyd.span.RoundBackgroundSpan
import com.mredrock.cyxbs.ufield.gyd.span.VerticalCenterSpan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class DetailActivity : BaseActivity() {
    private var id by Delegates.notNull<Int>()
    private val toolbar by R.id.detail_toolbar.view<Toolbar>()
    private val tvSee by R.id.ufield_tv_wantsee.view<TextView>()
    private val tvTitle by R.id.ufield_tv_title.view<TextView>()
    private val tvType by R.id.ufield_tv_type.view<TextView>()
    private val tvSum by R.id.ufield_tv_sum_wantsee.view<TextView>()
    private val tvSponsor by R.id.ufield_tv_sponsor.view<TextView>()
    private val tvCreator by R.id.ufield_tv_creator.view<TextView>()
    private val tvWay by R.id.ufield_tv_way.view<TextView>()
    private val tvPlace by R.id.ufield_tv_place.view<TextView>()
    private val tvDetail by R.id.ufield_tv_detail.view<TextView>()
    private val tvTime by R.id.ufield_tv_time.view<TextView>()
    private val ivCover by R.id.detail_iv_cover.view<ImageView>()
    private val tvStart by R.id.ufield_tv_starttime.view<TextView>()
    private val tvEnd by R.id.ufield_tv_endtime.view<TextView>()
    private val ivMap by R.id.ufield_map.view<ImageView>()
    private var countDownTimer:CountDownTimer=object :CountDownTimer(1,1000){
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
        }
    }
    private val viewModel by viewModelBy {
         id = intent.getIntExtra("actID", 1)
        DetailViewModel(id)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initView()
        //Log.d("827857", "测试结果-->> $id");
        viewModel.wantToSee.observe(this) {
            if (it) {
                toast("想看成功")
                tvSee.apply {
                    text = "已想看"
                    setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.seecolor))
                    setBackgroundResource(R.drawable.ufield_shape_haveseen)
                    setOnClickListener(null)
                }

            } else {
                toast("想看失败")
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ufield_ic_toolbar_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ivMap.setOnClickListener {
            ServiceManager.activity(DISCOVER_MAP) {
                withString(COURSE_POS_TO_MAP, "风雨操场")
            }
        }

        viewModel.detailData.observe(this) {
            tvTitle.text = it.data.activity_title
            when (it.data.activity_type) {
                "culture" -> tvType.text = "文娱活动"
                "sports" -> tvType.text = "体育活动"
                "education" -> tvType.text = "教育活动"
            }
            tvSum.text = "${it.data.activity_watch_number}人想看"
            tvSponsor.text = it.data.activity_organizer
            tvCreator.text = it.data.activity_creator
            tvWay.text = it.data.activity_registration_type
            tvPlace.text = it.data.activity_place
            tvDetail.text = it.data.activity_detail
            ivCover.setImageFromUrl(it.data.activity_cover_url)
            tvStart.text = trans(it.data.activity_start_at)
            tvEnd.text = trans(it.data.activity_end_at)
            if (it.data.want_to_watch) {
                tvSee.text = "已想看"
                tvSee.setTextColor(ContextCompat.getColor(this, R.color.seecolor))
                tvSee.setBackgroundResource(R.drawable.ufield_shape_haveseen)
            } else {
                val spannableString = SpannableString(" + 想看")

                // 调整加号的大小为相对于文字大小的比值（这里设置为 1.3）
                spannableString.setSpan(
                    RelativeSizeSpan(1.3f),
                    1,
                    2,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                tvSee.text = spannableString
                tvSee.setOnClickListener {
                    viewModel.wantToSee(id)
                }
            }
            if (it.data.ended) {
                tvTime.text = "活动已结束"
            } else {
                // 活动开始时间戳和结束时间戳（以秒为单位）
                val startTimeInSeconds = it.data.activity_start_at
                val endTimeInSeconds = it.data.activity_end_at

                val currentTimeInSeconds = System.currentTimeMillis() / 1000

                // 判断活动状态
                if (currentTimeInSeconds < startTimeInSeconds) {
                    // 距离开始的剩余时间
                    val remainingTimeInSeconds = startTimeInSeconds - currentTimeInSeconds

                    // 创建并启动倒计时
                     countDownTimer =
                        object : CountDownTimer(remainingTimeInSeconds * 1000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val days =
                                    kotlin.math.floor(millisUntilFinished / (1000 * 60 * 60 * 24.0))
                                        .toLong()
                                val hours =
                                    kotlin.math.floor((millisUntilFinished % (1000 * 60 * 60 * 24.0)) / (1000 * 60 * 60.0))
                                        .toLong()
                                val minutes =
                                    kotlin.math.floor((millisUntilFinished % (1000 * 60 * 60.0)) / (1000 * 60.0))
                                        .toLong()
                                val seconds =
                                    kotlin.math.floor((millisUntilFinished % (1000 * 60.0)) / 1000)
                                        .toLong()

                                val countdownText =
                                    "距离开始还有 ${days}天 ${hours}时 ${minutes}分 ${seconds}秒"

                                val spannableBuilder = SpannableStringBuilder(countdownText)


                                val backgroundColor = Color.parseColor("#ECEEF2")

                                val backgroundColorSpanDays =
                                    RoundBackgroundSpan(backgroundColor, 10F, 20F, Color.BLACK)
                                val backgroundColorSpanHours =
                                    RoundBackgroundSpan(backgroundColor, 20F, 20F, Color.BLACK)
                                val backgroundColorSpanMinutes =
                                    RoundBackgroundSpan(backgroundColor, 20F, 20F, Color.BLACK)
                                val backgroundColorSpanSeconds =
                                    RoundBackgroundSpan(backgroundColor, 20F, 20F, Color.BLACK)

// 设置天的样式
                                spannableBuilder.setSpan(
                                    backgroundColorSpanDays,
                                    "距离开始还有 ".length,
                                    "距离开始还有 ".length + days.toString().length,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                spannableBuilder.setSpan(
                                    VerticalCenterSpan(),
                                    "距离开始还有 ".length,
                                    "距离开始还有 ".length + days.toString().length,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )


// 设置小时的样式
                                spannableBuilder.setSpan(
                                    backgroundColorSpanHours,
                                    "距离开始还有 ${days}天 ".length,
                                    "距离开始还有 ${days}天 ".length + hours.toString().length,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                spannableBuilder.setSpan(
                                    VerticalCenterSpan(),
                                    "距离开始还有 ${days}天 ".length,
                                    "距离开始还有 ${days}天 ".length + hours.toString().length,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )


// 设置分钟的样式
                                spannableBuilder.setSpan(
                                    backgroundColorSpanMinutes,
                                    "距离开始还有 ${days}天 ${hours}时 ".length,
                                    "距离开始还有 ${days}天 ${hours}时 ".length + minutes.toString().length,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                spannableBuilder.setSpan(
                                    VerticalCenterSpan(),
                                    "距离开始还有 ${days}天 ${hours}时 ".length,
                                    "距离开始还有 ${days}天 ${hours}时 ".length + minutes.toString().length,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )


// 设置秒的样式
                                spannableBuilder.setSpan(
                                    backgroundColorSpanSeconds,
                                    "距离开始还有 ${days}天 ${hours}时 ${minutes}分 ".length,
                                    countdownText.length - 1,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                spannableBuilder.setSpan(
                                    VerticalCenterSpan(),
                                    "距离开始还有 ${days}天 ${hours}时 ${minutes}分 ".length,
                                    countdownText.length - 1,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )


                                tvTime.text = spannableBuilder
                            }

                            override fun onFinish() {
                                // 倒计时结束处理逻辑
                                tvTime.text = "活动开始"
                            }
                        }

                    countDownTimer.start()
                } else if (currentTimeInSeconds in startTimeInSeconds until endTimeInSeconds) {
                    tvTime.text = "活动进行中"
                } else {
                    tvTime.text = "活动已结束"
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun trans(timestampInSeconds: Long): String {
        val date = Date(timestampInSeconds * 1000L)

        val format = SimpleDateFormat("yyyy年MM月dd日HH点mm分", Locale.getDefault())
        return format.format(date)
    }
}