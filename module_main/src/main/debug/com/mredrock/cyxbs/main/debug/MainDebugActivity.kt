package com.mredrock.cyxbs.main.debug

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.main.ui.main.MainActivity
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.SingleModuleActivity

/**
 * .
 *
 * @author 985892345
 * 2024/3/19 13:19
 */
@Route(path = "/single/main")
class MainDebugActivity : ISingleModuleEntry {
  override fun getPage(activity: SingleModuleActivity): ISingleModuleEntry.Page {
    return ISingleModuleEntry.ActionPage {
      activity.startActivity(Intent(activity, MainActivity::class.java))
    }
  }
}