package com.mredrock.cyxbs.course.component

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.common.component.RedRockAutoWarpView
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.WeekSelectRecAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentWeekSelectBinding
import com.mredrock.cyxbs.course.utils.weekSelectCheckBoxState
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener

/**
 * Created by anriku on 2018/9/8.
 */

class WeekSelectDialog(context: Context, val weekSelect: RedRockAutoWarpView, val mPostWeeks: MutableList<Int>) : RedRockBottomSheetDialog(context) {

    private var mBinding: CourseFragmentWeekSelectBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.course_fragment_week_select,
            null, false)
    private var adapter: WeekSelectRecAdapter = WeekSelectRecAdapter(mPostWeeks)

    init {
        mBinding.courseAuto.adapter = adapter
        mBinding.fragment = this
        mBinding.tvSure.setOnSingleClickListener {
            mPostWeeks.clear()
            for ((k, v) in adapter.checkBoxMap) {
                if (v.isChecked) {
                    if (k == 0) {
                        for (i in 1..21) {
                            mPostWeeks.add(i)
                        }
                        break
                    }
                    mPostWeeks.add(k)
                }
            }
            weekSelect.refreshData()
            dismiss()
        }
        setContentView(mBinding.root)
    }

    override fun show() {
        super.show()
        for ((k, v) in adapter.checkBoxMap) {
            v.isChecked = false
            weekSelectCheckBoxState(v, context)
            if (mPostWeeks.size == 21) {
                if (k == 0) {
                    v.isChecked = true
                    weekSelectCheckBoxState(v, context)
                }
            } else {
                mPostWeeks.forEach {
                    if (it == k) {
                        v.isChecked = true
                        weekSelectCheckBoxState(v, context)
                    }
                }
            }
        }
    }
}