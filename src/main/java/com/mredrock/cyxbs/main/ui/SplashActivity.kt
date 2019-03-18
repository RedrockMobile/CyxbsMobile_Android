package com.mredrock.cyxbs.main.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.main_activity_splash.*

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
        splash_view.postDelayed({ startActivity<MainActivity>(true) }, 2000)

        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        viewModel.getStartPage()

        viewModel.startPage.observe(this, Observer {
            Glide.with(this).load(it?.photo_src).apply(RequestOptions().centerCrop()).into(splash_view)
        })

    }

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
