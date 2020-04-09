package com.mredrock.cyxbs.course.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.databinding.CourseFragmentRemindSelectBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel

/**
 * Created by anriku on 2018/9/11.
 */

class RemindSelectDialog(context: Context) : RedRockBottomSheetDialog(context) {

    private var mBinding: CourseFragmentRemindSelectBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.course_fragment_remind_select,
            null, false)
    private var mEditAffairViewModel: EditAffairViewModel = ViewModelProvider(context as AppCompatActivity).get(EditAffairViewModel::class.java)

    init {
        mBinding.remindStrings = mEditAffairViewModel.remindArray
        mBinding.listeners = RemindSelectListeners({
            dismiss()
        }, {
            mEditAffairViewModel.setRemindSelectString(mBinding.redRockNp.value)
            dismiss()
        })
        setContentView(mBinding.root)
    }

    class RemindSelectListeners(val onCancel: (ImageView) -> Unit,
                                val onSure: (TextView) -> Unit) {

        fun onCancelClick(view: View) {
            onCancel(view as ImageView)
        }

        fun onSureClick(view: View) {
            onSure(view as TextView)
        }
    }

}