package com.mredrock.cyxbs.main.ui

import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.get
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.MAIN_LOGIN
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.bean.LoginFailEvent
import com.mredrock.cyxbs.main.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.main_activity_login.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = MAIN_LOGIN)
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
    }

    private fun initView() {
        et_password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                viewModel.login(et_account.text?.toString(), et_password.text?.toString()){landing()}
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        btn_login.setOnClickListener {
            if (viewModel.userAgreementIsCheck) {
                viewModel.login(et_account.text?.toString(), et_password.text?.toString()){landing()}
            } else {
                CyxbsToast.makeText(this,R.string.main_user_agreement_title, Toast.LENGTH_SHORT).show()
            }
        }
        lav_login_check.setOnClickListener {
            lav_login_check.playAnimation()
            viewModel.userAgreementIsCheck = !viewModel.userAgreementIsCheck
        }
        lav_login_check.addAnimatorUpdateListener {
            if (it.animatedFraction == 1f && viewModel.userAgreementIsCheck) {
                lav_login_check.pauseAnimation()
            } else if (it.animatedFraction >= lottieProgress && it.animatedFraction != 1f && !viewModel.userAgreementIsCheck) {
                lav_login_check.pauseAnimation()
            }
        }
    }



    override fun onBackPressed() {
        landing()
    }


    //这个方法可以在登陆状态和未登陆状态之间切换
    private fun landing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(login_container,Explode())
        }
        for (i in 0 until login_container.childCount) {
            val view = login_container[i]
            view.visibility = when(view.visibility){
                View.GONE->View.VISIBLE
                View.VISIBLE->View.GONE
                else -> View.VISIBLE
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun landingError(loginFailEvent: LoginFailEvent) {
        landing()
    }

}
