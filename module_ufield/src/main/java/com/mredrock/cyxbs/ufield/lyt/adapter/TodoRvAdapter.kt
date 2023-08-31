package com.mredrock.cyxbs.ufield.lyt.adapter

import android.os.Build
import android.util.Log
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
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lyt.bean.TodoBean
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

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



    // 1秒内防止多次点击
    private val minDelayTime = 1000
    private var lastClickTime: Long = 0


    inner class RvTodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val actName: TextView = itemView.findViewById(R.id.uField_todo_activity_name)
        private val actTime: TextView = itemView.findViewById(R.id.uField_todo_activity_time)
        private val actType: TextView = itemView.findViewById(R.id.uField_todo_activity_type)
        private val actAuthor: TextView = itemView.findViewById(R.id.uField_todo_activity_author)
        private val actPhone: TextView = itemView.findViewById(R.id.uField_todo_activity_phone)
        private val actPass: Button = itemView.findViewById(R.id.uField_todo_btn_accept)
        private val actReject: Button = itemView.findViewById(R.id.uField_todo_btn_reject)
        private val actItem: ConstraintLayout = itemView.findViewById(R.id.ufield_check_constraintlayout)

        init {
            actPass.setOnClickListener {
                val currentTime: Long = Calendar.getInstance().timeInMillis
                if (currentTime - lastClickTime > minDelayTime) {
                    mOnPassClick?.invoke(absoluteAdapterPosition)
                    lastClickTime = currentTime;
                } else {
                    toast("短时间内请不要重复点击按钮哦！")
                }
            }
            actReject.setOnClickListener {
                val currentTime: Long = Calendar.getInstance().timeInMillis
                if (currentTime - lastClickTime > minDelayTime) {
                    mRejectClick?.invoke(absoluteAdapterPosition)
                    lastClickTime = currentTime;
                } else {
                    toast("短时间内请不要重复点击按钮哦！")
                }
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
            actName.text = itemData.activity_title
            actTime.text = timeFormat(itemData.activity_create_timestamp)
            actType.text = itemData.activity_type
            actAuthor.text = itemData.activity_creator
            actPhone.text = itemData.activity_phone
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
               // .format(DateTimeFormatter.ofPattern("HH:mm:ss"))//测试用
        }
    }


    class RvTodoDiffCallback : DiffUtil.ItemCallback<TodoBean>() {
        override fun areItemsTheSame(oldItem: TodoBean, newItem: TodoBean): Boolean {
            return oldItem == newItem
        }

        /**
         * 通过数据类中的一个特征值来比较
         */
        override fun areContentsTheSame(oldItem: TodoBean, newItem: TodoBean): Boolean {
            return oldItem.activity_id == newItem.activity_id
        }

    }


}

