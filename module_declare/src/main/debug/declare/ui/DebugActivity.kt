package declare.ui

import android.os.Bundle
import com.mredrock.cyxbs.declare.pages.main.page.activity.DeclareHomeActivity
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

/**
 * ...
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/2/4
 * @Description:
 */
class DebugActivity : BaseDebugActivity() {
    override val isNeedLogin: Boolean
        get() = true

    override fun onDebugCreate(savedInstanceState: Bundle?) {
        DeclareHomeActivity.startActivity(this)
    }
}