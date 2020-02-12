package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.component.CyxbsToast
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

    private val lottieProgress = 0.39f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_login)
        initView()
        initObserver()
    }

    private fun initView() {
        et_password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                viewModel.login(et_account.text?.toString(), et_password.text?.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        btn_login.setOnClickListener {
            if (lav_login_check.progress != 1f) {
                viewModel.login(et_account.text?.toString(), et_password.text?.toString())
            } else {
                CyxbsToast.makeText(this, "请先同意用户协议吧", Toast.LENGTH_SHORT).show()
            }
        }
        var isCheck = false
        lav_login_check.setOnClickListener {
            lav_login_check.playAnimation()
        }
        lav_login_check.addAnimatorUpdateListener {
            if (it.animatedFraction == 1f && !isCheck) {
                lav_login_check.pauseAnimation()
                isCheck = true
            } else if (it.animatedFraction >= lottieProgress && it.animatedFraction != 1f && isCheck) {
                lav_login_check.pauseAnimation()
                isCheck = false
            }
        }
    }

    private fun initObserver() {
        viewModel.backToMainEvent.observeNotNull {
            startActivity<MainActivity>(true)
        }
    }
}
