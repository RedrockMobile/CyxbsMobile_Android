package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.*
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.YouMightAdapter
import com.mredrock.cyxbs.course.databinding.CourseActivityEditAffairBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel
import kotlinx.android.synthetic.main.course_activity_edit_affair.*
import org.jetbrains.anko.dip


class EditAffairActivity : BaseActivity() {

    var status: Status = Status.TitleStatus

    override val isFragmentActivity: Boolean
        get() = true

    private lateinit var mBinding: CourseActivityEditAffairBinding
    private val mWeekSelectDialogFragment: WeekSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        WeekSelectDialogFragment()
    }
    private val mTimeSelectDialogFragment: TimeSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        TimeSelectDialogFragment()
    }
    private val mRemindSelectDialogFragment: RemindSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        RemindSelectDialogFragment()
    }
    private lateinit var mEditAffairViewModel: EditAffairViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditAffairViewModel = ViewModelProviders.of(this).get(EditAffairViewModel::class.java)

        mBinding = DataBindingUtil.setContentView(this, R.layout.course_activity_edit_affair)
        mBinding.editAffairViewModel = mEditAffairViewModel
        mBinding.lifecycleOwner = this

        initActivity()
    }

    private fun initActivity() {
        mEditAffairViewModel.observeWork(this)
        mEditAffairViewModel.initData(this)


        mBinding.listeners = EditAffairListeners({
            if (!mWeekSelectDialogFragment.isAdded) {
                mWeekSelectDialogFragment.show(supportFragmentManager, "weekSelectDialogFragment")
            }
        }, {
            if (!mTimeSelectDialogFragment.isAdded) {
                mTimeSelectDialogFragment.show(supportFragmentManager, "timeSelectDialogFragment")
            }
        }, {
            if (!mRemindSelectDialogFragment.isAdded) {
                mRemindSelectDialogFragment.show(supportFragmentManager, "remindSelectDialogFragment")
            }
        })
        course_view23.setOnClickListener {
            onClick()
        }
        et_title_content_input.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                return if (p1 == EditorInfo.IME_ACTION_NEXT) {
                    onClick()
                    return true
                } else false
            }
        })
        rv_you_might.adapter = YouMightAdapter(et_title_content_input)
    }

    private fun onClick(){
        when (status) {
            Status.TitleStatus -> addTitleNextMonitor()
            Status.ContentStatus -> addContentNextMonitor()
            Status.AllDoneStatus -> postAffair()
        }
    }


    override fun onBackPressed() {
        when (status) {
            Status.TitleStatus -> super.onBackPressed()
            Status.ContentStatus -> addTitleBackMonitor()
            Status.AllDoneStatus -> addContentBackMonitor()
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
                    duration = 500
                    slideEdge = Gravity.RIGHT
                })
                addTransition(ChangeBounds().apply { duration = 500 })
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
            status = Status.ContentStatus
        }
    }


    /**
     * 添加标题之后跳转到添加内容动画
     */
    private fun addTitleBackMonitor() {
        TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
            addTransition(Slide().apply {
                duration = 500
                slideEdge = Gravity.RIGHT
            })
            addTransition(ChangeBounds().apply { duration = 500 })
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
        et_title_content_input.setText(tv_title.text, TextView.BufferType.EDITABLE);
        et_title_content_input.setSelection(tv_title.text.length)
        tv_title.text = ""
        tv_content_text.visibility = View.GONE
        status = Status.TitleStatus
    }

    fun addContentNextMonitor(){
        if (et_title_content_input.text.trim().isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.course_content_is_null),
                    Toast.LENGTH_SHORT).show()
        } else {
            TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
                addTransition(Slide().apply {
                    duration = 500
                    slideEdge = Gravity.RIGHT
                })
                addTransition(ChangeBounds().apply { duration = 500 })
            })
            val set = ConstraintSet().apply { clone(course_affair_container) }
            set.connect(R.id.et_title_content_input, ConstraintSet.TOP, R.id.course_affair_container, ConstraintSet.TOP)
            set.setVerticalBias(R.id.et_title_content_input, 0.32f)
            set.applyTo(course_affair_container)
            //单独修改控件属性要在apply之后
            course_textview.visibility = View.GONE
            tv_title_text.visibility = View.GONE
            tv_title.visibility = View.GONE
            tv_content_text.visibility = View.GONE
            et_title.visibility = View.VISIBLE
            tv_week_select.visibility = View.VISIBLE
            tv_time_select.visibility = View.VISIBLE
            tv_remind_select.visibility = View.VISIBLE
            et_title_content_input.imeOptions = EditorInfo.IME_ACTION_DONE
            et_title.setText(tv_title.text, TextView.BufferType.EDITABLE);
            status = Status.AllDoneStatus
        }
    }
    fun addContentBackMonitor(){
        if (et_title_content_input.text.trim().isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.course_content_is_null),
                    Toast.LENGTH_SHORT).show()
        } else {
            TransitionManager.beginDelayedTransition(course_affair_container, TransitionSet().apply {
                addTransition(Slide().apply {
                    duration = 500
                    slideEdge = Gravity.RIGHT
                })
                addTransition(ChangeBounds().apply { duration = 500 })
            })
            val set = ConstraintSet().apply { clone(course_affair_container) }
            set.connect(R.id.et_title_content_input, ConstraintSet.TOP, R.id.tv_content_text, ConstraintSet.BOTTOM)
            set.setVerticalBias(R.id.et_title_content_input, 0f)
            set.applyTo(course_affair_container)
            //单独修改控件属性要在apply之后
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
            status = Status.ContentStatus
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.course_post_affair_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (item.itemId) {
                R.id.post_affair -> {
                    postAffair()
                }
                android.R.id.home -> {
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 此方法用于上传事务
     */
    private fun postAffair() {
        when {
            TextUtils.isEmpty(mBinding.etTitle.text.trim()) -> {
                Toast.makeText(this, resources.getString(R.string.course_title_is_null),
                        Toast.LENGTH_SHORT).show()
            }
            mEditAffairViewModel.selectedWeekString.value ==
                    resources.getString(R.string.course_week_select) -> {
                Toast.makeText(this, resources.getString(R.string.course_week_is_not_select),
                        Toast.LENGTH_SHORT).show()
            }
            mEditAffairViewModel.selectedTimeString.value ==
                    resources.getString(R.string.course_time_select) -> {
                Toast.makeText(this, R.string.course_time_is_not_select, Toast.LENGTH_SHORT).show()
            }
            else -> {
                mEditAffairViewModel.postOrModifyAffair(this, mBinding.etTitle.text.toString(),
                        mBinding.etTitleContentInput.text.toString())
            }
        }
    }

    class EditAffairListeners(val onSelectWeek: (TextView) -> Unit,
                              val onSelectTime: (TextView) -> Unit,
                              val onSelectRemind: (TextView) -> Unit) {

        fun onWeekSelectClick(weekSelect: View) {
            onSelectWeek(weekSelect as TextView)
        }

        fun onTimeSelectClick(timeSelect: View) {
            onSelectTime(timeSelect as TextView)
        }

        fun onRemindSelectClick(remindSelect: View) {
            onSelectRemind(remindSelect as TextView)
        }

    }

    companion object {
        const val AFFAIR_INFO = "affairInfo"
        const val WEEK_NUM = "weekString"
        const val TIME_NUM = "timeNum"
    }

    enum class Status {
        TitleStatus, ContentStatus, AllDoneStatus
    }
}
