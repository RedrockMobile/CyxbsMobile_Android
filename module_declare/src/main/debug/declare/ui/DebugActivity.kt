package declare.ui

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.declare.pages.main.page.activity.DeclareHomeActivity
import com.mredrock.cyxbs.declare.pages.post.PostActivity
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

class DebugActivity : BaseDebugActivity() {

    override val isNeedLogin: Boolean
        get() = true

    override fun onDebugCreate(savedInstanceState: Bundle?) {
        DeclareHomeActivity.startActivity(this)
    }
}