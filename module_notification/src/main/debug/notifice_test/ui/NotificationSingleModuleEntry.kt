package notifice_test.ui

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.BaseSingleModuleActivity
import com.redrock.module_notification.ui.activity.NotificationActivity

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/2
 * @Description:
 *
 */
@Route(path = "/single/notification")
class NotificationSingleModuleEntry: ISingleModuleEntry {
    override fun getPage(activity: BaseSingleModuleActivity): ISingleModuleEntry.Page {
        return ISingleModuleEntry.ActionPage {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }
}