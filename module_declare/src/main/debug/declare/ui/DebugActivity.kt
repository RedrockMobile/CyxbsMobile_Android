package declare.ui

import android.os.Bundle
import com.mredrock.cyxbs.declare.pages.main.page.activity.HomeActivity
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

class DebugActivity : BaseDebugActivity() {

    override val isNeedLogin: Boolean
        get() = true

    override fun onDebugCreate(savedInstanceState: Bundle?) {
        HomeActivity.startActivity(this)
    }
}