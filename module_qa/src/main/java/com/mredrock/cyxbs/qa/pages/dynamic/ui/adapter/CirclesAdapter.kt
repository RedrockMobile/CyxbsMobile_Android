package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.common.utils.extensions.setAvatarImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.beannew.TopicMessage
import com.mredrock.cyxbs.qa.pages.dynamic.ui.fragment.DynamicFragment
import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleSquareActivity
import com.mredrock.cyxbs.qa.ui.widget.ImageViewAddCount
import kotlinx.android.synthetic.main.qa_recycler_item_circles.view.*


/**
 * @Author: xgl
 * @ClassName: CirclesAdapter
 * @Description:
 * @Date: 2020/11/18 23:18
 */
class CirclesAdapter(private val onItemClickEvent: (Topic, View) -> Unit, private val fragment: DynamicFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val NO_CIRCLE = 0
        const val HAVE_CIRCLE = 1
    }

    private val circlesItemList = ArrayList<Topic>()
    private val topicMessageList = ArrayList<TopicMessage>()

    class NoCircleItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_add_circles: ImageView = itemView.findViewById(R.id.qa_iv_add_circles)
    }

    class CirclesItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_circle: ImageViewAddCount = itemView.findViewById(R.id.qa_iv_circle)
        val tv_circle_name: TextView = itemView.findViewById(R.id.qa_tv_circle_name)
    }

    override fun getItemViewType(position: Int): Int {
        return if (circlesItemList.size == 0)
            NO_CIRCLE
        else
            HAVE_CIRCLE

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NO_CIRCLE -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.qa_recycler_item_no_circles,
                        parent,
                        false
                )
                return NoCircleItem(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.qa_recycler_item_circles,
                        parent,
                        false
                )
                return CircleListViewHolder(view)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            NO_CIRCLE -> {
                val viewHolder = holder as NoCircleItem
                viewHolder.itemView.setOnSingleClickListener {
                    CircleSquareActivity.activityStartFromDynamic(fragment)
                }
            }
            HAVE_CIRCLE -> {
                val viewHolder = holder as CircleListViewHolder
                viewHolder.itemView.apply {
                    setOnSingleClickListener {
                        circlesItemList[position].post_count = 0
                        onItemClickEvent(circlesItemList[position], viewHolder.itemView.findViewById<LinearLayout>(R.id.qa_ll_topic))
                    }
                    qa_iv_circle.apply {

                        setAvatarImageFromUrl(circlesItemList[position].topicLogo)
                        setHaveMessage(true)
                        topicMessageList.forEach { topicMessage ->
                            if (circlesItemList[position].topicId == topicMessage.topic_id)
                                setMessageBum(topicMessage.post_count)
                        }
                    }
                    if (circlesItemList[position].topicName.length <= 4)
                        qa_tv_circle_name.text = circlesItemList[position].topicName
                    else
                        qa_tv_circle_name.text = circlesItemList[position].topicName.substring(0, 4) + "..."
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (circlesItemList.size == 0)
            1
        else
            circlesItemList.size

    }

    fun addCircleData(newData: List<Topic>) {
        circlesItemList.clear()
        circlesItemList.addAll(newData)
        notifyDataSetChanged()
    }

    fun noticeChangeCircleData() {
        circlesItemList.clear()
        notifyDataSetChanged()
    }


    fun addTopicMessageData(newTopicMessageData: List<TopicMessage>) {
        topicMessageList.clear()
        topicMessageList.addAll(newTopicMessageData)
        notifyDataSetChanged()
    }

    //保证单页可以显示4.5个圈子的ViewHolder
    class CircleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val width = BaseApp.context.getScreenWidth()//单位为px
            val wishWidth = width.toFloat() / 4.5//希望View达到的宽度
            val rawWidth = BaseApp.context.dp2px(70f)//内部ImageView的宽度，理解为最低宽度
            if (wishWidth >= rawWidth) {//保证这样除以之后的宽度大于77dp，防止view显示不全或者太挤
                val leftPadding = (wishWidth - rawWidth) / 2//期望情况下的padding
                val topPadding = BaseApp.context.dp2px(10f)
                itemView.setPadding(leftPadding.toInt(), topPadding, leftPadding.toInt(), 0)
            }
        }
    }
}