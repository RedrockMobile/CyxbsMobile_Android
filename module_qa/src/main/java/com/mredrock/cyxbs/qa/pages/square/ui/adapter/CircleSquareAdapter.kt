package com.mredrock.cyxbs.qa.pages.square.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic
import de.hdodenhof.circleimageview.CircleImageView

/**
 *@Date 2020-11-19
 *@Time 19:22
 *@author SpreadWater
 *@description
 */
class CircleSquareAdapter :
    ListAdapter<Topic, CircleSquareAdapter.ViewHolder>(CircleSquareDiffCallback()) {

    /**
     * Item点击方法回调
     */
    var itemClick: ((Topic, View) -> Unit)? = null

    /**
     * 关注按钮点击方法回调
     */
    var concernClick: ((String, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.qa_recycler_item_circle_square, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        // 找到控件，这里没用dataBinding，会增加编译时间
        private val circleAvatar: CircleImageView = itemView.findViewById(R.id.iv_circle_square)
        private val circleName: TextView = itemView.findViewById(R.id.tv_circle_square_name)
        private val circleDesc: TextView = itemView.findViewById(R.id.tv_circle_square_description)
        private val circlePersonNum: TextView =
            itemView.findViewById(R.id.tv_circle_square_person_number)
        private val circleConcern: AppCompatTextView =
            itemView.findViewById(R.id.btn_circle_square_concern)

        init {
            // 在ViewHolder中设置点击事件，减少性能损耗
            itemView.setOnClickListener {
                Log.d("TAG","(CircleSquareAdapter.kt:64)->${getItem(adapterPosition)}")
                itemClick?.invoke(getItem(layoutPosition), it)
            }
            circleConcern.setOnClickListener {
                val item = getItem(adapterPosition)
                // 更新关注UI
                if (item._isFollow == 1) {
                    item._isFollow = 0
                    circleConcern.text = "+关注"
                    circleConcern.background = context.getDrawable(
                        R.drawable.qa_shape_send_dynamic_btn_blue_background
                    )
                } else {
                    item._isFollow = 1
                    circleConcern.text = "已关注"
                    circleConcern.background = context.getDrawable(
                        R.drawable.qa_shape_send_dynamic_btn_grey_background
                    )
                }
                concernClick?.invoke(item.topicName, item._isFollow == 0)
            }
        }

        // 绑定数据
        fun bind(data: Topic?) {
            circleAvatar.setAvatarImageFromUrl(data?.topicLogo)
            circleName.text = data?.topicName
            circleDesc.text = data?.introduction
            circlePersonNum.text = data?.follow_count.toString() + "个成员"
            data?._isFollow?.let {
                if (it == 1) {
                    circleConcern.text = "已关注"
                    circleConcern.background = context.getDrawable(
                        R.drawable.qa_shape_send_dynamic_btn_grey_background
                    )
                } else {
                    circleConcern.text = "+关注"
                    circleConcern.background = context.getDrawable(
                        R.drawable.qa_shape_send_dynamic_btn_blue_background
                    )
                }
            }
        }
    }
}

/**
 * 圈子广场List的DiffUtil
 */
class CircleSquareDiffCallback : DiffUtil.ItemCallback<Topic>() {
    override fun areItemsTheSame(oldItem: Topic, newItem: Topic) =
        oldItem.topicId == newItem.topicId

    override fun areContentsTheSame(oldItem: Topic, newItem: Topic) =
        oldItem == newItem
}