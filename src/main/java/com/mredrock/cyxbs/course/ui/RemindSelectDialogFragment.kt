package com.mredrock.cyxbs.course.ui

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.databinding.CourseFragmentRemindSelectBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel

/**
 * Created by anriku on 2018/9/11.
 */

class RemindSelectDialogFragment : AppCompatDialogFragment() {

    private lateinit var mBottomSheetDialog: BottomSheetDialog
    private lateinit var mBinding: CourseFragmentRemindSelectBinding
    private lateinit var mEditAffairViewModel: EditAffairViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity ?: throw IllegalStateException("The activity of the Fragment is null")

        mBottomSheetDialog = BottomSheetDialog(activity!!)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.course_fragment_remind_select,
                null, false)

        mEditAffairViewModel = ViewModelProviders.of(activity!!).get(EditAffairViewModel::class.java)

        mBinding.remindStrings = mEditAffairViewModel.remindArray

        mBinding.listeners = RemindSelectListeners({
            mBottomSheetDialog.dismiss()
        }, {
            mEditAffairViewModel.setRemindSelectString(mBinding.redRockNp.value)
            mBottomSheetDialog.dismiss()
        })

        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        mBottomSheetDialog.setContentView(mBinding.root, params)

        return mBottomSheetDialog
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