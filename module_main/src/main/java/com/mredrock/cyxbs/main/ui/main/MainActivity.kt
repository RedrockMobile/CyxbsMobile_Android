package com.mredrock.cyxbs.main.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.login.ILoginService
import com.mredrock.cyxbs.api.update.IAppUpdateService
import com.mredrock.cyxbs.config.route.DISCOVER_EMPTY_ROOM
import com.mredrock.cyxbs.config.route.DISCOVER_GRADES
import com.mredrock.cyxbs.config.route.DISCOVER_SCHOOL_CAR
import com.mredrock.cyxbs.config.route.MAIN_MAIN
import com.mredrock.cyxbs.config.sp.SP_COURSE_SHOW_STATE
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.adapter.MainAdapter
import com.mredrock.cyxbs.main.ui.course.CourseFragment
import com.mredrock.cyxbs.main.ui.splash.SplashFragment
import com.mredrock.cyxbs.main.viewmodel.MainViewModel
import com.mredrock.cyxbs.main.widget.BottomNavLayout

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/14 20:49
 */
@Route(path = MAIN_MAIN)
class MainActivity : BaseActivity() {
  
  private val mViewModel by viewModels<MainViewModel>()
  
  private val mAccountService = IAccountService::class.impl
  
  private val mViewPager by R.id.main_vp.view<ViewPager2>()
  private val mBottomNavLayout by R.id.main_bnl_bottom_nav.view<BottomNavLayout>()
  
  private var mIsLogin = false
  
  override fun onCreate(savedInstanceState: Bundle?) {
    // 还原主题，因为 MainActivity 最开始在 AndroidManifest.xml 设置了闪屏页背景，所以这里需要还原
    setTheme(com.mredrock.cyxbs.config.R.style.ConfigAppTheme)
    super.onCreate(savedInstanceState)
    val isLogin = checkIsLogin()
    if (isLogin != null) {
      mIsLogin = isLogin
      initUI()
    }
  }
  
  private fun checkIsLogin(): Boolean? {
    if (!mAccountService.getVerifyService().isTouristMode()) {
      // 不是游客模式
      if (!mAccountService.getVerifyService().isLogin() || mAccountService.getVerifyService().isRefreshTokenExpired()) {
        // 未登录 和 refreshToken 过期时 需要跳转到登录界面
        ILoginService::class.impl
          .startLoginActivityReboot()
        finish()
        return null
      }
      return true
    }
    return false
  }
  
  /**
   * 在没有跳转到登录界面时调用，用于初始化 UI
   *
   * 以后初始化的内容写在这里
   */
  private fun initUI() {
    setContentView(R.layout.main_activity_main)
    initSplash()
    initCourse()
    initViewPager()
    initBottomNav()
    initUpdate()
  }
  
  // 初始化闪屏页
  private fun initSplash() {
    replaceFragment(R.id.main_fcv_splash_fragment) {
      SplashFragment()
    }
  }
  
  private fun initCourse() {
    replaceFragment(R.id.main_fcv_course_fragment) {
      CourseFragment()
    }
    mViewModel.courseBottomSheetOffset.observe {
      // 底部按钮跟随课表展开而变化
      mBottomNavLayout.translationY = mBottomNavLayout.height * it
      mBottomNavLayout.alpha = 1 - it
    }
    when (intent.action) {
      DESKTOP_SHORTCUT_COURSE -> {
        if (mIsLogin) {
          mViewPager.postDelayed(300) {
            // 延迟些时间才打开课表，因为打开快了，课表会出现短时间的白屏
            mViewModel.courseBottomSheetExpand.value = true
          }
        }
      }
      DESKTOP_SHORTCUT_EXAM -> {
        if (mIsLogin) {
          ServiceManager.activity(DISCOVER_GRADES)
        }
      }
      DESKTOP_SHORTCUT_SCHOOL_CAR -> {
        ServiceManager.activity(DISCOVER_SCHOOL_CAR)
      }
      DESKTOP_SHORTCUT_EMPTY_ROOM -> {
        ServiceManager.activity(DISCOVER_EMPTY_ROOM)
      }
      else -> {
        if (mIsLogin && defaultSp.getBoolean(SP_COURSE_SHOW_STATE, false)) {
          // 打开应用优先显示课表的设置
          mViewPager.postDelayed(300) {
            // 延迟些时间才打开课表，因为打开快了，课表会出现短时间的白屏
            mViewModel.courseBottomSheetExpand.value = true
          }
        }
      }
    }
  }
  
  private fun initViewPager() {
    mViewPager.adapter = MainAdapter(this)
    mViewPager.isUserInputEnabled = false
  }
  
  private fun initBottomNav() {
    mBottomNavLayout.addSelectListener {
      mViewPager.currentItem = it
      when (it) {
        0, 2 -> {
          mBottomNavLayout.cardElevation = 0F
          if (!mIsActivityRebuilt) {
            // 在重建 Activity 后这里会回调一次，这个时候需要还原之前的状态，这里不能去设置成 false
            mViewModel.courseBottomSheetExpand.value = false
          }
        }
        1 -> {
          mBottomNavLayout.cardElevation = 4.dp2pxF
          mViewModel.courseBottomSheetExpand.value = null
        }
      }
    }
  }
  
  private fun initUpdate() {
    IAppUpdateService::class.impl.tryNoticeUpdate(this)
  }
  
  override fun onBackPressed() {
    if (mViewModel.courseBottomSheetExpand.value == true) {
      mViewModel.courseBottomSheetExpand.value = false
    } else {
      super.onBackPressed()
    }
  }
  
  companion object {
    // 长按桌面图标的那个东西，对应 AndroidManifest.xml 中的设置
    const val DESKTOP_SHORTCUT_COURSE = "com.mredrock.cyxbs.action.COURSE"
    const val DESKTOP_SHORTCUT_EXAM = "android.intent.action.EXAM"
    const val DESKTOP_SHORTCUT_SCHOOL_CAR = "android.intent.action.SCHOOL_CAR"
    const val DESKTOP_SHORTCUT_EMPTY_ROOM = "android.intent.action.EMPTY_ROOM"
  }
}