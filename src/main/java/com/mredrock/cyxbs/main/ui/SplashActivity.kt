package com.mredrock.cyxbs.main.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.config.*
import com.mredrock.cyxbs.common.event.AskLoginEvent
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.bean.FinishEvent
import com.mredrock.cyxbs.main.viewmodel.SplashViewModel
import org.greenrobot.eventbus.EventBus
import com.mredrock.cyxbs.main.utils.getSplashFile
import com.mredrock.cyxbs.main.utils.isDownloadSplash
import kotlinx.android.synthetic.main.main_activity_splash.*
import kotlinx.android.synthetic.main.main_view_stub_splash.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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
        val fragments = ArrayList<Fragment>()
        fragments.add(ARouter.getInstance().build(DISCOVER_ENTRY).navigation() as Fragment)
        EventBus.getDefault().postSticky(fragments)
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
                    startActivity<MainActivity>()
                    overridePendingTransition(0, 0)
                }

                if (isDownloadSplash) {//如果下载了

                    object : CountDownTimer(3000, 1000) {
                        override fun onFinish() {
                            viewModel.finishAfter(0)
                        }

                        override fun onTick(millisUntilFinished: Long) {
                            runOnUiThread {
                                viewStub.main_activity_splash_skip.text = "跳过 ${millisUntilFinished / 1000}"
                            }
                        }
                    }.start()

                    viewStub.main_activity_splash_skip.setOnClickListener {
                        viewModel.finishAfter(0)
                    }

                } else {//如果没闪屏页直接打开
                    viewModel.finishAfter(0)
                }
            }
        }
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFinish(event:FinishEvent){
        this.finish()
    }
}
