package com.mredrock.cyxbs.main.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.commit
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.getSp
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.network.mapOrThrowApiException
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.network.MainApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/14 21:03
 */
class SplashFragment : BaseFragment() {
  
  private val mIvSplash by R.id.main_iv_splash.view<ImageView>()
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.main_fragment_splash, container, false)
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initSplash()
  }
  
  private fun initSplash() {
    val lastUrl = getLastLoadUrl()
    if (lastUrl != null) {
      mIvSplash.setImageFromUrl(lastUrl.first, R.drawable.main_ic_bg_splash_big, R.drawable.main_ic_bg_splash_big)
      mIvSplash.setOnSingleClickListener {
        val uri = Uri.parse(lastUrl.second)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
      }
      requireView().postDelayed(1500) {
        endSplashPage()
      }
    } else {
      hideSplashPage() // 暂时隐藏闪屏页，将网络请求移到后台加载
      MainApiService::class.api
        .getStartPage()
        .mapOrThrowApiException()
        .map { list ->
          val calendar = Calendar.getInstance()
          val nowTime = calendar.timeInMillis
          list.find {
            calendar.time = it.start
            calendar.apply {
              set(Calendar.HOUR, 0)
              set(Calendar.MINUTE, 0)
              set(Calendar.SECOND, 0)
            }
            nowTime in calendar.timeInMillis..calendar.timeInMillis + 24 * 60 * 60 * 1000
          } ?: error("")
        }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .safeSubscribeBy {
          saveLoadUrl(it.photo_src, it.target_url) // 保存起来下次打开才显示
          endSplashPage()
        }
    }
  }
  
  /**
   * 隐藏闪屏页
   */
  private fun hideSplashPage() {
    if (requireView().isVisible) {
      requireView().gone()
    }
  }
  
  /**
   * 结束闪屏页，包括移除 Fragment
   */
  private fun endSplashPage() {
    if (requireView().isVisible) {
      // 加一个判断，防止重复加载动画
      requireView().gone()
      requireView().startAnimation(
        AlphaAnimation(1F, 0F).apply {
          duration = 300
          setAnimationListener(
            object : Animation.AnimationListener {
              override fun onAnimationStart(animation: Animation?) {}
              override fun onAnimationRepeat(animation: Animation?) {}
              override fun onAnimationEnd(animation: Animation?) {
                requireActivity().supportFragmentManager.commit {
                  this.remove(this@SplashFragment)
                }
              }
            }
          )
        }
      )
    } else {
      requireActivity().supportFragmentManager.commit {
        this.remove(this@SplashFragment)
      }
    }
  }
  
  private fun getLastLoadUrl(): Pair<String, String>? {
    val sp = requireContext().getSp("闪屏页记录")
    val lastEndTime = sp.getLong("上一次闪屏页截止时间", 0)
    if (System.currentTimeMillis() - lastEndTime > 0) {
      return null
    }
    val imgUrl = sp.getString("imgUrl", null) ?: ""
    val targetUrl = sp.getString("targetUrl", null) ?: ""
    return Pair(imgUrl, targetUrl)
  }
  
  private fun saveLoadUrl(imgUrl: String, targetUrl: String) {
    val sp = requireContext().getSp("闪屏页记录")
    val calendar = Calendar.getInstance().apply {
      set(Calendar.HOUR, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
    }
    calendar.add(Calendar.DATE, 1)
    sp.edit {
      putLong("上一次闪屏页截止时间", calendar.timeInMillis)
      putString("imgUrl", imgUrl)
      putString("targetUrl", targetUrl)
    }
  }
}