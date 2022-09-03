package course.ui

import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import com.mredrock.cyxbs.course.page.course.ui.home.HomeCourseVpFragment
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/15 18:49
 */
class DebugActivity : BaseDebugActivity() {
  
  override val isCancelStatusBar: Boolean
    get() = false
  
  override fun onDebugCreate(savedInstanceState: Bundle?) {
    val fcv = FragmentContainerView(this)
    setContentView(fcv)
    fcv.id = 123
    replaceFragment(fcv.id) {
      HomeCourseVpFragment()
    }
  }
}