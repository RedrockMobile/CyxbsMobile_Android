package com.mredrock.cyxbs.qa.pages.quiz.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.RelativeLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.quiz.ui.QuizActivity
import com.mredrock.cyxbs.qa.pages.quiz.ui.adpter.RewardSetAdapter
import com.mredrock.cyxbs.qa.utils.toFormatString
import kotlinx.android.synthetic.main.qa_dialog_question_reward_set.*
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import com.mredrock.cyxbs.common.utils.extensions.setSingleOnClickListener

class RewardSetDialog(context: Context, rewardCount: Int, private val isFirstQuiz: Boolean, private val contentList: List<DownMessageText>) : BottomSheetDialog(context) {
    companion object {
        @JvmStatic
        val HOURS_DISPLAY_NAME = Array(24) { String.format("%02d时", it) }

        @JvmStatic
        val MINUTES_DISPLAY_NAME = Array(60) { String.format("%02d分", it) }

        @JvmStatic
        val rewardCountList = listOf(1, 2, 3, 5, 10, 15)

        //最大消失时间间隔（：天）
        const val MAX_DAY = 30

        //最小消失时间间隔(:小时)
        const val MIN_GAP_HOUR = 1
    }

    var onSubmitButtonClickListener: (String, Int) -> Unit = { _: String, _: Int -> }
    private val showToday: Boolean

    private var now = GregorianCalendar()
    private var default: GregorianCalendar
    private val myRewardCount = rewardCount
    private var mBehavior: BottomSheetBehavior<View>
    private var day: String
    private var hour: String
    private var minute: String

    @SuppressLint("InflateParams")
    private val container: View = layoutInflater.inflate(R.layout.qa_dialog_question_reward_set, null)
    private val daysDisplayName: Array<String> by lazy { initDayDisplayName() }
    private val todayHourStartIndex: Int
    private val todayMinuteStartIndex: Int
    private val daysCommitName: Array<String> by lazy { initDaysCommitName() }

    init {
        setContentView(container)
        mBehavior = BottomSheetBehavior.from(container.parent as View).apply {
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                }

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_HIDDEN || p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                        cancel()
                    }
                }

            })
        }
        default = GregorianCalendar().apply {
            add(Calendar.HOUR_OF_DAY, MIN_GAP_HOUR)
        }
        val defaultHour = String.format("%02d时", default.get(Calendar.HOUR_OF_DAY))
        val defaultMinute = String.format("%02d分", default.get(Calendar.MINUTE))
        todayHourStartIndex = HOURS_DISPLAY_NAME.binarySearch(defaultHour)
        todayMinuteStartIndex = MINUTES_DISPLAY_NAME.binarySearch(defaultMinute)
        showToday = now.get(Calendar.DAY_OF_MONTH) == default.get(Calendar.DAY_OF_MONTH)
        day = daysDisplayName[0]
        hour = HOURS_DISPLAY_NAME[todayHourStartIndex]
        minute = MINUTES_DISPLAY_NAME[todayMinuteStartIndex]
        tv_mission_deadline_detail.text = "$day $hour $minute"
        initPickerView()
        vp_set_reward_count.adapter = RewardSetAdapter(rewardCountList)
        val marginSize = context.getScreenWidth() / 2
        val exceptedWidth = context.getScreenWidth() / 10
        val lp: RelativeLayout.LayoutParams? = vp_set_reward_count.layoutParams as RelativeLayout.LayoutParams?
        lp?.setMargins(marginSize - exceptedWidth, 0, marginSize - exceptedWidth, 0)
        vp_set_reward_count.apply {
            layoutParams = lp
            offscreenPageLimit = 7
            setPageTransformer(true) { page, position ->
                page.alpha = 0.7.pow(abs(position.toDouble())).toFloat()
                page.scaleX = 0.7.pow(abs(position.toDouble())).toFloat()
                page.scaleY = 0.7.pow(abs(position.toDouble())).toFloat()
            }
            currentItem = 3
        }
        rl_reward_picker.setOnTouchListener { v, event -> vp_set_reward_count.onTouchEvent(event) }
        iv_reward_description.setSingleOnClickListener { RewardDescriptionDialog(context, contentList).show() }
        tv_my_reward_count.text = context.resources.getString(R.string.qa_quiz_reward_set_my_reward, myRewardCount)
        tv_quiz_dialog_cancel.setSingleOnClickListener { cancel() }

    }


    override fun show() {
        super.show()
        if (isFirstQuiz) {
            RewardDescriptionDialog(context, contentList).apply {
                setOnDismissListener {
                    context.sharedPreferences(QuizActivity.FIRST_QUIZ).editor { putBoolean(QuizActivity.FIRST_QUIZ_SP_KEY, false) }
                }
            }.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initPickerView() {
        tp_quiz_day.apply {
            initDividerColor()
            minValue = 0
            maxValue = daysDisplayName.size - 1
            displayedValues = daysDisplayName
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            setOnValueChangedListener { numberPicker, i, i2 ->
                day = displayedValues[value].toString()
                tv_mission_deadline_detail.text = "$day $hour $minute"
            }
        }
        tp_quiz_hour.apply {
            initDividerColor()
            minValue = 0
            maxValue = HOURS_DISPLAY_NAME.size - 1
            displayedValues = HOURS_DISPLAY_NAME
            value = todayHourStartIndex
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            setOnValueChangedListener { numberPicker, i, i2 ->
                hour = displayedValues[value].toString()
                tv_mission_deadline_detail.text = "$day $hour $minute"
            }

        }
        tp_quiz_minute.apply {
            initDividerColor()
            minValue = 0
            maxValue = MINUTES_DISPLAY_NAME.size - 1
            displayedValues = MINUTES_DISPLAY_NAME
            value = todayMinuteStartIndex
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            setOnValueChangedListener { numberPicker, i, i2 ->
                minute = displayedValues[value].toString()
                tv_mission_deadline_detail.text = "$day $hour $minute"
            }
        }
        fl_quiz_submit.setSingleOnClickListener {
            onSubmitButtonClickListener("${daysCommitName[tp_quiz_day.value]} ${tp_quiz_hour.value}时${tp_quiz_minute.value}分", rewardCountList[vp_set_reward_count.currentItem])
        }
    }


    private fun NumberPicker.initDividerColor() {
        val clazz = NumberPicker::class.java
        clazz.getDeclaredField("mSelectionDivider").apply {
            isAccessible = true
            set(this@initDividerColor, ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initDayDisplayName(): Array<String> {
        val now = GregorianCalendar()
        return Array(MAX_DAY) {
            if (it == 0) {
                if (showToday) {
                    "今天"
                } else {
                    now.apply { add(Calendar.DAY_OF_MONTH, 1) }
                            .time
                            .toFormatString("yyyy-MM-dd")
                }
            } else {
                now.apply { add(Calendar.DAY_OF_MONTH, 1) }
                        .time
                        .toFormatString("yyyy-MM-dd")
            }
        }
    }

    private fun initDaysCommitName(): Array<String> {
        return if (showToday) {
            Array(MAX_DAY) {
                if (it == 0) {
                    now.time.toFormatString("yyyy-MM-dd")
                } else
                    now.apply { add(Calendar.DAY_OF_MONTH, 1) }
                            .time
                            .toFormatString("yyyy-MM-dd")
            }
        } else {
            Array(MAX_DAY) {
                now.apply { add(Calendar.DAY_OF_MONTH, 1) }
                        .time
                        .toFormatString("yyyy-MM-dd")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.findViewById<View>(R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent)
        mBehavior.peekHeight = container.measuredHeight
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED

    }
}