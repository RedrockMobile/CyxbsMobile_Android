package login.ui

import android.os.Bundle
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/7 21:38
 */
class DebugActivity : BaseDebugActivity() {
  
  override fun onDebugCreate(savedInstanceState: Bundle?) {
    // 在 BaseDebugActivity 的 onCreate() 中会主动启动 LoginActivity，所以就没有必要写其他代码
  }
}