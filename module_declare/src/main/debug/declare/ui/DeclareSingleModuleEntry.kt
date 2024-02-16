package declare.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.declare.pages.main.page.activity.HomeActivity
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.SingleModuleActivity

/**
 * .
 *
 * @author 985892345
 * @date 2024/2/15 14:03
 */
@Route(path = "/single/declare")
class DeclareSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(activity: SingleModuleActivity): ISingleModuleEntry.Page {
    return ISingleModuleEntry.ActionPage {
      HomeActivity.startActivity(activity)
    }
  }
}