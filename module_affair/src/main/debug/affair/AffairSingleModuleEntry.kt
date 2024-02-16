package affair

import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.SingleModuleActivity

/**
 * .
 *
 * @author 985892345
 * @date 2024/2/15 13:53
 */
@Route(path = "/single/affair")
class AffairSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(activity: SingleModuleActivity): ISingleModuleEntry.Page {
    return ISingleModuleEntry.ActionPage {
      ServiceManager(IAffairService::class)
        .startActivityForAddAffair(
          0, 1, 1, 1
        )
    }
  }
}