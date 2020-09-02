package com.mredrock.cyxbs.mine.util.ui

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_list_item_feature_intro.view.*

/**
 * copy from UserAgreementAdapter
 */

class DynamicRVAdapter(private val list: List<DownMessageText>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mine_list_item_feature_intro, parent, false)) {}
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            restoredToTheirOriginal(mine_about_rv_title, mine_about_rv_content)
            when (list[position].title) {
                "title" -> {
                    mine_about_rv_title.apply {
                        textSize = 20f
                        gravity = Gravity.CENTER
                        text = list[position].content
                    }
                    mine_about_rv_content.visibility = View.GONE
                }
                "time" -> {
                    mine_about_rv_content.text = list[position].content
                    mine_about_rv_title.visibility = View.GONE
                }
                else -> {
                    //ß这个字符是我在下发文档里面嵌入的标识符，表示需要加上双tab
                    mine_about_rv_title.text = list[position].title.replace("ß", "      ")
                    mine_about_rv_content.text = list[position].content.replace("ß", "      ")
                }
            }
        }
    }


    private fun restoredToTheirOriginal(title: TextView, content: TextView) {
        title.apply {
            gravity = Gravity.START
            textSize = 15f
            text = ""
            visibility = View.VISIBLE
        }
        content.apply {
            gravity = Gravity.START
            textSize = 15f
            text = ""
            visibility = View.VISIBLE
        }
    }
}