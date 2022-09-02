package ui

import android.app.Application
import android.content.Context
import com.mredrock.lib.crash.DebugCyxbsCrashMonitor

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/7/9
 * @Description:
 */
class DebugApp:Application() {
    companion object{
        lateinit var appContext:Context
        private set
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}