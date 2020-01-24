package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.WeekSelectRecAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentWeekSelectBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel

/**
 * Created by anriku on 2018/9/8.
 */

class WeekSelectDialogFragment(context: Context) : RedRockBottomSheetDialog(context) {

    private var mBinding: CourseFragmentWeekSelectBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.course_fragment_week_select,
            null, false)
    private var mEditAffairViewModel: EditAffairViewModel = ViewModelProviders.of(context as AppCompatActivity).get(EditAffairViewModel::class.java)

    init {
        mBinding.courseAuto.adapter = WeekSelectRecAdapter(context as AppCompatActivity)
        mBinding.fragment = this
        mBinding.tvSure.setOnClickListener {
            mEditAffairViewModel.setWeekSelectStringFromFragment()
            dismiss()
        }
        setContentView(mBinding.root)
    }
}