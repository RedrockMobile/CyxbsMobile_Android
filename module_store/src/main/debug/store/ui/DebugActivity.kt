package store.ui

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.lib.base.BaseDebugActivity
import com.mredrock.cyxbs.store.page.center.ui.activity.StoreCenterActivity

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/8/9 2:07
 */
class DebugActivity : BaseDebugActivity() {
  
  override fun onDebugCreate(savedInstanceState: Bundle?) {
    startActivity(
      Intent(this, StoreCenterActivity::class.java)
    )
  }
}