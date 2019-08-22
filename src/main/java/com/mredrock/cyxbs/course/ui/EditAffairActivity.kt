package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.databinding.CourseActivityEditAffairBinding
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel

class EditAffairActivity : BaseActivity() {

    companion object {
        const val AFFAIR_INFO = "affairInfo"
        const val WEEK_NUM = "weekString"
        const val TIME_NUM = "timeNum"
    }

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
        mBinding.setLifecycleOwner(this)

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

        val toolbar = common_toolbar
        toolbar.title = getString(R.string.course_edit_affair)
        setSupportActionBar(toolbar)
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
            TextUtils.isEmpty(mBinding.etContent.text.trim()) -> {
                Toast.makeText(this, resources.getString(R.string.course_content_is_null),
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
                        mBinding.etContent.text.toString())
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
}
