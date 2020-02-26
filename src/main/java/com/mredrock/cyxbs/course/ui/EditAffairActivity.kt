package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.ChangeBounds
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.TimeSelectedAdapter
import com.mredrock.cyxbs.course.adapters.WeekSelectedAdapter
import com.mredrock.cyxbs.course.adapters.YouMightAdapter
import com.mredrock.cyxbs.course.databinding.CourseActivityEditAffairBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel
import kotlinx.android.synthetic.main.course_activity_edit_affair.*

/**
 * 是个activity是一个有些复杂的动画集合
 */
class EditAffairActivity : BaseActivity() {

    override val isFragmentActivity: Boolean
        get() = true

    private lateinit var mBinding: CourseActivityEditAffairBinding

    //周数选择BottomSheetDialog
    val mWeekSelectDialogFragment: WeekSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        WeekSelectDialogFragment(this)
    }

    //时间选择BottomSheetDialog
    val mTimeSelectDialogFragment: TimeSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        TimeSelectDialogFragment(this)
    }

    //提醒选择BottomSheetDialog
    private val mRemindSelectDialogFragment: RemindSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        RemindSelectDialogFragment(this)
    }

    lateinit var mEditAffairViewModel: EditAffairViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditAffairViewModel = ViewModelProviders.of(this).get(EditAffairViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.course_activity_edit_affair)
        mBinding.editAffairViewModel = mEditAffairViewModel
        mBinding.lifecycleOwner = this
        initActivity()
    }

    private fun initActivity() {
        mEditAffairViewModel.initData(this)

        tv_week_select.adapter = WeekSelectedAdapter(mEditAffairViewModel.mPostWeeks, this)
        tv_time_select.adapter = TimeSelectedAdapter(mEditAffairViewModel.mPostClassAndDays, this)
        tv_remind_select.setOnClickListener {
            if (!mRemindSelectDialogFragment.isShowing) {
                mRemindSelectDialogFragment.show()
            }
        }
        course_next_step.setOnClickListener {
            forward()
        }
        et_title_content_input.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                return if (p1 == EditorInfo.IME_ACTION_NEXT) {
                    forward()
                    return true
                } else false
            }
        })

        course_back.setOnClickListener { finish() }
        //必须在ViewModel的initData之后执行
        if (mEditAffairViewModel.passedAffairInfo != null) {
            modifyPageLayout()
        }
        mEditAffairViewModel.titleCandidateList.observe(this, Observer {
            rv_you_might.adapter = YouMightAdapter(it,et_title_content_input)
        })
    }

    /**
     * 不断进行下一步，根据状态执行相应动画
     */
    private fun forward() {
        when (mEditAffairViewModel.status) {
            EditAffairViewModel.Status.TitleStatus -> addTitleNextMonitor()
            EditAffairViewModel.Status.ContentStatus -> addContentNextMonitor()
            EditAffairViewModel.Status.AllDoneStatus -> postAffair()
        }
    }

    /**
     * 不断进行后退，根据状态执行相应动画，或者直接退出activity
     */
    override fun onBackPressed() {
        when (mEditAffairViewModel.status) {
            EditAffairViewModel.Status.TitleStatus -> super.onBackPressed()
            EditAffairViewModel.Status.ContentStatus -> backAddTitleMonitor()
            //如果是修改事务，此时按返回键直接退出
            EditAffairViewModel.Status.AllDoneStatus -> {
                if (mEditAffairViewModel.passedAffairInfo != null) {
                    super.onBackPressed()
                } else {
                    backAddContentMonitor()
                }
            }
        }
    }

    /**
     * 添加标题之后跳转到添加内容动画
     */
    private fun addTitleNextMonitor() {
        if (et_title_content_input.text.trim().isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.course_title_is_null),
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
            set.connect(R.id.et_title_content_input, ConstraintSet.TOP, R.id.tv_content_text, ConstraintSet.BOTTOM)
            set.applyTo(course_affair_container)
            //单独修改控件属性要在apply之后
            tv_title_text.textSize = 15f
            tv_title_text.text = "标题："
            tv_title.visibility = View.VISIBLE
            rv_you_might.visibility = View.GONE
            tv_title.text = et_title_content_input.text.toString()
            tv_content_text.visibility = View.VISIBLE
            et_title_content_input.text.clear()
            mEditAffairViewModel.status = EditAffairViewModel.Status.ContentStatus
        }
    }

    /**
     * 返回到添加标题的动画
     */
    private fun backAddTitleMonitor() {
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
        set.connect(R.id.et_title_content_input, ConstraintSet.TOP, R.id.tv_title_text, ConstraintSet.BOTTOM)
        set.applyTo(course_affair_container)
        //单独修改控件属性要在apply之后
        tv_title_text.textSize = 34f
        tv_title_text.text = "一个标题"
        tv_title.visibility = View.GONE
        rv_you_might.visibility = View.VISIBLE
        et_title_content_input.setText(tv_title.text, TextView.BufferType.EDITABLE)
        et_title_content_input.setSelection(tv_title.text.length)
        tv_title.text = ""
        tv_content_text.visibility = View.GONE
        mEditAffairViewModel.status = EditAffairViewModel.Status.TitleStatus
    }

    /**
     * 添加内容之后跳转到选择时间动画
     */
    private fun addContentNextMonitor() {
        //鄙人觉得这个没有必要做内容为空的判断，事务其实有时候简单的事务大多数人写个内容都是为了占位置，没有具体意义
        TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
            addTransition(Slide().apply {
                duration = 300
                slideEdge = Gravity.END
            })
            addTransition(ChangeBounds().apply { duration = 300 })
        })
        modifyPageLayout()
        mEditAffairViewModel.status = EditAffairViewModel.Status.AllDoneStatus

    }

    /**
     * 返回到添加内容的动画
     */
    private fun backAddContentMonitor() {
        //鄙人觉得这个没有必要做内容为空的判断，事务其实有时候简单的事务大多数人写个内容都是为了占位置，没有具体意义
        TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
            addTransition(ChangeBounds().apply { duration = 300 })
        })
        val set = ConstraintSet().apply { clone(course_affair_container) }
        set.connect(R.id.et_title_content_input, ConstraintSet.TOP, R.id.tv_content_text, ConstraintSet.BOTTOM)
        set.setVerticalBias(R.id.et_title_content_input, 0f)
        set.applyTo(course_affair_container)
        //单独修改控件属性要在applyTo之后
        course_textview.visibility = View.VISIBLE
        tv_title_text.visibility = View.VISIBLE
        tv_title.visibility = View.VISIBLE
        tv_content_text.visibility = View.VISIBLE
        et_title.visibility = View.GONE
        tv_week_select.visibility = View.GONE
        tv_time_select.visibility = View.GONE
        tv_remind_select.visibility = View.GONE
        et_title_content_input.imeOptions = EditorInfo.IME_ACTION_NEXT
        tv_title.text = et_title.text
        mEditAffairViewModel.status = EditAffairViewModel.Status.ContentStatus
    }

    /**
     * 如果是修改事务，这个方法用于将此activity转换到最后一个状态
     */
    private fun modifyPageLayout() {
        val set = ConstraintSet().apply { clone(course_affair_container) }
        set.connect(R.id.et_title_content_input, ConstraintSet.TOP, R.id.course_affair_container, ConstraintSet.TOP)
        set.setVerticalBias(R.id.et_title_content_input, 0.32f)
        set.applyTo(course_affair_container)
        //单独修改控件属性要在apply之后
        course_textview.visibility = View.GONE
        tv_title_text.visibility = View.GONE
        tv_title.visibility = View.GONE
        tv_content_text.visibility = View.GONE
        rv_you_might.visibility = View.GONE
        et_title.visibility = View.VISIBLE
        tv_week_select.visibility = View.VISIBLE
        tv_time_select.visibility = View.VISIBLE
        tv_remind_select.visibility = View.VISIBLE
        et_title_content_input.imeOptions = EditorInfo.IME_ACTION_DONE
        et_title.setText(tv_title.text, TextView.BufferType.EDITABLE)
        mEditAffairViewModel.status = EditAffairViewModel.Status.AllDoneStatus
    }

    /**
     * 最后一步上传事务
     */
    private fun postAffair() {
        when {
            TextUtils.isEmpty(mBinding.etTitle.text.trim()) -> {
                Toast.makeText(this, resources.getString(R.string.course_title_is_null),
                        Toast.LENGTH_SHORT).show()
            }
            mEditAffairViewModel.mPostWeeks.isEmpty() -> {
                Toast.makeText(this, resources.getString(R.string.course_week_is_not_select),
                        Toast.LENGTH_SHORT).show()
            }
            mEditAffairViewModel.mPostClassAndDays.isEmpty() -> {
                Toast.makeText(this, R.string.course_time_is_not_select, Toast.LENGTH_SHORT).show()
            }
            else -> {
                mEditAffairViewModel.postOrModifyAffair(this, mBinding.etTitle.text.toString(),
                        mBinding.etTitleContentInput.text.toString())
            }
        }
    }

    companion object {
        const val AFFAIR_INFO = "affairInfo"
        const val WEEK_NUM = "weekString"
        const val TIME_NUM = "timeNum"
    }

}
