package com.mredrock.cyxbs.main.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.config.URI_PATH_QA_ANSWER
import com.mredrock.cyxbs.common.config.URI_PATH_QA_QUESTION
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.main_activity_splash.*
import org.greenrobot.eventbus.EventBus

class SplashActivity : BaseViewModelActivity<SplashViewModel>() {
    companion object {
        val TAG = SplashActivity::class.java.simpleName
    }

    override val isFragmentActivity = false
    override val viewModelClass = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_splash)
        setFullScreen()

        val uri = intent.data
        when (uri?.path) {
            URI_PATH_QA_QUESTION -> {
                if (!BaseApp.isLogin) {
                    EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用邮问哦~"))
                    return
                }
                viewModel.finishModel.observeNotNullAndTrue {
                    navigateAndFinish(QA_ANSWER_LIST)
                }
                viewModel.getQuestion(uri.getQueryParameter("qid"))
            }
            URI_PATH_QA_ANSWER -> {

            }
            else -> {
                viewModel.finishModel.observeNotNullAndTrue {
                    startActivity<MainActivity>(true)
                }
                viewModel.finishAfter(2000)
            }
        }

        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        viewModel.getStartPage()

        viewModel.startPage.observe(this, Observer {
            Glide.with(this).load(it?.photo_src).apply(RequestOptions().centerCrop()).into(splash_view)
        })

    }

    private fun navigateAndFinish(path: String) {
        ARouter.getInstance().build(path).navigation()
        finish()
    }

    private inline fun SingleLiveEvent<Boolean>.observeNotNullAndTrue(crossinline onChange: () -> Unit) = observe(this@SplashActivity, Observer {
        if (it == true) {
            onChange()
        }
    })

    private fun setFullScreen() {
        val decorView = window.decorView
        var uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        decorView.systemUiVisibility = uiOptions
    }
}
