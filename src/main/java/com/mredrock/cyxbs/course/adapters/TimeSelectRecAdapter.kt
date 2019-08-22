package com.mredrock.cyxbs.course.adapters

import android.annotation.SuppressLint
import android.widget.CheckBox
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel


/**
 * Created by anriku on 2018/9/10.
 */

@SuppressLint("PrivateResource")
class TimeSelectRecAdapter(mActivity: FragmentActivity) : BaseRecAdapter(mActivity) {

    companion object {
        private const val TAG = "TimeSelectRecAdapter"
    }

    private val mEditAffairViewModel: EditAffairViewModel =
            ViewModelProviders.of(mActivity).get(EditAffairViewModel::class.java)

    override fun getThePositionLayoutId(position: Int): Int {
        return R.layout.course_time_select_rec_item
    }

    override fun getItemCount(): Int {
        return 7 * 6
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.cb)

        if (mEditAffairViewModel.isSelectedTimeViews.indexOfKey(position) >= 0) {
            checkBox.isChecked = true
            //替换掉上次打开TimeSelectDialogFragment时存储的对应的CheckBox对象
            mEditAffairViewModel.isSelectedTimeViews.put(position, checkBox)
        }

        checkBox.setOnCheckedChangeListener { cb, isChecked ->
            val isSelectedWeekViews = mEditAffairViewModel.isSelectedTimeViews
            if (isChecked) {
                isSelectedWeekViews.put(position, cb as CheckBox)
            } else {
                isSelectedWeekViews.remove(position)
            }
        }

    }
}