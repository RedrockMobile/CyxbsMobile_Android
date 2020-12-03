package com.mredrock.cyxbs.mine.util.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.model.SecurityQuestion
import com.mredrock.cyxbs.mine.page.security.adapter.SelQuestionRVAdapter

/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 * describe: 设置密保问题时选择问题用的sheetDialog
 */
class SelQuestionDialog(context: Context, listOfSecurityQuestion: List<SecurityQuestion>, onClick: (Int) -> Unit) : RedRockBottomSheetDialog(context) {
    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.mine_fragment_security_choose_question, null, false)
        setContentView(dialogView)
        val questionRV = dialogView.findViewById<RecyclerView>(R.id.mine_rv_security_choose_question)
        val adapter = SelQuestionRVAdapter(listOfSecurityQuestion) {
            onClick(it)
            this.hide()
        }
        questionRV.adapter = adapter
        questionRV.layoutManager = LinearLayoutManager(context)
    }
}