package com.mredrock.cyxbs.course.adapters

import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.utils.weekSelectCheckBoxState
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel

/**
 * Created by anriku on 2018/9/9.
 * Correct by jon 2019-12-12 20:47
 * 描述:
 *   这个适配用于周数选择里面的AutoWarpView的适配器
 */
class WeekSelectRecAdapter(mActivity: FragmentActivity) : RedRockAutoWarpView.Adapter() {

    private val mEditAffairViewModel: EditAffairViewModel = ViewModelProvider(mActivity).get(EditAffairViewModel::class.java)

    private val mWeeks = mEditAffairViewModel.weekArray

    val checkBoxMap = HashMap<Int, CheckBox>()

    override fun getItemId(): Int {
        return R.layout.course_week_select_rec_item
    }

    override fun getItemCount(): Int {
        return mWeeks.size
    }

    override fun initItem(item: View, position: Int) {
        val checkBox = item.findViewById<CheckBox>(R.id.cb)
        checkBox.text = mWeeks[position]
        checkBoxMap[position] = checkBox
        mEditAffairViewModel.mPostWeeks.forEach {
            if (mEditAffairViewModel.mPostWeeks.size == 21 && position == 0) {
                checkBox.isChecked = true
                weekSelectCheckBoxState(checkBox, context)
            } else {
                if (it == position) {
                    checkBox.isChecked = true
                    weekSelectCheckBoxState(checkBox, context)
                }
            }
        }
        if (position == 0) {
            checkBox.setOnClickListener {
                weekSelectCheckBoxState(checkBox, context)
                if (checkBox.isChecked) {
                    for ((k, v) in checkBoxMap) {
                        if (k != 0) {
                            v.isChecked = false
                            weekSelectCheckBoxState(v, context)
                        }
                    }
                }
            }
        } else {
            checkBox.setOnClickListener {
                weekSelectCheckBoxState(checkBox, context)
                checkBoxMap[0]?.apply {
                    isChecked = false
                    weekSelectCheckBoxState(this, context)
                }
            }
        }
    }
}