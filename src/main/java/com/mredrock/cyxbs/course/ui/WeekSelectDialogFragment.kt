package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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

    init{
        mBinding.courseAuto.adapter = WeekSelectRecAdapter(context as AppCompatActivity)
            mBinding.listeners = WeekSelectListeners({
                dismiss()
            }, {
                mEditAffairViewModel.setWeekSelectStringFromFragment()
                dismiss()
            })
            setContentView(mBinding.root)
    }

    /**
     * 为WeekSelectDialogFragment中的控件设置点击事件
     * @param onCancel 给取消的ImageView设置点击事件
     * @param onSure 给确定的TextView设置点击事件
     */
    class WeekSelectListeners(val onCancel: (ImageView) -> Unit, val onSure: (TextView) -> Unit) {
        fun onCancelClick(cancel: View) {
            onCancel(cancel as ImageView)
        }

        fun onSureClick(sure: View) {
            onSure(sure as TextView)
        }
    }
}