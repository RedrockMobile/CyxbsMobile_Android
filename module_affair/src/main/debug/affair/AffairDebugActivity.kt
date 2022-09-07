package affair

import android.os.Bundle
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/6
 * description:
 */
class DebugActivity : BaseDebugActivity() {
  override val isNeedLogin: Boolean
    get() = true

  override fun onDebugCreate(savedInstanceState: Bundle?) {
    com.mredrock.cyxbs.lib.utils.service.ServiceManager(IAffairService::class)
      .startAffairEditActivity(
        this,
        0, 1, 2
      )
  }
}