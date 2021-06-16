package com.mredrock.cyxbs.qa.pages.square.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.component.recycler.BaseRvAdapter
import com.mredrock.cyxbs.qa.component.recycler.BaseViewHolder
import com.mredrock.cyxbs.qa.pages.square.viewmodel.CircleSquareViewModel
import kotlinx.android.synthetic.main.qa_recycler_item_circle_square.view.*

/**
 *@Date 2020-11-19
 *@Time 19:22
 *@author SpreadWater
 *@description
 */
class CircleSquareAdapter(val viewmodel: CircleSquareViewModel, private val onItemClickEvent: (Topic, View,Int) -> Unit) : BaseRvAdapter<Topic>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Topic> = CircleSquareViewHolder(parent)


    class CircleSquareViewHolder(parent: ViewGroup) : BaseViewHolder<Topic>(parent, R.layout.qa_recycler_item_circle_square) {
        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun refresh(data: Topic?) {
            itemView.iv_circle_square.setAvatarImageFromUrl(data?.topicLogo)
            itemView.tv_circle_square_name.text = data?.topicName
            itemView.tv_circle_square_descriprion.text = data?.introduction
            itemView.tv_circle_square_person_number.text = data?.follow_count.toString() + "个成员"
            if (data?._isFollow!=null) {
                if (data._isFollow.equals(1)) {
                    itemView.btn_circle_square_concern.text = "已关注"
                    itemView.btn_circle_square_concern.background = context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_grey_background)
                } else {
                    itemView.btn_circle_square_concern.text = "+关注"
                    itemView.btn_circle_square_concern.background = context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_blue_background)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onItemClickListener(holder: BaseViewHolder<Topic>, position: Int, data: Topic) {
        super.onItemClickListener(holder, position, data)
        onItemClickEvent.invoke(data,
                holder.itemView.findViewById<ConstraintLayout>(R.id.qa_ctl_topic),position )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: BaseViewHolder<Topic>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.btn_circle_square_concern.setOnClickListener {
            if (dataList[position]._isFollow.equals(1)) {
                //已经关注的状态点击是取消关注
                viewmodel.followTopic(dataList[position].topicName, dataList[position]._isFollow.equals(1))
                holder.itemView.btn_circle_square_concern.background = context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_blue_background)
                dataList[position]._isFollow=0
                dataList[position].follow_count=dataList[position].follow_count-1
                holder.itemView.btn_circle_square_concern.text = "+关注"
                notifyDataSetChanged()
            } else {
                viewmodel.followTopic(dataList[position].topicName, dataList[position]._isFollow.equals(1))
                holder.itemView.btn_circle_square_concern.background = context.getDrawable(R.drawable.qa_shape_send_dynamic_btn_grey_background)
                dataList[position]._isFollow=1
                dataList[position].follow_count=dataList[position].follow_count+1
                holder.itemView.btn_circle_square_concern.text = "已关注"
                notifyDataSetChanged()
            }
        }
    }
}