package com.mredrock.cyxbs.freshman.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.bean.EnrollmentRequirementsItemBean
import com.mredrock.cyxbs.freshman.bean.EnrollmentRequirementsTitleBean
import com.mredrock.cyxbs.freshman.interfaces.ParseBean
import com.mredrock.cyxbs.freshman.util.event.MemorandumBookItemSelectedEvent
import com.mredrock.cyxbs.freshman.util.event.MemorandumBookItemUnSelectedEvent
import com.mredrock.cyxbs.freshman.view.activity.AddMemorandumBookActivity
import org.greenrobot.eventbus.EventBus

/**
 * Create by yuanbing
 * on 2019/8/10
 */
class EditMemorandumBookAdapter(val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mMemorandumBook: List<ParseBean> = listOf()
    private val header = 0
    private val item = 1
    private val hint = 2
    private val mSelectedItemIndex: HashSet<Int> = HashSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            header -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.freshman_recycle_item_enrollment_requirements_title, parent, false)
                EnrollmentRequirementsTitleViewHolder(view)
            }
            item -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.freshman_recycle_item_enrollment_requirements_item, parent, false)
                EnrollmentRequirementsItemViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.freshman_recycle_item_edit_memorandum_add_hint, parent, false)
                AddHintViewHolder(view)
            }
        }
    }

    override fun getItemCount() = if (mMemorandumBook.isEmpty()) 1 else mMemorandumBook.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            getItemViewType(position) == header -> {
                holder as EnrollmentRequirementsTitleViewHolder
                val memorandumBook = mMemorandumBook[position] as EnrollmentRequirementsTitleBean
                holder.mTitle.text = memorandumBook.title
            }
            getItemViewType(position) == item -> {
                holder as EnrollmentRequirementsItemViewHolder
                val memorandumBook = mMemorandumBook[position] as EnrollmentRequirementsItemBean
                holder.mDescription.gone()
                holder.mSwitch.gone()
                holder.mName.text = memorandumBook.name
                val isSelected = mSelectedItemIndex.contains(position)
                if (isSelected) {
                    holder.mTag.setImageResource(
                            R.drawable.freshman_recycle_item_edit_memorandum_book_selected)
                } else {
                    holder.mTag.setImageResource(
                            R.drawable.freshman_recycle_item_enrollment_requirements_todo)
                }
                holder.itemView.setOnClickListener {
                    if (isSelected) {
                        EventBus.getDefault().post(MemorandumBookItemUnSelectedEvent(memorandumBook.name))
                        mSelectedItemIndex.remove(position)
                    } else {
                        EventBus.getDefault().post(MemorandumBookItemSelectedEvent(memorandumBook.name))
                        mSelectedItemIndex.add(position)
                    }
                    notifyItemChanged(position)
                }
            }
            else -> {
                holder as AddHintViewHolder
                holder.mHint.setOnClickListener {
                    val intent = Intent(activity, AddMemorandumBookActivity::class.java)
                    activity.startActivityForResult(intent, 0)
                }
            }
        }
    }

    override fun getItemViewType(position: Int) = if (mMemorandumBook.isEmpty()) {
        hint
    } else {
        if (mMemorandumBook[position] is EnrollmentRequirementsTitleBean) header else item
    }

    fun refreshData(memorandumBook: List<ParseBean>) {
        mMemorandumBook = memorandumBook
        clearAllSelectedItem()
    }

    fun clearAllSelectedItem() {
        mSelectedItemIndex.clear()
        notifyDataSetChanged()
    }
}

class AddHintViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mHint: TextView = view.findViewById(R.id.tv_edit_memorandum_book_add_hint)
}