package com.mredrock.cyxbs.course.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.TimeSelectedAdapter
import com.mredrock.cyxbs.course.adapters.WeekSelectedAdapter
import com.mredrock.cyxbs.course.adapters.YouMightAdapter
import com.mredrock.cyxbs.course.component.RemindSelectDialog
import com.mredrock.cyxbs.course.component.TimeSelectDialog
import com.mredrock.cyxbs.course.component.WeekSelectDialog
import com.mredrock.cyxbs.course.databinding.CourseActivityEditAffairBinding
import com.mredrock.cyxbs.course.ui.AffairTransitionAnimHelper
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel
import kotlinx.android.synthetic.main.course_activity_edit_affair.*

/**
 * @author Jovines
 * @date 2019/12/21 20:35
 * description：具有三个状态，添加标题，添加内容，最后修改，
 *              分别对应了Helper里面的几个切换方法
 */

class AffairEditActivity : BaseViewModelActivity<EditAffairViewModel>() {

    override val isFragmentActivity: Boolean
        get() = true

    private lateinit var mBinding: CourseActivityEditAffairBinding

    //周数选择BottomSheetDialog
    private val mWeekSelectDialog: WeekSelectDialog by lazy(LazyThreadSafetyMode.NONE) {
        WeekSelectDialog(this, tv_week_select, viewModel.mPostWeeks)
    }

    //时间选择BottomSheetDialog
    private val mTimeSelectDialog: TimeSelectDialog by lazy(LazyThreadSafetyMode.NONE) {
        TimeSelectDialog(this)
    }

    //提醒选择BottomSheetDialog
    private val mRemindSelectDialog: RemindSelectDialog by lazy(LazyThreadSafetyMode.NONE) {
        RemindSelectDialog(this)
    }

    private lateinit var affairTransitionAnimHelper: AffairTransitionAnimHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.course_activity_edit_affair)
        mBinding.editAffairViewModel = viewModel
        mBinding.lifecycleOwner = this
        affairTransitionAnimHelper = AffairTransitionAnimHelper(this)
        initActivity()
    }

    private fun initActivity() {
        viewModel.initData(this)
        tv_week_select.adapter = WeekSelectedAdapter(viewModel.mPostWeeks, mWeekSelectDialog)
        tv_time_select.adapter = TimeSelectedAdapter(viewModel.mPostClassAndDays, mTimeSelectDialog)
        tv_remind_select.setOnSingleClickListener {
            if (!mRemindSelectDialog.isShowing) {
                mRemindSelectDialog.show()
            }
        }
        course_next_step.setOnSingleClickListener {
            forward()
        }
        et_content_input.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                return if (p1 == EditorInfo.IME_ACTION_NEXT) {
                    forward()
                    return true
                } else false
            }
        })
        viewModel.content.observe(this, Observer {
            et_content_input.setText(it)
            et_content_input.setSelection(it.length)
        })

        // 提醒确定后真正要上传的数据的存储
        viewModel.selectedRemindString.observe(this, Observer {
            val remindStrings = resources.getStringArray(R.array.course_remind_strings)
            val position = remindStrings.indexOf(it)
            viewModel.setThePostRemind(position)
        })

        course_back.setOnSingleClickListener { finish() }
        //必须在ViewModel的initData之后执行
        if (viewModel.passedAffairInfo != null) {
            affairTransitionAnimHelper.modifyPageLayout()
        }
        viewModel.titleCandidateList.observe(this, Observer {
            rv_you_might.adapter = YouMightAdapter(it, et_content_input)
        })
    }

    /**
     * 不断进行下一步，根据状态执行相应动画
     */
    private fun forward() {
        when (viewModel.status) {
            EditAffairViewModel.Status.TitleStatus -> affairTransitionAnimHelper.addTitleNextMonitor()
            EditAffairViewModel.Status.ContentStatus -> affairTransitionAnimHelper.addContentNextMonitor()
            EditAffairViewModel.Status.AllDoneStatus -> postAffair()
        }
    }

    /**
     * 不断进行后退，根据状态执行相应动画，或者直接退出activity
     */
    override fun onBackPressed() {
        when (viewModel.status) {
            EditAffairViewModel.Status.TitleStatus -> super.onBackPressed()
            EditAffairViewModel.Status.ContentStatus -> affairTransitionAnimHelper.backAddTitleMonitor()
            //如果是修改事务，此时按返回键直接退出
            EditAffairViewModel.Status.AllDoneStatus -> {
                if (viewModel.passedAffairInfo != null) {
                    super.onBackPressed()
                } else {
                    affairTransitionAnimHelper.backAddContentMonitor()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        affairTransitionAnimHelper.unBindActivity()
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
            viewModel.mPostWeeks.isEmpty() -> {
                Toast.makeText(this, resources.getString(R.string.course_week_is_not_select),
                        Toast.LENGTH_SHORT).show()
            }
            viewModel.mPostClassAndDays.isEmpty() -> {
                Toast.makeText(this, R.string.course_time_is_not_select, Toast.LENGTH_SHORT).show()
            }
            else -> {
                viewModel.postOrModifyAffair(mBinding.etTitle.text.toString(), mBinding.etContentInput.text.toString())
                finish()
            }
        }
    }

    companion object {
        const val AFFAIR_INFO = "affairInfo"
        const val WEEK_NUM = "weekString"
        const val TIME_NUM = "timeNum"
    }

}
