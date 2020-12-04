package com.mredrock.cyxbs.main.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Explode
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.bean.LoginConfig
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.config.ACTIVITY_CLASS
import com.mredrock.cyxbs.common.config.IS_EXIT_LOGIN
import com.mredrock.cyxbs.common.config.MINE_FORGET_PASSWORD
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.main.MAIN_LOGIN
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.adapter.UserAgreementAdapter
import com.mredrock.cyxbs.main.bean.LoginFailEvent
import com.mredrock.cyxbs.main.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.main_activity_login.*
import kotlinx.android.synthetic.main.main_user_agreement_dialog.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = MAIN_LOGIN)
class LoginActivity : BaseViewModelActivity<LoginViewModel>(), EventBusLifecycleSubscriber {


    private val lottieProgress = 0.39f//点击同意用户协议时的动画的时间

    override val loginConfig = LoginConfig(isCheckLogin = false)

    @JvmField
    @Autowired(name = ACTIVITY_CLASS)
    var targetActivity: Class<*>? = null

    @JvmField
    @Autowired(name = IS_EXIT_LOGIN)
    var isExitLogin: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
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
        main_user_agreement.setOnClickListener {
            val materialDialog = Dialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.main_user_agreement_dialog, materialDialog.window?.decorView as ViewGroup, false)
            materialDialog.setContentView(view)
            materialDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val userAgreementAdapter = UserAgreementAdapter(viewModel.userAgreementList)
            view.rv_content.adapter = userAgreementAdapter
            view.rv_content.layoutManager = LinearLayoutManager(this@LoginActivity)
            if (viewModel.userAgreementList.isNotEmpty()) view.loader.visibility = View.GONE
            materialDialog.show()
            viewModel.getUserAgreement {
                userAgreementAdapter.notifyDataSetChanged()
                view.loader.visibility = View.GONE
            }
        }
        tv_tourist_mode_enter.setOnClickListener {
            if (!viewModel.userAgreementIsCheck) {
                CyxbsToast.makeText(this, R.string.main_user_agreement_title, Toast.LENGTH_SHORT).show()
            } else {
                ServiceManager.getService(IAccountService::class.java).getVerifyService().loginByTourist()
                //如果是点击退出按钮到达的登录页那么就默认启动mainActivity，或者唤起登录页的Activity是MainActivity就默认启动
                if (isExitLogin != null && isExitLogin!!) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    if (targetActivity != null) {
                        startActivity(Intent(this, targetActivity))
                        finish()
                    } else {
                        finish()
                    }
                }
            }
        }
        //跳转到忘记密码模块
        tv_main_forget_password.setOnClickListener {
            ARouter.getInstance().build(MINE_FORGET_PASSWORD).navigation()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun loginAction() {
        if (viewModel.userAgreementIsCheck) {
            //放下键盘
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive) {
                inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
            }
            viewModel.login(et_account.text?.toString(), et_password.text?.toString(), landing) {
                //如果是点击退出按钮到达的登录页那么就默认启动mainActivity，或者唤起登录页的Activity是MainActivity就默认启动
                if (isExitLogin != null && isExitLogin!!) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
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
        finishAffinity()
        finish()
    }

    //这个方法可以在登录状态和未登录状态之间切换
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
