package com.mredrock.cyxbs.main.ui

import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.get
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.ACTIVITY_CLASS
import com.mredrock.cyxbs.common.config.IS_EXIT_LOGIN
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

    companion object{
        const val USER_LOGIN = "UserLogin"
    }

    override val isFragmentActivity = false

    override val viewModelClass = LoginViewModel::class.java

    private val lottieProgress = 0.39f

    override val loginConfig = LoginConfig(isCheckLogin = false)

    @JvmField
    @Autowired(name = ACTIVITY_CLASS)
    var targetActivity: Class<*>? = null

    @JvmField
    @Autowired(name = IS_EXIT_LOGIN)
    var isExitLogin: Boolean? = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this);
        setContentView(R.layout.main_activity_login)
        initView()
    }

    private fun initView() {
        et_password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                loginAction()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        btn_login.setOnClickListener {
            loginAction()
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

    private fun loginAction() {
        if (viewModel.userAgreementIsCheck) {
            viewModel.login(et_account.text?.toString(), et_password.text?.toString(), landing){
                if (isExitLogin!=null && isExitLogin!!) {
                    startActivity(Intent(this,MainActivity::class.java).apply {
                        putExtra(USER_LOGIN,"UserLogin")
                    })
                    finish()
                }else{
                    if (targetActivity != null) {
                        startActivity(Intent(this, targetActivity))
                        finish()
                    } else {
                        finish()
                    }
                }
            }
        } else {
            CyxbsToast.makeText(this, R.string.main_user_agreement_title, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        finishAffinity();
        finish()
    }

    //这个方法可以在登陆状态和未登陆状态之间切换
    private val landing = {
        TransitionManager.beginDelayedTransition(login_container, Explode())
        for (i in 0 until login_container.childCount) {
            val view = login_container[i]
            view.visibility = when (view.visibility) {
                View.GONE -> View.VISIBLE
                View.VISIBLE -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun landingError(loginFailEvent: LoginFailEvent) {
        landing()
    }

}
