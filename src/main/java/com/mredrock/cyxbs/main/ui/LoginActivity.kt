package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.main_activity_login.*

@Route(path = "/main/login")
class LoginActivity : BaseViewModelActivity<LoginViewModel>() {
    companion object {
        val TAG = LoginActivity::class.java.simpleName
    }

    override val isFragmentActivity = false

    override val viewModelClass = LoginViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_login)
        initView()
        initObserver()
    }

    private fun initView() {
        toolbar.init(getString(R.string.main_activity_login_title))
        et_password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                viewModel.login(et_account.text?.toString(), et_password.text?.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        btn_login.setOnClickListener {
            viewModel.login(et_account.text?.toString(), et_password.text?.toString())
        }
    }

    private fun initObserver() {
        viewModel.backToMainEvent.observeNotNull {
            startActivity<MainActivity>(true)
        }
    }
}
