package store.ui

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.SingleModuleActivity
import com.mredrock.cyxbs.store.page.center.ui.activity.StoreCenterActivity

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/8/9 2:07
 */
@Route(path = "/single/store")
class StoreSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(activity: SingleModuleActivity): ISingleModuleEntry.Page {
    return ISingleModuleEntry.ActionPage {
      startActivity(Intent(this, StoreCenterActivity::class.java))
    }
  }
}