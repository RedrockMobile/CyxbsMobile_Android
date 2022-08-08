package login.ui

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.login.page.login.ui.LoginActivity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/7 21:38
 */
class DebugActivity : BaseActivity() {
  // 在 BaseActivity 的 onCreate() 中会主动启动 LoginActivity，所以就没有必要写其他代码
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    LoginActivity.startActivity(
      this,
      null,
      Intent(this, this::class.java)
    )
  }
}