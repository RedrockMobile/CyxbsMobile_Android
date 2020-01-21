package com.mredrock.cyxbs.course.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.TimeSelectRecAdapter
import com.mredrock.cyxbs.course.databinding.CourseFragmentTimeSelectBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel


/**
 * Created by anriku on 2018/9/10.
 */

class TimeSelectDialogFragment(context: Context) : RedRockBottomSheetDialog(context) {

    private var mBinding: CourseFragmentTimeSelectBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.course_fragment_time_select, null, false)
    private var mEditAffairViewModel: EditAffairViewModel = ViewModelProviders.of(context as AppCompatActivity).get(EditAffairViewModel::class.java)


    init{

        mBinding.recAdapter = TimeSelectRecAdapter(context as AppCompatActivity)
        mBinding.listeners = TimeSelectListeners({
            dismiss()
        }, {
            mEditAffairViewModel.setTimeSelectStringFromFragment()
            dismiss()
        })

        ContextCompat.getDrawable(context, R.drawable.course_time_select_divider)?.let {
            mBinding.rv.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL).apply {
                setDrawable(it)
            })
            mBinding.rv.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL).apply {
                setDrawable(it)
            })
        }
        setContentView(mBinding.root)
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