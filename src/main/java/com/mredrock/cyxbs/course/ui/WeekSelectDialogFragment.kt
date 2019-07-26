package com.mredrock.cyxbs.course.ui

import android.app.Dialog
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.appcompat.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.WeekSelectRecAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentWeekSelectBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel

/**
 * Created by anriku on 2018/9/8.
 */

class WeekSelectDialogFragment : AppCompatDialogFragment() {

    private lateinit var mBinding: CourseFragmentWeekSelectBinding
    private lateinit var mBottomSheetDialog: BottomSheetDialog
    private lateinit var mEditAffairViewModel: EditAffairViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity ?: throw IllegalStateException("The activity of WeekSelectDialogFragment is null")

        mEditAffairViewModel = ViewModelProviders.of(activity!!).get(EditAffairViewModel::class.java)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.course_fragment_week_select,
                null, false)
        mBinding.recAdapter = WeekSelectRecAdapter(activity!!)
        mBinding.listeners = WeekSelectListeners({
            dismiss()
        }, {
            mEditAffairViewModel.setWeekSelectStringFromFragment()
            dismiss()
        })

        mBottomSheetDialog = BottomSheetDialog(activity!!)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        mBottomSheetDialog.setContentView(mBinding.root, params)

        return mBottomSheetDialog
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