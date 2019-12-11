package com.mredrock.cyxbs.qa.pages.quiz.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.NumberPicker
import android.widget.RelativeLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.quiz.ui.RewardSetAdapter
import kotlinx.android.synthetic.main.qa_dialog_question_reward_set.*
import top.limuyang2.photolibrary.util.getScreenWidth
import kotlin.math.abs
import kotlin.math.pow

class RewardSetDialog(context: Context, rewardCount: Int) : BottomSheetDialog(context) {
    var onSubmitButtonClickListener: (Int) -> Unit = {}
    private val myRewardCount = rewardCount
    private var mBehavior: BottomSheetBehavior<View>
    @SuppressLint("InflateParams")
    private val container: View = layoutInflater.inflate(R.layout.qa_dialog_question_reward_set, null)

    init {
        setContentView(container)
        mBehavior = BottomSheetBehavior.from(container.parent as View)
        initPickerView()
        vp_set_reward_count.adapter = RewardSetAdapter()
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

        }

        tv_my_reward_count.text = context.resources.getString(R.string.qa_quiz_reward_set_my_reward, myRewardCount)
        tv_quiz_dialog_cancel.setOnClickListener { cancel() }
    }

    private fun initPickerView() {
        vp_quiz_day.apply {
            initDividerColor()
            minValue = 0
            maxValue = 2
            displayedValues = arrayOf("今天", "明天", "后天")
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
        vp_quiz_hour.apply {
            initDividerColor()
            minValue = 0
            maxValue = 23
            displayedValues = Array(24) { it.toString() }
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
        vp_quiz_minute.apply {
            initDividerColor()

            minValue = 0
            maxValue = 59
            displayedValues = Array(60) { it.toString() }
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

    private fun NumberPicker.initDividerColor() {
        val clazz = NumberPicker::class.java
        clazz.getDeclaredField("mSelectionDivider").apply {
            isAccessible = true
            set(this@initDividerColor, ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onStart() {
        super.onStart()

        window?.findViewById<View>(R.id.design_bottom_sheet)?.setBackgroundResource(R.drawable.qa_question_more_dialog_head_background)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun show() {
        super.show()
        mBehavior.peekHeight = container.measuredHeight
    }
}