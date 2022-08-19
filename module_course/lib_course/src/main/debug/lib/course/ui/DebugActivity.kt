package lib.course.ui

import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.mredrock.cyxbs.lib.base.BaseDebugActivity
import com.mredrock.cyxbs.lib.course.fragment.CourseVpFragment

/**
 * 因为没有依赖 lib_utils，所以不能继承于 BaseDebugActivity
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:00
 */
class DebugActivity : BaseDebugActivity() {
  
  override val isNeedLogin: Boolean
    get() = false
  
  override val isCancelStatusBar: Boolean
    get() = false
  
  override fun onDebugCreate(savedInstanceState: Bundle?) {
    val fcv = FragmentContainerView(this)
    setContentView(fcv)
    fcv.id = 78362892
    if (savedInstanceState == null) {
      supportFragmentManager.commit {
        replace(fcv.id, CourseVpFragment.newInstance(1660492800000L, 21))
      }
    }
  }
}