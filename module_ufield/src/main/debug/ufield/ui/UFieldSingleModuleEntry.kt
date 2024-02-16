package ufield.ui

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.BaseSingleModuleActivity
import com.mredrock.cyxbs.ufield.ui.activity.UFieldActivity

/**
 * description ：
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 19:53
 * version: 1.0
 */
@Route(path = "/single/ufield")
class UFieldSingleModuleEntry : ISingleModuleEntry {
    override fun getPage(activity: BaseSingleModuleActivity): ISingleModuleEntry.Page {
        return ISingleModuleEntry.ActionPage {
            startActivity(Intent(this, UFieldActivity::class.java))
        }
    }
}