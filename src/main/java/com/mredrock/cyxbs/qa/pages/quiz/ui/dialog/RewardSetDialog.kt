package com.mredrock.cyxbs.qa.pages.quiz.ui.dialog

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.AppCompatCheckedTextView
import com.mredrock.cyxbs.qa.R
import kotlinx.android.synthetic.main.qa_dialog_quiz_reward_set.*

/**
 * Created By jay68 on 2018/11/27.
 */
class RewardSetDialog(context: Context, rewardCount: Int) : BottomSheetDialog(context) {
    companion object {
        @JvmStatic
        private val REWARD_LIST = intArrayOf(1, 2, 3, 5, 10, 15)
    }

    private val checkedTextViewGroup: List<AppCompatCheckedTextView>
    private var preCheckedTextViewIndex = 0
    private val myRewardCount = rewardCount

    var onSubmitButtonClickListener: (Int) -> Unit = {}

    init {
        setContentView(R.layout.qa_dialog_quiz_reward_set)
        tv_quiz_reward_set_dialog_cancel.setOnClickListener { dismiss() }
        tv_quiz_reward_set_dialog_sure.setOnClickListener { onSubmitButtonClickListener(REWARD_LIST[preCheckedTextViewIndex]) }

        checkedTextViewGroup = ArrayList()
        forEach({
            if (it is AppCompatCheckedTextView) {
                checkedTextViewGroup += it
            }
        })
        checkedTextViewGroup[0].isChecked = true
        preCheckedTextViewIndex = 0
        tv_reward_set_cur_question_reward.text = checkedTextViewGroup[0].resources.getString(R.string.qa_quiz_reward_set_cur_question_reward, REWARD_LIST[0])
        tv_reward_set_my_reward.text = checkedTextViewGroup[0].resources.getString(R.string.qa_quiz_reward_set_my_reward, myRewardCount)

        checkedTextViewGroup.forEachIndexed { index, tv ->
            tv.text = REWARD_LIST[index].toString()
            tv.setOnClickListener {
                if (index != preCheckedTextViewIndex) {
                    tv.toggle()
                    checkedTextViewGroup[preCheckedTextViewIndex].toggle()
                    preCheckedTextViewIndex = index
                    val curReward = REWARD_LIST[index]
                    tv_reward_set_cur_question_reward.text = it.resources.getString(R.string.qa_quiz_reward_set_cur_question_reward, curReward)
                }
            }
        }
    }
}