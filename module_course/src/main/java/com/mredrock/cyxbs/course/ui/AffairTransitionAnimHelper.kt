package com.mredrock.cyxbs.course.ui

import android.transition.ChangeBounds
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.ui.activity.AffairEditActivity
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel
import kotlinx.android.synthetic.main.course_activity_edit_affair.*

/**
 * @author Jovines
 * @date 2020/3/20 16:27
 * description：事务Activity的Transition动画的逻辑代码,
 *              以此来使activity主要逻辑代码显得更加清晰，让activity专心处理逻辑代码
 */
class AffairTransitionAnimHelper(private var affairEditActivity: AffairEditActivity?) {

    /**
     * 添加标题之后跳转到添加内容动画
     */
    fun addTitleNextMonitor() {
        affairEditActivity?.apply {
            if (et_content_input.text.trim().isEmpty()) {
                CyxbsToast.makeText(this, resources.getString(R.string.course_title_is_null),
                        Toast.LENGTH_SHORT).show()
            } else {
                TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
                    addTransition(Slide().apply {
                        duration = 300
                        slideEdge = Gravity.END
                    })
                    addTransition(ChangeBounds().apply { duration = 300 })
                })
                val set = ConstraintSet().apply { clone(course_affair_container) }
                set.connect(R.id.tv_title_text, ConstraintSet.BOTTOM, R.id.course_textview, ConstraintSet.TOP)
                set.connect(R.id.tv_title_text, ConstraintSet.TOP, R.id.course_affair_container, ConstraintSet.TOP)
                set.setVerticalBias(R.id.tv_title_text, 1f)
                set.connect(R.id.et_content_input, ConstraintSet.TOP, R.id.tv_content_text, ConstraintSet.BOTTOM)
                set.applyTo(course_affair_container)
                //单独修改控件属性要在apply之后
                tv_title_text.textSize = 15f
                tv_title_text.text = "标题："
                tv_title_tips.visibility = View.VISIBLE
                rv_you_might.visibility = View.GONE
                tv_title_tips.text = et_content_input.text.toString()
                tv_content_text.visibility = View.VISIBLE
                et_content_input.text.clear()
                viewModel.status = EditAffairViewModel.Status.ContentStatus
            }
        }
    }

    /**
     * 返回到添加标题的动画
     */
    fun backAddTitleMonitor() {
        affairEditActivity?.apply {
            TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
                addTransition(Slide().apply {
                    duration = 300
                    slideEdge = Gravity.END
                })
                addTransition(ChangeBounds().apply { duration = 300 })
            })
            val set = ConstraintSet().apply { clone(course_affair_container) }
            set.connect(R.id.tv_title_text, ConstraintSet.BOTTOM, R.id.course_affair_container, ConstraintSet.BOTTOM)
            set.connect(R.id.tv_title_text, ConstraintSet.TOP, R.id.course_textview, ConstraintSet.BOTTOM)
            set.setVerticalBias(R.id.tv_title_text, 0f)
            set.connect(R.id.et_content_input, ConstraintSet.TOP, R.id.tv_title_text, ConstraintSet.BOTTOM)
            set.applyTo(course_affair_container)
            //单独修改控件属性要在apply之后
            tv_title_text.textSize = 34f
            tv_title_text.text = "一个标题"
            tv_title_tips.visibility = View.GONE
            rv_you_might.visibility = View.VISIBLE
            et_content_input.setText(tv_title_tips.text, TextView.BufferType.EDITABLE)
            et_content_input.setSelection(tv_title_tips.text.length)
            tv_title_tips.text = ""
            tv_content_text.visibility = View.GONE
            viewModel.status = EditAffairViewModel.Status.TitleStatus
        }
    }


    /**
     * 添加内容之后跳转到选择时间动画
     */
    fun addContentNextMonitor() {
        affairEditActivity?.apply {
            //鄙人觉得这个没有必要做内容为空的判断，事务其实有时候简单的事务大多数人写个内容都是为了占位置，没有具体意义
            TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
                addTransition(Slide().apply {
                    duration = 300
                    slideEdge = Gravity.END
                })
                addTransition(ChangeBounds().apply { duration = 300 })
            })
            modifyPageLayout()
            viewModel.status = EditAffairViewModel.Status.AllDoneStatus

        }
    }


    /**
     * 返回到添加内容的动画
     */
    fun backAddContentMonitor() {
        affairEditActivity?.apply { //鄙人觉得这个没有必要做内容为空的判断，事务其实有时候简单的事务大多数人写个内容都是为了占位置，没有具体意义
            TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
                addTransition(ChangeBounds().apply { duration = 300 })
            })
            val set = ConstraintSet().apply { clone(course_affair_container) }
            set.connect(R.id.et_content_input, ConstraintSet.TOP, R.id.tv_content_text, ConstraintSet.BOTTOM)
            set.setVerticalBias(R.id.et_content_input, 0.04f)
            set.applyTo(course_affair_container)
            //单独修改控件属性要在applyTo之后
            course_textview.visibility = View.VISIBLE
            tv_title_text.visibility = View.VISIBLE
            tv_title_tips.visibility = View.VISIBLE
            tv_content_text.visibility = View.VISIBLE
            et_title.visibility = View.GONE
            tv_week_select.visibility = View.GONE
            tv_time_select.visibility = View.GONE
            tv_remind_select.visibility = View.GONE
            et_content_input.imeOptions = EditorInfo.IME_ACTION_NEXT
            tv_title_tips.text = et_title.text
            viewModel.status = EditAffairViewModel.Status.ContentStatus
        }
    }


    /**
     * 如果是修改事务，这个方法用于将此activity转换到最后一个状态
     */
    fun modifyPageLayout() {
        affairEditActivity?.apply {
            val set = ConstraintSet().apply { clone(course_affair_container) }
            set.connect(R.id.et_content_input, ConstraintSet.TOP, R.id.course_affair_container, ConstraintSet.TOP)
            set.setVerticalBias(R.id.et_content_input, 0.32f)
            set.applyTo(course_affair_container)
            //单独修改控件属性要在apply之后
            course_textview.visibility = View.GONE
            tv_title_text.visibility = View.GONE
            tv_title_tips.visibility = View.GONE
            tv_content_text.visibility = View.GONE
            rv_you_might.visibility = View.GONE
            et_title.visibility = View.VISIBLE
            tv_week_select.visibility = View.VISIBLE
            tv_time_select.visibility = View.VISIBLE
            tv_remind_select.visibility = View.VISIBLE
            et_content_input.imeOptions = EditorInfo.IME_ACTION_DONE
            et_title.setText(tv_title_tips.text, TextView.BufferType.EDITABLE)
            viewModel.status = EditAffairViewModel.Status.AllDoneStatus
        }
    }

    fun unBindActivity() {
        affairEditActivity = null
    }
}