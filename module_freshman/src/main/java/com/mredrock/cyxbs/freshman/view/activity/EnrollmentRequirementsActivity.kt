package com.mredrock.cyxbs.freshman.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseActivity
import com.mredrock.cyxbs.freshman.interfaces.ParseBean
import com.mredrock.cyxbs.freshman.interfaces.model.IActivityEnrollmentRequirementsModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IActivityEnrollmentRequirementsPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IActivityEnrollmentRequirementsView
import com.mredrock.cyxbs.freshman.presenter.ActivityEnrollmentRequirementsPresenter
import com.mredrock.cyxbs.freshman.view.adapter.EnrollmentRequirementsAdapter
import kotlinx.android.synthetic.main.freshman_activity_enrollment_requirements.*
import kotlinx.android.synthetic.main.freshman_toolbar.*

/**
 * Create by yuanbing
 * on 2019/8/3
 * 入学必备
 */
class EnrollmentRequirementsActivity :
        BaseActivity<IActivityEnrollmentRequirementsView, IActivityEnrollmentRequirementsPresenter,
                IActivityEnrollmentRequirementsModel>(), IActivityEnrollmentRequirementsView {
    override val isFragmentActivity: Boolean
        get() = false
    private lateinit var mAdapter: EnrollmentRequirementsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freshman_activity_enrollment_requirements)

        initToolbar()
        initView()

        presenter?.getEnrollmentRequirements()
    }

    private fun initView() {
        floating_action_button_add_memorandum_book.setOnClickListener {
            val intent = Intent(this@EnrollmentRequirementsActivity,
                    AddMemorandumBookActivity::class.java)
            startActivityForResult(intent, 0)
        }

        rv_enrollment_requirements.layoutManager = LinearLayoutManager(this)
        mAdapter = EnrollmentRequirementsAdapter()
        rv_enrollment_requirements.adapter = mAdapter
    }

    private fun initToolbar() {
        freshman_toolbar.init(
                title = "",
                listener = View.OnClickListener { finish() }
        )
        freshman_toolbar_title.text = resources.getString(R.string.freshman_enrollment_requirement)
        freshman_toolbar_text_right.text = getString(R.string.freshman_add_enrollment_requiremnets_edit)
        freshman_toolbar_text_right.setOnClickListener {
            val intent = Intent(this@EnrollmentRequirementsActivity,
                    EditMemorandumBookActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0, 1 -> if (resultCode == Activity.RESULT_OK) presenter?.getEnrollmentRequirements()
        }
    }

    override fun getViewToAttach() = this

    override fun createPresenter() = ActivityEnrollmentRequirementsPresenter()

    override fun showEnrollmentRequirements(enrollmentRequirements: List<ParseBean>) {
        mAdapter.refreshData(enrollmentRequirements)
    }
}