package lib.course.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.mredrock.cyxbs.lib.course.CoursePageFragment

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 15:00
 */
class DebugActivity : AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val fcv = FragmentContainerView(this)
    setContentView(fcv)
    fcv.id = 78362892
    if (savedInstanceState == null) {
      supportFragmentManager.commit {
        replace(fcv.id, CoursePageFragment())
      }
    }
  }
}