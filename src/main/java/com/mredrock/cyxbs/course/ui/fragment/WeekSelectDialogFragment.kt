package com.mredrock.cyxbs.course.ui.fragment

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.WeekSelectRecAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentWeekSelectBinding
import com.mredrock.cyxbs.course.ui.activity.AffairEditActivity
import com.mredrock.cyxbs.course.utils.weekSelectCheckBoxState
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel
import kotlinx.android.synthetic.main.course_activity_edit_affair.*

/**
 * Created by anriku on 2018/9/8.
 */

class WeekSelectDialogFragment(context: Context) : RedRockBottomSheetDialog(context) {

    private var mBinding: CourseFragmentWeekSelectBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.course_fragment_week_select,
            null, false)
    private var mEditAffairViewModel: EditAffairViewModel = ViewModelProvider(context as AppCompatActivity).get(EditAffairViewModel::class.java)
    private var adapter: WeekSelectRecAdapter = WeekSelectRecAdapter(context as AppCompatActivity)

    init {
        mBinding.courseAuto.adapter = adapter
        mBinding.fragment = this
        mBinding.tvSure.setOnClickListener {
            mEditAffairViewModel.mPostWeeks.clear()
            for ((k, v) in adapter.checkBoxMap) {
                if (v.isChecked) {
                    if (k == 0) {
                        for (i in 1..21) {
                            mEditAffairViewModel.mPostWeeks.add(i)
                        }
                        break
                    }
                    mEditAffairViewModel.mPostWeeks.add(k)
                }
            }
            (context as AffairEditActivity).tv_week_select.adapter?.view?.refreshData()
            dismiss()
        }
        setContentView(mBinding.root)
    }

    override fun show() {
        super.show()
        for ((k, v) in adapter.checkBoxMap) {
            v.isChecked = false
            weekSelectCheckBoxState(v, context)
            if (mEditAffairViewModel.mPostWeeks.size == 21) {
                if (k == 0) {
                    v.isChecked = true
                    weekSelectCheckBoxState(v, context)
                }
            } else {
                mEditAffairViewModel.mPostWeeks.forEach {
                    if (it == k) {
                        v.isChecked = true
                        weekSelectCheckBoxState(v, context)
                    }
                }
            }
        }
    }
}