package com.mredrock.cyxbs.freshman.view.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseActivity
import com.mredrock.cyxbs.freshman.bean.EnrollmentProcessText
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEnrollmentProcessModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityEnrollmentProcessPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEnrollmentProcessView
import com.mredrock.cyxbs.freshman.presenter.ActivityEnrollmentProcessPresenter
import com.mredrock.cyxbs.freshman.view.adapter.EnrollmentProcessAdapter
import kotlinx.android.synthetic.main.freshman_activity_enrollment_process.*

/**
 * Create by yuanbing
 * on 2019/8/3
 * 入学流程
 */
class EnrollmentProcessActivity : BaseActivity<IActivityEnrollmentProcessView,
        IActivityEnrollmentProcessPresenter, IActivityEnrollmentProcessModel>(),
        IActivityEnrollmentProcessView {
    override val isFragmentActivity: Boolean
        get() = false
    private lateinit var mAdapter: EnrollmentProcessAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_enrollment_process)

        initToolbar()
        initView()

        presenter?.getEnrollmentProcess()
    }

    private fun initView() {
        mAdapter = EnrollmentProcessAdapter()
        rv_enrollment_process.adapter = mAdapter
        rv_enrollment_process.layoutManager = LinearLayoutManager(this)
        view_enrollment_process.gone()
    }

    private fun initToolbar() {
        common_toolbar.init(
                title = resources.getString(R.string.freshman_enrollment_process),
                listener = View.OnClickListener { finish() }
        )
    }

    override fun showEnrollmentProcess(enrollmentProcess: List<EnrollmentProcessText>) {
        mAdapter.refreshData(enrollmentProcess)
        view_enrollment_process.visible()
    }

    override fun getViewToAttach() = this

    override fun createPresenter() = ActivityEnrollmentProcessPresenter()
}
