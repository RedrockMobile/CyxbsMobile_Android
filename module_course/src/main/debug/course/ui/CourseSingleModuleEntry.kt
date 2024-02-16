package course.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.course.page.course.ui.home.HomeCourseVpFragment
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.SingleModuleActivity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/15 18:49
 */
@Route(path = "/single/course")
class CourseSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(activity: SingleModuleActivity): ISingleModuleEntry.Page {
    return ISingleModuleEntry.FragmentPage {
      HomeCourseVpFragment()
    }
  }
}