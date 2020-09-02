package com.mredrock.cyxbs.course.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.databinding.CourseFragmentTimeSelectBinding
import com.mredrock.cyxbs.course.ui.activity.AffairEditActivity
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel
import com.super_rabbit.wheel_picker.WheelAdapter
import kotlinx.android.synthetic.main.course_activity_edit_affair.*


/**
 * Created by anriku on 2018/9/10.
 */
@SuppressLint("ClickableViewAccessibility")
class TimeSelectDialog(context: Context) : RedRockBottomSheetDialog(context) {

    private var mBinding: CourseFragmentTimeSelectBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.course_fragment_time_select, null, false)
    private var mEditAffairViewModel: EditAffairViewModel = ViewModelProvider(context as AppCompatActivity).get(EditAffairViewModel::class.java)

    init {
        mBinding.listeners = TimeSelectListeners {
            val data = Pair(mEditAffairViewModel.timeArray.indexOf(mBinding.affairTimeSelect.getCurrentItem()),
                    mEditAffairViewModel.dayOfWeekArray.indexOf(mBinding.affairWeekDaySelect.getCurrentItem()))
            if (!mEditAffairViewModel.mPostClassAndDays.contains(data)) {
                mEditAffairViewModel.mPostClassAndDays.add(data)
                (context as AffairEditActivity).tv_time_select.refreshData()
                dismiss()
            } else {
                Toast.makeText(context, "掌友，这个时间已经选择了哦", Toast.LENGTH_SHORT).show()
            }
        }

        //解决与bottomSheet的滑动冲突
        mBinding.affairTimeSelect.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        mBinding.affairTimeSelect.apply {
            setAdapter(AffairTimeSelectAdapter(mEditAffairViewModel.timeArray))
            setSelectorRoundedWrapPreferred(true)
        }

        //解决与BottomSheet的滑动冲突
        mBinding.affairWeekDaySelect.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        mBinding.affairWeekDaySelect.apply {
            setAdapter(AffairWeekSelectAdapter(mEditAffairViewModel.dayOfWeekArray))
            setSelectorRoundedWrapPreferred(true)
        }
        setContentView(mBinding.root)
    }

    /**
     * 为TimeSelectDialogFragment中的控件设置点击事件
     * @param onSure 给确定的TextView设置点击事件
     */
    class TimeSelectListeners(val onSure: (ImageView) -> Unit) {

        fun onSureClick(sure: View) {
            onSure(sure as ImageView)
        }
    }

    class AffairTimeSelectAdapter(private val timeList: Array<String>) : WheelAdapter {

        override fun getMaxIndex(): Int {
            return timeList.size - 1
        }

        override fun getMinIndex(): Int {
            return 0
        }

        override fun getPosition(vale: String): Int {
            return timeList.indexOf(vale)
        }

        override fun getTextWithMaximumLength(): String {
            return "十一十二节课"
        }

        override fun getValue(position: Int): String {
            if (position < 0) {
                val a = (6 - (-(position % 6))) % 6
                return timeList[a]
            }
            return timeList[position % 6]
        }
    }


    class AffairWeekSelectAdapter(private val timeList: Array<String>) : WheelAdapter {

        override fun getMaxIndex(): Int {
            return timeList.size - 1
        }

        override fun getMinIndex(): Int {
            return 0
        }

        override fun getPosition(vale: String): Int {
            return timeList.indexOf(vale)
        }

        override fun getTextWithMaximumLength(): String {
            return "周日"
        }

        override fun getValue(position: Int): String {
            if (position < 0) {
                val a = (7 - (-(position % 7))) % 7
                return timeList[a]
            }
            return timeList[position % 7]
        }
    }
}