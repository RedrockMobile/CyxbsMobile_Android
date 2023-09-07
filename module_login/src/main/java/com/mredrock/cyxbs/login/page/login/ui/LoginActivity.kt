package com.mredrock.cyxbs.login.page.login.ui

import android.app.Activity
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
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.get
import androidx.core.view.postDelayed
import com.airbnb.lottie.LottieAnimationView
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.update.IAppUpdateService
import com.mredrock.cyxbs.config.route.MAIN_MAIN
import com.mredrock.cyxbs.config.route.MINE_FORGET_PASSWORD
import com.mredrock.cyxbs.config.sp.SP_PRIVACY_AGREED
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.base.BaseApp
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.*
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.lib.utils.utils.judge.NetworkUtil
import com.mredrock.cyxbs.login.R
import com.mredrock.cyxbs.login.page.login.viewmodel.LoginViewModel
import com.mredrock.cyxbs.login.page.privacy.PrivacyActivity
import com.mredrock.cyxbs.login.page.useragree.UserAgreeActivity
import com.mredrock.cyxbs.login.ui.UserAgreementDialog

class LoginActivity : BaseActivity() {
  companion object {
    fun start(intent: (Intent.() -> Unit)? = null) {
      startInternal(false, null, intent)
    }

    fun start(
      successActivity: Class<out Activity>,
      intent: (Intent.() -> Unit)? = null
    ) {
      startInternal(false, successActivity, intent)
    }

    fun startReboot(intent: (Intent.() -> Unit)?) {
      startInternal(true, null, intent)
    }

    private fun startInternal(
      isReboot: Boolean,
      successActivity: Class<out Activity>?,
      intent: (Intent.() -> Unit)?
    ) {
      appContext.startActivity(
        Intent(appContext, LoginActivity::class.java)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 因为使用 appContext，所以需要加
          .putExtra(LoginActivity::mIsReboot.name, isReboot)
          .apply {
            if (successActivity != null) {
              putExtra(
                LoginActivity::mSuccessIntent.name,
                Intent(appContext, successActivity)
                  .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 因为使用 appContext，所以需要加
              )
            }
            if (isReboot) {
              addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // 清空 Activity 栈
            }
            intent?.invoke(this)
          }
      )
    }

    private const val INTENT_SUCCESS = "成功后跳转的 Activity"
  }

  private val mIsReboot by intent<Boolean>()
  private val mSuccessIntent by lazyUnlock {
    // 因为 by intent<>() 泛型不能写 null，所以采用原始的方法去拿
    intent.getParcelableExtra(INTENT_SUCCESS) as Intent?
  }

  private val mViewModel by viewModels<LoginViewModel>()

  private val mLottieProgress = 0.39f // 点击同意用户协议时的动画的时间

  private val mEtAccount by R.id.login_et_account.view<EditText>()
  private val mEtPassword by R.id.login_et_password.view<EditText>()
  private val mBtnLogin by R.id.login_btn_login.view<Button>()
  private val mLavCheck by R.id.login_lav_check.view<LottieAnimationView>()
  private val mTvTourist by R.id.login_tv_tourist_mode_enter.view<TextView>()
  private val mTvForget by R.id.login_tv_forget_password.view<TextView>()
  private val mTvUserAgreement by R.id.login_tv_user_agreement.view<TextView>()
  private val mContainer by R.id.login_container.view<ViewGroup>()
  
  // 后端是否可用
  private var mIsServerAvailable = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login_activity_login)
    initView()
    initObserve()
    initUpdate()
    initCheckNetWork()
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
      if (!mIsServerAvailable) {
        toast("当前后端服务不可用，可能无法正常登录")
      }
    }
    mLavCheck.setOnSingleClickListener {
      mLavCheck.playAnimation()
      mViewModel.userAgreementIsCheck = !mViewModel.userAgreementIsCheck
    }
    mLavCheck.addAnimatorUpdateListener {
      if (it.animatedFraction == 1f && mViewModel.userAgreementIsCheck) {
        mLavCheck.pauseAnimation()
      } else if (it.animatedFraction >= mLottieProgress && it.animatedFraction != 1f && !mViewModel.userAgreementIsCheck) {
        mLavCheck.pauseAnimation()
      }
    }
    mTvTourist.setOnSingleClickListener {
      if (!mViewModel.userAgreementIsCheck) {
        agreeToUserAgreement()
      } else {
        IAccountService::class.impl
          .getVerifyService()
          .loginByTourist()
        it.postDelayed(30) {
          // 延迟一下，因为 sp 保存属性没得这么快
          ServiceManager.activity(MAIN_MAIN) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // 游客模式也需要清空 Activity 栈
          }
          finish()
        }
      }
    }
    //跳转到忘记密码模块
    mTvForget.setOnSingleClickListener {
      ARouter.getInstance().build(MINE_FORGET_PASSWORD).navigation()
    }
  
    if (!mViewModel.userAgreementIsCheck) {
      // 显示用户协议 dialog
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
    }.wrapByNoLeak(mTvUserAgreement) // 防止内存泄漏
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
    }.wrapByNoLeak(mTvUserAgreement) // 防止内存泄漏
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
    mViewModel.loginEvent.collectLaunch {
      if (it) {
        if (mIsReboot) {
          ServiceManager.activity(MAIN_MAIN) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) //清空 Activity 栈
          }
        } else {
          if (mSuccessIntent != null) {
            startActivity(mSuccessIntent)
          }
        }
        finish()
      } else {
        changeUiState()
      }
    }
  }

  private fun loginAction() {
    if (mViewModel.userAgreementIsCheck) {
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
        mViewModel.login(stuNum, password)
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
    UserAgreementDialog.Builder(this)
      .setPositiveClick {
        mViewModel.userAgreementIsCheck = true
        mLavCheck.playAnimation()
        BaseApp.baseApp.privacyAgree()
        dismiss()
        defaultSp.edit {
          putBoolean(SP_PRIVACY_AGREED, true)
        }
      }.setNegativeClick {
        mViewModel.userAgreementIsCheck = false
        BaseApp.baseApp.privacyDenied()
        dismiss()
        finish()
      }.show()
  }

  private fun checkDataCorrect(stuNum: String, idNum: String): Boolean {
    if (idNum.length < 6) {
      toast("请检查一下密码吧，似乎有点问题")
      return false
    }
    return true
  }

  private fun initUpdate() {
    IAppUpdateService::class.impl.tryNoticeUpdate(this)
  }
  
  private fun initCheckNetWork() {
    launch {
      NetworkUtil.tryPingNetWork()?.onFailure {
        mIsServerAvailable = false
      }
    }
  }
}