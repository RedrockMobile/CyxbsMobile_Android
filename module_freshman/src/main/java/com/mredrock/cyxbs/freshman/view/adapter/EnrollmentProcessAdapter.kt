package com.mredrock.cyxbs.freshman.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.EnrollmentProcessText
import com.mredrock.cyxbs.freshman.view.activity.showPhotosToScenery

/**
 * Create by yuanbing
 * on 2019/8/3
 */
class EnrollmentProcessAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mEnrollmentProcess: List<EnrollmentProcessText> = listOf()
    private val header = 0
    private val item = 1
    private var mCurrentOpenedItemIndex = -1
    private var mPreviousOpenedItemIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == header) {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.freshman_recycle_item_enrollment_process_header, parent, false)
            HeaderEnrollmentProcessViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.freshman_recycle_item_enrollment_process_item, parent, false)
            ItemEnrollmentProcessViewHolder(view)
        }
    }

    override fun getItemCount() = mEnrollmentProcess.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == header) {
            bindHeader(holder as HeaderEnrollmentProcessViewHolder, mEnrollmentProcess[position])
        } else {
            bindItem(holder as ItemEnrollmentProcessViewHolder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) header else item
    }

    @SuppressLint("SetTextI18n")
    private fun bindHeader(holder: HeaderEnrollmentProcessViewHolder,
                           headerText: EnrollmentProcessText) {
        holder.mTime.text = "${headerText.title}： ${headerText.message}"
    }

    private fun bindItem(holder: ItemEnrollmentProcessViewHolder, position: Int) {
        val itemText = mEnrollmentProcess[position]
        fun open() {
            holder.mShowDetail.setImageResource(R.drawable.freshman_recycle_item_open)
            holder.mDetail.visible()
            if (itemText.photo.isBlank()) {
                holder.mPhoto.gone()
            } else {
                holder.mPhoto.visible()
            }
        }

        fun close() {
            holder.mShowDetail.setImageResource(R.drawable.freshman_recycle_item_close)
            holder.mDetail.gone()
            holder.mPhoto.gone()
        }
        holder.mMessage.text = itemText.title
        holder.mDetail.text = itemText.detail

        holder.mPhoto.setImageFromUrl(itemText.photo)
        holder.mPhoto.setOnClickListener {
            showPhotosToScenery(holder.itemView.context, listOf(itemText.photo))
        }
        if (itemText.detail.isNotBlank()) close()
        if (mPreviousOpenedItemIndex == position) {

            close()
        }
        if (mCurrentOpenedItemIndex == position) {
            open()
        }
        holder.itemView.setOnClickListener {
            if (mCurrentOpenedItemIndex == position) {
                mPreviousOpenedItemIndex = position
                mCurrentOpenedItemIndex = -1
            } else {
                mPreviousOpenedItemIndex = mCurrentOpenedItemIndex
                mCurrentOpenedItemIndex = position
            }
            notifyItemChanged(mPreviousOpenedItemIndex)
            notifyItemChanged(position)
        }
    }

    fun refreshData(enrollmentProcess: List<EnrollmentProcessText>) {
        mEnrollmentProcess = enrollmentProcess
        notifyDataSetChanged()
    }
}

class HeaderEnrollmentProcessViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mTime: TextView = view.findViewById(R.id.tv_recycle_item_enrollment_process_time)
}

class ItemEnrollmentProcessViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mMessage: TextView = view.findViewById(R.id.tv_recycle_item_enrollment_process_message)
    val mDetail: TextView = view.findViewById(R.id.tv_recycle_item_enrollment_process_detail)
    val mShowDetail: ImageView = view.findViewById(R.id.iv_recycle_item_enrollment_process_open_or_close)
    val mPhoto: ImageView = view.findViewById(R.id.iv_recycle_item_enrollment_process_photo)
}