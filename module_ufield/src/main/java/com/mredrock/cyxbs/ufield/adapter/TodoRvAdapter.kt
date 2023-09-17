package com.mredrock.cyxbs.ufield.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.bean.TodoBean
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 *  author : lytMoon
 *  date : 2023/8/19 18:08
 *  description :负责展示待审核活动的Rv ListAdapter
 *  version ： 1.0
 */
class TodoRvAdapter :
    ListAdapter<TodoBean, TodoRvAdapter.RvTodoViewHolder>((RvTodoDiffCallback())) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvTodoViewHolder {
        return RvTodoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ufield_item_rv_todo, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RvTodoViewHolder, position: Int) {

        val itemData = getItem(position)
        holder.bind(itemData)
    }


    /**
     * 点击同意按钮的回调
     */

    private var mOnPassClick: ((Int) -> Unit)? = null

    /**
     * 点击驳回按钮的回调
     */
    private var mRejectClick: ((Int) -> Unit)? = null

    /**
     * 点击每一个item的回调
     */
    private var mItemClick: ((Int) -> Unit)? = null


    fun setOnPassClick(listener: (Int) -> Unit) {
        mOnPassClick = listener
    }

    fun setOnRejectClick(listener: (Int) -> Unit) {
        mRejectClick = listener
    }

    fun setOnItemClick(listener: (Int) -> Unit) {
        mItemClick = listener
    }

    inner class RvTodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val actName: TextView = itemView.findViewById(R.id.uField_todo_activity_name)
        private val actTime: TextView = itemView.findViewById(R.id.uField_todo_activity_time)
        private val actType: TextView = itemView.findViewById(R.id.uField_todo_activity_type)
        private val actAuthor: TextView = itemView.findViewById(R.id.uField_todo_activity_author)
        private val actPhone: TextView = itemView.findViewById(R.id.uField_todo_activity_phone)
        private val actPass: Button = itemView.findViewById(R.id.uField_todo_btn_accept)
        private val actReject: Button = itemView.findViewById(R.id.uField_todo_btn_reject)
        private val actItem: ConstraintLayout =
            itemView.findViewById(R.id.ufield_check_constraintlayout)

        init {
            /**
             * 当我们点击通过或者不通过活动的时候，列表会刷新，数组会改变。防止用户多次点击，这里使用单次点击的监听事件
             */

            actPass.setOnSingleClickListener {
                mOnPassClick?.invoke(absoluteAdapterPosition)
            }

            actReject.setOnSingleClickListener {
                mRejectClick?.invoke(absoluteAdapterPosition)
            }

            actItem.setOnClickListener {
                mItemClick?.invoke(absoluteAdapterPosition)
            }
        }

        /**
         * 进行视图的绑定
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(itemData: TodoBean) {
            actName.text = itemData.activityTitle
            actTime.text = timeFormat(itemData.activityCreateTimestamp)
            actType.text = itemData.activityType
            actAuthor.text = itemData.activityCreator
            actPhone.text = itemData.activityPhone
        }


        /**
         * 加工时间戳,把时间戳转化为“年.月.日”格式
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun timeFormat(time: Long): String {
            return Instant
                .ofEpochSecond(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        }
    }


    class RvTodoDiffCallback : DiffUtil.ItemCallback<TodoBean>() {
        override fun areItemsTheSame(oldItem: TodoBean, newItem: TodoBean): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(oldItem: TodoBean, newItem: TodoBean): Boolean {
            return oldItem.activityId == newItem.activityId && oldItem.activityCreator == newItem.activityCreator && oldItem.activityStartAt == newItem.activityStartAt
        }

    }


}

