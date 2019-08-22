package com.mredrock.cyxbs.course.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.TimeSelectRecAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentTimeSelectBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel


/**
 * Created by anriku on 2018/9/10.
 */

class TimeSelectDialogFragment : AppCompatDialogFragment() {

    private lateinit var mBottomSheetDialog: BottomSheetDialog
    private lateinit var mBinding: CourseFragmentTimeSelectBinding
    private lateinit var mEditAffairViewModel: EditAffairViewModel


    @SuppressLint("PrivateResource")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity ?: throw IllegalStateException("The activity of WeekSelectDialogFragment is null")

        mEditAffairViewModel = ViewModelProviders.of(activity!!).get(EditAffairViewModel::class.java)

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity),
                R.layout.course_fragment_time_select, null, false)
        mBinding.recAdapter = TimeSelectRecAdapter(activity!!)
        mBinding.listeners = TimeSelectListeners({
            dismiss()
        }, {
            mEditAffairViewModel.setTimeSelectStringFromFragment()
            dismiss()
        })

        ContextCompat.getDrawable(activity!!, R.drawable.course_time_select_divider)?.let {
            mBinding.rv.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL).apply {
                setDrawable(it)
            })
            mBinding.rv.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity, androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL).apply {
                setDrawable(it)
            })
        }

        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        mBottomSheetDialog = BottomSheetDialog(activity!!)
        mBottomSheetDialog.setContentView(mBinding.root, params)

        return mBottomSheetDialog
    }

    /**
     * 为TimeSelectDialogFragment中的控件设置点击事件
     * @param onCancel 给取消的ImageView设置点击事件
     * @param onSure 给确定的TextView设置点击事件
     */
    class TimeSelectListeners(val onCancel: (ImageView) -> Unit, val onSure: (TextView) -> Unit) {
        fun onCancelClick(cancel: View) {
            onCancel(cancel as ImageView)
        }

        fun onSureClick(sure: View) {
            onSure(sure as TextView)
        }
    }

}