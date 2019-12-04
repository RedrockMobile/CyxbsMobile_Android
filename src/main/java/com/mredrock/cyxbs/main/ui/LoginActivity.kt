package com.mredrock.cyxbs.main.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.main_activity_login.*
import kotlinx.android.synthetic.main.main_dialog_input_info.view.*

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
        BaseApp.user = null //退出登陆
        initView()
        initObserver()
    }

    private fun initView() {
//        toolbar.init(getString(R.string.main_activity_login_title))
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

    @SuppressLint("InflateParams")
    private fun initObserver() {
        viewModel.backToMainOrEditInfoEvent.observe(this, Observer {
            it ?: return@Observer
            if (it) {
                startActivity<MainActivity>(true)
            } else {

                val layout = layoutInflater.inflate(R.layout.main_dialog_input_info, null)

                val dialog = AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("首次登陆，请填写重要信息")
                        .setView(layout).setPositiveButton("确定", null).create()

                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (viewModel.register(et_account.text?.toString(), et_password.text?.toString(), layout.et_input_username.text?.toString())) {
                            dialog.dismiss()
                        }
                    }
                }

                dialog.show()

            }
        })
    }
}
