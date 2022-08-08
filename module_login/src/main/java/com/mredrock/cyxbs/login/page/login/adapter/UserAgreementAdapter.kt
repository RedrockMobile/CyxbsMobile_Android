package com.mredrock.cyxbs.login.page.login.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.network.DownMessage
import com.mredrock.cyxbs.login.R

/**
 * @author Jovines
 * @create 2020-04-12 6:05 PM
 *
 * 描述:用户协议的adapter
 */

class UserAgreementAdapter(
    private val list: List<DownMessage.DownMessageText>
) : RecyclerView.Adapter<UserAgreementAdapter.UserAgreementVH>() {
    
    class UserAgreementVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.login_tv_rv_item_user_agreement_title)
        val tvContent: TextView = itemView.findViewById(R.id.login_tv_rv_item_user_agreement_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAgreementVH {
        return UserAgreementVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.login_rv_item_user_agreement, parent, false)
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: UserAgreementVH, position: Int) {
        holder.apply {
            restoredToTheirOriginal(tvTitle, tvContent)
            when (list[position].title) {
                "title" -> {
                    tvTitle.apply {
                        textSize = 20f
                        gravity = Gravity.CENTER
                        text = list[position].content
                    }
                    tvContent.visibility = View.GONE
                }
                "time" -> {
                    tvContent.text = list[position].content
                    tvTitle.visibility = View.GONE
                }
                else -> {
                    //ß这个字符是我在下发文档里面嵌入的标识符，表示需要加上双tab
                    tvTitle.text = list[position].title.replace("ß", "      ")
                    tvContent.text = list[position].content.replace("ß", "      ")
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