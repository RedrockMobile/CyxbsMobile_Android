package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
import org.greenrobot.eventbus.EventBus
import com.mredrock.cyxbs.common.utils.extensions.setFullScreen
import com.mredrock.cyxbs.main.utils.getSplashFile
import com.mredrock.cyxbs.main.utils.isDownloadSplash
import kotlinx.android.synthetic.main.main_activity_splash.*


class SplashActivity : BaseViewModelActivity<SplashViewModel>() {

    override val isFragmentActivity = false
    override val viewModelClass = SplashViewModel::class.java

    companion object {
        const val SPLASH_PHOTO_NAME = "splash_photo.jpg"
        const val SPLASH_PHOTO_LOCATION = "splash_store_location"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_splash)
        setFullScreen()

        //判断是否下载了Splash图，下载了就直接设置
        if(isDownloadSplash()){
            Glide.with(applicationContext).load(getSplashFile()).apply(RequestOptions().centerCrop()).into(splash_view)
        }

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
                viewModel.getQuestion(uri.getQueryParameter("qid") ?: "0")
            }
            URI_PATH_QA_ANSWER -> {

            }
            else -> {
                viewModel.finishModel.observeNotNullAndTrue {
                    startActivity<MainActivity>(true)
                }

                //如果启动时间都大于2s了，鄙人觉得就不要打开闪屏页伤害用户体验了
                if (System.currentTimeMillis() - BaseApp.startTime < 2000) {
                    viewModel.finishAfter(800)//设置这个值，改变闪屏页的时间。
                } else {
                    viewModel.finishAfter(0)
                }
            }
        }
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
    }

    override fun finish() {
        super.finish()
        //加个固定动画，避免突然闪屏带来的不良好的体验
        overridePendingTransition(R.anim.main_activity_splash_close,0)
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
}
