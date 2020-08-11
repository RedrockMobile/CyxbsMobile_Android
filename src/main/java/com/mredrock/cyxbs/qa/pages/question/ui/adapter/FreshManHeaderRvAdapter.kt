package com.mredrock.cyxbs.qa.pages.question.ui.adapter

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.qa_recycler_header_fresh_man_questiond.view.*

/**
 * Created by yyfbe, Date on 2020/8/11.
 * 迎新生的fragment的rv的header
 */
class FreshManHeaderRvAdapter(private val adapter: FreshManHeaderVpAdapter) : BaseRvAdapter<Int>() {
    override fun onBindViewHolder(holder: BaseViewHolder<Int>, position: Int) {
        super.onBindViewHolder(holder, position)
        val root = holder.itemView as LinearLayout
        val marginSize = root.context.getScreenWidth() / 2
        val exceptedWidth = root.context.getScreenWidth() / 10
        val lp: FrameLayout.LayoutParams? = root.vp_freshman_hot.layoutParams as FrameLayout.LayoutParams?
        lp?.setMargins(marginSize - exceptedWidth, 0, marginSize - exceptedWidth, 0)
        root.vp_freshman_hot.adapter = adapter
    }

    class FreshManViewHolder(parent: ViewGroup) : BaseViewHolder<Int>(parent, R.layout.qa_recycler_header_fresh_man_questiond) {
        override fun refresh(data: Int?) {
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Int> = FreshManViewHolder(parent)

}