package com.mredrock.cyxbs.qa.pages.question.ui.adapter

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_header_fresh_man_questiond.view.*

/**
 * Created by yyfbe, Date on 2020/8/11.
 * 迎新生的fragment的rv的header
 */
class FreshManHeaderRvAdapter(private val adapter: FreshManHeaderInnerVpAdapter, private val askQuestion: () -> Unit) : BaseRvAdapter<Int>() {
    override fun onBindViewHolder(holder: BaseViewHolder<Int>, position: Int) {
        super.onBindViewHolder(holder, position)
        val root = holder.itemView as LinearLayout
        val recyclerView: RecyclerView = root.rv_freshman_hot
        root.tv_hot_question_more.setOnClickListener {
            holder.itemView.context?.doIfLogin {
                askQuestion.invoke()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.apply {
            layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun getItemCount(): Int = 1

    class FreshManViewHolder(parent: ViewGroup) : BaseViewHolder<Int>(parent, R.layout.qa_recycler_header_fresh_man_questiond) {
        override fun refresh(data: Int?) {
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Int> = FreshManViewHolder(parent)

}