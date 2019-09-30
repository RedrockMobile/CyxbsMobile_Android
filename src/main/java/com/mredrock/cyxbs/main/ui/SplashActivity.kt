package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.config.URI_PATH_QA_ANSWER
import com.mredrock.cyxbs.common.config.URI_PATH_QA_QUESTION
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.viewmodel.SplashViewModel
import org.greenrobot.eventbus.EventBus
import com.mredrock.cyxbs.main.utils.getSplashFile
import com.mredrock.cyxbs.main.utils.isDownloadSplash
import kotlinx.android.synthetic.main.main_activity_splash.*
import java.sql.Time
import java.util.*
import kotlin.concurrent.schedule


class SplashActivity : BaseViewModelActivity<SplashViewModel>() {

    override val isFragmentActivity = false
    override val viewModelClass = SplashViewModel::class.java

    companion object {
        const val SPLASH_PHOTO_NAME = "splash_photo.jpg"
        const val SPLASH_PHOTO_LOCATION = "splash_store_location"
    }

    private var isDownloadSplash: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_splash)
        setFullScreen()

        //判断是否下载了Splash图，下载了就直接设置
        isDownloadSplash = if (isDownloadSplash()) {
            splash_view.visible()//防止图片拉伸
            main_activity_splash_container.visible()

            Glide.with(applicationContext)
                    .load(getSplashFile())
                    .apply(RequestOptions()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(splash_view)
            true
        }else{
            splash_view.gone()//防止图片拉伸
            main_activity_splash_container.gone()
            false
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

                if(isDownloadSplash){//如果下载了

                    object : CountDownTimer(3000,1000){
                        override fun onFinish() {
                            viewModel.finishAfter(0)
                        }
                        override fun onTick(millisUntilFinished: Long) {
                            runOnUiThread {
                                main_activity_splash_skip.text = "跳过 ${millisUntilFinished/1000}"
                            }
                        }
                    }.start()

                    main_activity_splash_skip.setOnClickListener {
                        viewModel.finishAfter(0)
                    }

                }else{//如果没闪屏页直接打开
                    viewModel.finishAfter(0)
                }
            }
        }
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
    }

    override fun finish() {
        super.finish()
        //加个固定动画，避免突然闪屏带来的不良好的体验
        overridePendingTransition(R.anim.main_activity_splash_close, 0)
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
