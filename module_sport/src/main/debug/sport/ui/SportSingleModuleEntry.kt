package sport.ui

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.BaseSingleModuleActivity
import com.mredrock.cyxbs.sport.ui.activity.SportDetailActivity

/**
 * @author : why
 * @time   : 2022/8/11 00:23
 * @bless  : God bless my code
 */
@Route(path = "/single/sport")
class SportSingleModuleEntry : ISingleModuleEntry {
    override fun getPage(activity: BaseSingleModuleActivity): ISingleModuleEntry.Page {
        return ISingleModuleEntry.ActionPage {
            startActivity(Intent(this, SportDetailActivity::class.java))
        }
    }
}