package crash.ui

import android.content.Context
import com.mredrock.cyxbs.lib.base.BaseApp

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/7/9
 * @Description:
 */
class DebugApp: BaseApp() {
    companion object{
        lateinit var appContext:Context
        private set
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}