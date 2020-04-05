package com.mredrock.cyxbs.main.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.config.MAIN_LOGIN
import com.mredrock.cyxbs.common.config.MAIN_MAIN
import com.mredrock.cyxbs.common.config.MAIN_SPLASH
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setFullScreen
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.utils.getSplashFile
import com.mredrock.cyxbs.main.utils.isDownloadSplash
import com.mredrock.cyxbs.main.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.main_activity_splash.*
import kotlinx.android.synthetic.main.main_view_stub_splash.view.*

@Route(path = MAIN_SPLASH)
class SplashActivity : BaseViewModelActivity<SplashViewModel>() {

    override val isFragmentActivity = false
    override val viewModelClass = SplashViewModel::class.java

    companion object {
        const val SPLASH_PHOTO_NAME = "splash_photo.jpg"
        const val SPLASH_PHOTO_LOCATION = "splash_store_location"
    }

    private var isDownloadSplash: Boolean = false
    private lateinit var viewStub: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_splash)
        setFullScreen()
        //判断是否下载了Splash图，下载了就直接设置
        isDownloadSplash = if (isDownloadSplash(this@SplashActivity)) {
            viewStub = main_activity_splash_viewStub.inflate()//ViewStub加载

            Glide.with(applicationContext)
                    .load(getSplashFile(this@SplashActivity))
                    .apply(RequestOptions()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(viewStub.splash_view)
            true
        } else {
            false
        }

        val isLogin = ServiceManager.getService(IAccountService::class.java).getVerifyService().isLogin()
        viewModel.finishModel.observeNotNullAndTrue {
            if (isLogin) {
                navigateAndFinish(MAIN_MAIN)
            } else {
                navigateAndFinish(MAIN_LOGIN)
            }
        }
        if (isDownloadSplash) {//如果下载了
            object : CountDownTimer(3000, 1000) {
                override fun onFinish() {
                    viewModel.finishAfter(0)
                }

                override fun onTick(millisUntilFinished: Long) {
                    runOnUiThread {
                        val str = "跳过 ${millisUntilFinished / 1000}"
                        viewStub.main_activity_splash_skip.text = str
                    }
                }
            }.start()
            viewStub.main_activity_splash_skip.setOnClickListener {
                viewModel.finishAfter(0)
            }
        } else {//如果没闪屏页直接打开
            viewModel.finishAfter(0)
        }
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
    }

    //用来跳转并且关闭闪屏页
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
