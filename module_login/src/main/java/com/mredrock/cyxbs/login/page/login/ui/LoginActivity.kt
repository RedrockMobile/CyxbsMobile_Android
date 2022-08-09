package com.mredrock.cyxbs.login.page.login.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.transition.Explode
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.get
import androidx.core.view.postDelayed
import com.airbnb.lottie.LottieAnimationView
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.config.route.MINE_FORGET_PASSWORD
import com.mredrock.cyxbs.config.sp.SP_FIRST_TIME_OPEN
import com.mredrock.cyxbs.config.sp.SP_PRIVACY_AGREED
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.base.BaseApp
import com.mredrock.cyxbs.lib.base.ui.mvvm.BaseVmActivity
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.login.R
import com.mredrock.cyxbs.login.page.login.viewmodel.LoginViewModel
import com.mredrock.cyxbs.login.page.privacy.PrivacyActivity
import com.mredrock.cyxbs.login.page.useragree.UserAgreeActivity
import com.mredrock.cyxbs.login.ui.UserAgreementDialog

class LoginActivity : BaseVmActivity<LoginViewModel>() {
    companion object {
        
        private const val INTENT_TARGET = "intent_target"
        private const val INTENT_TOURIST_MODE = "intent_tourist_mode"
        
        fun startActivity(
            context: Context,
            successIntent: Intent?,
            touristModeIntent: Intent,
            intent: (Intent.() -> Unit)? = null
        ) {
            context.startActivity(
                Intent(context, LoginActivity::class.java)
                    .putExtra(INTENT_TARGET, successIntent)
                    .putExtra(INTENT_TOURIST_MODE, touristModeIntent)
                    .also { intent?.invoke(it) }
            )
        }
    }
    
    private val mIntentSuccess by lazyUnlock { intent.getParcelableExtra<Intent>(INTENT_TARGET) }
    private val mIntentTouristMode by lazyUnlock {
        intent.getParcelableExtra<Intent>(INTENT_TOURIST_MODE)!!
    }
    
    private val lottieProgress = 0.39f // 点击同意用户协议时的动画的时间
    
    private val mEtAccount by R.id.login_et_account.view<EditText>()
    private val mEtPassword by R.id.login_et_password.view<EditText>()
    private val mBtnLogin by R.id.login_btn_login.view<Button>()
    private val mLavCheck by R.id.login_lav_check.view<LottieAnimationView>()
    private val mTvTourist by R.id.login_tv_tourist_mode_enter.view<TextView>()
    private val mTvForget by R.id.login_tv_forget_password.view<TextView>()
    private val mTvUserAgreement by R.id.login_tv_user_agreement.view<TextView>()
    private val mContainer by R.id.login_container.view<ViewGroup>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_login)
        initView()
        initObserve()
    }

    private fun initView() {
        mEtPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                loginAction()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        mBtnLogin.setOnSingleClickListener {
            loginAction()
        }
        mLavCheck.setOnSingleClickListener {
            mLavCheck.playAnimation()
            viewModel.userAgreementIsCheck = !viewModel.userAgreementIsCheck
        }
        mLavCheck.addAnimatorUpdateListener {
            if (it.animatedFraction == 1f && viewModel.userAgreementIsCheck) {
                mLavCheck.pauseAnimation()
            } else if (it.animatedFraction >= lottieProgress && it.animatedFraction != 1f && !viewModel.userAgreementIsCheck) {
                mLavCheck.pauseAnimation()
            }
        }
        mTvTourist.setOnSingleClickListener {
            if (!viewModel.userAgreementIsCheck) {
                agreeToUserAgreement()
            } else {
                IAccountService::class.impl
                    .getVerifyService()
                    .loginByTourist()
                it.postDelayed(100) {
                    // 延迟一下，因为 sp 读取属性没得这么快，不然检测到仍未登录，会直接关闭应用
                    startActivity(
                        mIntentTouristMode
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // 清除掉其他 Activity
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    finish()
                }
            }
        }
        //跳转到忘记密码模块
        mTvForget.setOnSingleClickListener {
            ARouter.getInstance().build(MINE_FORGET_PASSWORD).navigation()
        }

        //如果是第一次使用app并且没有同意过用户协议，自动打开用户协议页面
        if (defaultSp.getBoolean(SP_FIRST_TIME_OPEN, true)) {
            showUserAgreement()
        }

        //设置用户协议和隐私政策的文字
        val spannableString = SpannableStringBuilder()
        spannableString.append("同意《用户协议》和《隐私权政策》")
        //解决文字点击后变色
        mTvUserAgreement.highlightColor =
            ContextCompat.getColor(this, android.R.color.transparent)
        //设置用户协议和隐私权政策点击事件
        val userAgreementClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, UserAgreeActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                /**设置文字颜色**/
                ds.color = ds.linkColor
                /**去除连接下划线**/
                ds.isUnderlineText = false
            }
        }
        val privacyClickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, PrivacyActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                /**设置文字颜色**/
                ds.color = ds.linkColor
                /**去除连接下划线**/
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(userAgreementClickSpan, 2, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableString.setSpan(privacyClickSpan, 9, 16, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        //设置用户协议和隐私权政策字体颜色
        val userAgreementSpan = ForegroundColorSpan(Color.parseColor("#2CDEFF"))
        val privacySpan = ForegroundColorSpan(Color.parseColor("#2CDEFF"))
        spannableString.setSpan(userAgreementSpan, 2, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableString.setSpan(privacySpan, 9, 16, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        mTvUserAgreement.text = spannableString
        mTvUserAgreement.movementMethod = LinkMovementMethod.getInstance()
    }
    
    private fun initObserve() {
        viewModel.loginEvent.collectLaunch {
            if (it) {
                startActivity(mIntentSuccess)
                finish()
            } else {
                changeUiState()
            }
        }
    }

    private fun loginAction() {
        if (viewModel.userAgreementIsCheck) {
            //放下键盘
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive) {
                inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
            }
            val stuNum = mEtAccount.text?.toString() ?: ""
            val password = mEtPassword.text?.toString() ?: ""
            if (checkDataCorrect(stuNum, password)) {
                changeUiState()
                viewModel.login(stuNum, password)
            }
        } else {
            agreeToUserAgreement()
        }
    }
    
    /**
     * 同意用户协议
     */
    private fun agreeToUserAgreement() {
        toast("请先同意用户协议吧")
    }
    
    // 这个方法可以在登录状态和未登录状态之间切换
    private fun changeUiState() {
        TransitionManager.beginDelayedTransition(mContainer, Explode())
        for (i in 0 until mContainer.childCount) {
            val view = mContainer[i]
            view.visibility = when (view.visibility) {
                View.GONE -> View.VISIBLE
                View.VISIBLE -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    private fun showUserAgreement() {
        UserAgreementDialog.show(
            supportFragmentManager,
            onNegativeClick = {
                viewModel.userAgreementIsCheck = false
                BaseApp.baseApp.privacyDenied()
                dismiss()
                finish()
            },
            onPositiveClick = {
                viewModel.userAgreementIsCheck = true
                mLavCheck.playAnimation()
                BaseApp.baseApp.privacyAgree()
                dismiss()
                defaultSp.edit {
                    putBoolean(SP_FIRST_TIME_OPEN, false)
                    putBoolean(SP_PRIVACY_AGREED, true)
                    commit()
                }
            }
        )
    }
    
    private fun checkDataCorrect(stuNum: String, idNum: String): Boolean {
        if (stuNum.length < 10) {
            toast("请检查一下学号吧，似乎有点问题")
            return false
        } else if (idNum.length < 6) {
            toast("请检查一下密码吧，似乎有点问题")
            return false
        }
        return true
    }
}