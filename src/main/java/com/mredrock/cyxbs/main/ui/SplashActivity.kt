package com.mredrock.cyxbs.main.ui

import android.os.Bundle
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
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.viewmodel.SplashViewModel
import org.greenrobot.eventbus.EventBus
import com.mredrock.cyxbs.common.utils.extensions.setFullScreen
import kotlinx.android.synthetic.main.main_activity_splash.*


class SplashActivity : BaseViewModelActivity<SplashViewModel>() {

    override val isFragmentActivity = false
    override val viewModelClass = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        val t1 = System.nanoTime()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_splash)
        setFullScreen()

        val uri = intent.data
        println("sadasd ${uri?.path}")
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
                viewModel.finishAfter(0)
            }
        }
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        viewModel.getStartPage()

        viewModel.startPage.observe(this, Observer {
            Glide.with(this).load(it?.photo_src).apply(RequestOptions().centerCrop()).into(splash_view)
        })
        val t2 = System.nanoTime()
        LogUtils.d("MyTag splash","${t2-t1}")
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
