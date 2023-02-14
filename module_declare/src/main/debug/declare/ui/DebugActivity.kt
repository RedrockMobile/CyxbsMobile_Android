package declare.ui

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.declare.pages.post.PostActivity
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

class DebugActivity : BaseDebugActivity() {

    override val isNeedLogin: Boolean
        get() = false

    override fun onDebugCreate(savedInstanceState: Bundle?) {
        startActivity(
            Intent(this, PostActivity::class.java)
        )
    }
}