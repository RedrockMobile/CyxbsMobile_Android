package com.mredrock.cyxbs.main.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.adapter.UserAgreementAdapter
import com.mredrock.cyxbs.main.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.main_activity_user_agreement.*

class UserAgreementActivity : BaseViewModelActivity<LoginViewModel>() {
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_user_agreement)

        main_user_privacy_back.setOnClickListener {
            finish()
        }

        val userAgreementAdapter = UserAgreementAdapter(viewModel.userAgreementList)
        main_rv_user_agree.adapter = userAgreementAdapter
        main_rv_user_agree.layoutManager = LinearLayoutManager(this)

        if (viewModel.userAgreementList.isNotEmpty()) loader.visibility = View.GONE

        viewModel.getUserAgreement {
            userAgreementAdapter.notifyDataSetChanged()
            loader.visibility = View.GONE
        }
    }
}