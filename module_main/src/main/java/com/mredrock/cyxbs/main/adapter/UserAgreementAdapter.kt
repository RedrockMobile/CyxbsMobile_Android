package com.mredrock.cyxbs.main.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.down.bean.DownMessageText
import com.mredrock.cyxbs.main.R
import kotlinx.android.synthetic.main.main_recycle_item_user_agreement.view.*

/**
 * @author Jovines
 * @create 2020-04-12 6:05 PM
 *
 * 描述:用户协议的adapter
 */

class UserAgreementAdapter(private val list: List<DownMessageText>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_recycle_item_user_agreement, parent, false)) {}
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            restoredToTheirOriginal(user_agreement_title, user_agreement_content)
            when (list[position].title) {
                "title" -> {
                    user_agreement_title.apply {
                        textSize = 20f
                        gravity = Gravity.CENTER
                        text = list[position].content
                    }
                    user_agreement_content.visibility = View.GONE
                }
                "time" -> {
                    user_agreement_content.text = list[position].content
                    user_agreement_title.visibility = View.GONE
                }
                else -> {
                    //ß这个字符是我在下发文档里面嵌入的标识符，表示需要加上双tab
                    user_agreement_title.text = list[position].title.replace("ß", "      ")
                    user_agreement_content.text = list[position].content.replace("ß", "      ")
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