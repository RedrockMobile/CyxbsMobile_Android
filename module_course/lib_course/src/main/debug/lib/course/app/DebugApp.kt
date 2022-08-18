package lib.course.app

import android.app.Application
import com.mredrock.cyxbs.config.ConfigApplicationWrapper

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 14:59
 */
class DebugApp : Application() {
  override fun onCreate() {
    super.onCreate()
    // 因为该模块没有依赖 lib_base，所以需要自己手动给 lib_config 传入 application
    ConfigApplicationWrapper.initialize(this)
  }
}