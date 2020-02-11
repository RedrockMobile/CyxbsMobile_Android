package com.mredrock.cyxbs.qa.pages.quiz.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.mredrock.cyxbs.qa.R

/**
 * Created by yyfbe, Date on 2020-02-08.
 */
class RewardDescriptionDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.qa_dialog_qa_reward_description)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}