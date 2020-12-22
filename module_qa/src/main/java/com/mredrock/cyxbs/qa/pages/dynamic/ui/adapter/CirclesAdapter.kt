package com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.beannew.Topic
import com.mredrock.cyxbs.qa.beannew.TopicMessage

import com.mredrock.cyxbs.qa.pages.square.ui.activity.CircleSquareActivity
import com.mredrock.cyxbs.qa.ui.widget.ImageViewAddCount
import kotlinx.android.synthetic.main.qa_recycler_item_circles.view.*


/**
 * @Author: xgl
 * @ClassName: CirclesAdapter
 * @Description:
 * @Date: 2020/11/18 23:18
 */
class CirclesAdapter(private val onItemClickEvent: (Topic,View) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                return CirclesItem(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            NO_CIRCLE -> {
                val viewHolder = holder as NoCircleItem
                viewHolder.itemView.setOnSingleClickListener {
                    changeToActivity(CircleSquareActivity())
                }
            }
            HAVE_CIRCLE -> {
                val viewHolder = holder as CirclesItem
                viewHolder.itemView.apply {
                    setOnSingleClickListener {
                        qa_iv_circle.setHaveMessage(false)
                        onItemClickEvent(circlesItemList[position],viewHolder.itemView.findViewById<LinearLayout>(R.id.qa_ll_topic))
                    }
                    qa_iv_circle.apply {
                        setHaveMessage(true)
                        setMessageBum(circlesItemList[position].newMesCount)
                    }
                    qa_tv_circle_name.text = circlesItemList[position].topicName
                }
            }
        }
    }

    private fun changeToActivity(activity: Activity) {
        val intent = Intent(BaseApp.context, activity::class.java)
        BaseApp.context.startActivity(intent)
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

    fun addTopicMessageData(newTopicMessageData: List<TopicMessage>) {
        topicMessageList.clear()
        topicMessageList.addAll(newTopicMessageData)
    }
}