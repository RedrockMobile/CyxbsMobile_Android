package com.mredrock.cyxbs.qa.pages.quiz.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.pages.quiz.ui.adpter.RewardExplainAdapter
import kotlinx.android.synthetic.main.qa_dialog_qa_reward_description.*

/**
 * Created by yyfbe, Date on 2020-02-08.
 */
class RewardDescriptionDialog(context: Context, private val contentList: List<DownMessageText>) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_dialog_qa_reward_description)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rv_reward_explain_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RewardExplainAdapter().apply {
                refreshData(contentList)
            }
        }
    }
}