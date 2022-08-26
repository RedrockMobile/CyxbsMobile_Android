package sport.ui

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.lib.base.BaseDebugActivity
import com.mredrock.cyxbs.sport.ui.activity.SportDetailActivity

/**
 * @author : why
 * @time   : 2022/8/11 00:23
 * @bless  : God bless my code
 */
class DebugActivity : BaseDebugActivity() {
    override val isNeedLogin: Boolean
        get() = true

    override fun onDebugCreate(savedInstanceState: Bundle?) {
        startActivity(
            Intent(this, SportDetailActivity::class.java)
        )
    }
}