package crash.ui

import com.mredrock.cyxbs.lib.base.BaseApp
import com.mredrock.lib.crash.core.CyxbsCrashMonitor

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/11/5
 * @Description:
 */
class CrashDebugApp:BaseApp() {
    override fun onCreate() {
        super.onCreate()
        CyxbsCrashMonitor.install(this)
    }
}