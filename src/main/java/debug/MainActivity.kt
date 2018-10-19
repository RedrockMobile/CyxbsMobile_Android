package debug

import android.os.Bundle
import com.jude.swipbackhelper.SwipeBackHelper
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.qa.R

class MainActivity : BaseActivity() {
    override val isFragmentActivity: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_main)
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false)
        common_toolbar.init(title = "邮问", listener = null)
    }
}
