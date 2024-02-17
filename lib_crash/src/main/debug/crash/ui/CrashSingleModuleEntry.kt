package crash.ui

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.SingleModuleActivity
import com.mredrock.lib.crash.core.CyxbsCrashMonitor

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/11/5
 * @Description:
 */
@Route(path = "/single/crash")
class CrashSingleModuleEntry: ISingleModuleEntry {

    override val isNeedLogin: Boolean
        get() = false

    override fun getPage(activity: SingleModuleActivity): ISingleModuleEntry.Page {
        // 因为 CrashService 只会在 release 时才开启，所以这里单模块时显示安装
        CyxbsCrashMonitor.install(activity.application)
        return ISingleModuleEntry.ActionPage {
            startActivity(Intent(this, DebugFirstActivity::class.java))
        }
    }
}