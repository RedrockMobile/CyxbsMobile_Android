package noclass.ui

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.noclass.page.ui.activity.NoClassActivity
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.BaseSingleModuleActivity

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        noclass.ui
 * @ClassName:      DebugActivity
 * @Author:         Yan
 * @CreateDate:     2022年08月11日 16:27:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
@Route(path = "/single/noclass")
class NoClassSingleModuleEntry : ISingleModuleEntry {
    override fun getPage(activity: BaseSingleModuleActivity): ISingleModuleEntry.Page {
        return ISingleModuleEntry.ActionPage {
            startActivity(Intent(this, NoClassActivity::class.java))
        }
    }
}