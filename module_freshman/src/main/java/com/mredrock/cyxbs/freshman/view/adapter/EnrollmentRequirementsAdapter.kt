package com.mredrock.cyxbs.freshman.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.*
import com.mredrock.cyxbs.freshman.interfaces.ParseBean
import com.mredrock.cyxbs.freshman.util.MemorandumManager
import org.jetbrains.anko.textColor

/**
 * Create by yuanbing
 * on 2019/8/9
 */
class EnrollmentRequirementsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mEnrollmentRequirements: List<ParseBean> = listOf()
    private val header = 0
    private val item = 1
    private var mPreviousOpenedItemIndex = -1
    private var mCurrentOpenedItemIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == header) {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.freshman_recycle_item_enrollment_requirements_title, parent, false)
            EnrollmentRequirementsTitleViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.freshman_recycle_item_enrollment_requirements_item, parent, false)
            EnrollmentRequirementsItemViewHolder(view)
        }
    }

    override fun getItemCount() = mEnrollmentRequirements.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == header) {
            holder as EnrollmentRequirementsTitleViewHolder
            val enrollmentRequirements = mEnrollmentRequirements[position] as
                    EnrollmentRequirementsTitleBean
            holder.mTitle.text = enrollmentRequirements.title
        } else {
            bindItem(holder as EnrollmentRequirementsItemViewHolder, position)
        }
    }

    private fun bindItem(holder: EnrollmentRequirementsItemViewHolder, position: Int) {
        fun open() {
            holder.mSwitch.setImageResource(R.drawable.freshman_recycle_item_open)
            holder.mDescription.visible()
        }

        fun close() {
            holder.mSwitch.setImageResource(R.drawable.freshman_recycle_item_close)
            holder.mDescription.gone()
        }

        val enrollmentRequirements = mEnrollmentRequirements[position] as
                EnrollmentRequirementsItemBean
        holder.mDescription.text = enrollmentRequirements.detail
        holder.mName.text = enrollmentRequirements.name
        val status = enrollmentRequirements.status

        val itemViewOnClickListener = View.OnClickListener {
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

        holder.mTag.setOnClickListener {
            if (status == STATUS_TRUE_CUSTOM || status == STATUS_TRUE_MUST) {
                if (status == STATUS_TRUE_CUSTOM) {
                    MemorandumManager.undo(enrollmentRequirements.name)
                } else {
                    MemorandumManager.undoMust(enrollmentRequirements.name)
                }
            } else {
                enrollmentRequirements.status = -status
                if (mCurrentOpenedItemIndex == position) {
                    mCurrentOpenedItemIndex = -1
                    close()
                }
                if (status == STATUS_FALSE_CUSTOM) {
                    MemorandumManager.done(enrollmentRequirements.name)
                } else {
                    MemorandumManager.doMust(enrollmentRequirements.name)
                }
            }
            enrollmentRequirements.status = -status
            notifyItemChanged(position)
        }

        // 由事件的状态来决定view
        when (status) {
            STATUS_TRUE_CUSTOM, STATUS_TRUE_MUST -> {
                holder.mSwitch.gone()
                holder.mTag.setImageResource(R.drawable.freshman_recycle_item_enrollment_requirements_done)
                holder.mName.textColor = BaseApp.context.resources.getColor(
                        R.color.freshman_recycle_item_enrollment_requirements_item_name_done_text_color)
                holder.itemView.setOnClickListener(null)
            }
            STATUS_FALSE_MUST, STATUS_FALSE_CUSTOM -> {
                holder.mSwitch.visible()
                holder.mTag.setImageResource(R.drawable.freshman_recycle_item_enrollment_requirements_todo)
                holder.mName.textColor = BaseApp.context.resources.getColor(
                        R.color.freshman_recycle_item_enrollment_requirements_item_name_text_color)
                holder.itemView.setOnClickListener(itemViewOnClickListener)
            }
        }

        if (enrollmentRequirements.detail.isBlank()) {
            holder.mSwitch.gone()
            holder.mDescription.gone()
            holder.itemView.setOnClickListener(null)
        } else {
            close()
        }

        if (mPreviousOpenedItemIndex == position) {
            close()
        }
        if (mCurrentOpenedItemIndex == position) {
            open()
        }
    }

    override fun getItemViewType(position: Int) = if (mEnrollmentRequirements[position] is
                    EnrollmentRequirementsTitleBean) header else item

    fun refreshData(enrollmentRequirements: List<ParseBean>) {
        mEnrollmentRequirements = enrollmentRequirements
        notifyDataSetChanged()
    }
}

class EnrollmentRequirementsTitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mTitle: TextView = view.findViewById(R.id.tv_recycle_item_enrollment_requirements_title)
}

class EnrollmentRequirementsItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mTag: ImageView = view.findViewById(R.id.iv_recycle_item_enrollment_requirements_select)
    val mSwitch: ImageView = view.findViewById(R.id.iv_recycle_item_enrollment_requirements_item_open_or_close)
    val mName: TextView = view.findViewById(R.id.tv_recycle_item_enrollment_requirements_name)
    val mDescription: TextView = view.findViewById(R.id.tv_recycle_item_enrollment_requirements_item_description)
}