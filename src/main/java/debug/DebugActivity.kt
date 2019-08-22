package debug

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.pages.discover.DiscoverFragment
import com.mredrock.cyxbs.discover.pages.hiding.DiscoverHidingActivity
import kotlinx.android.synthetic.main.activity_debug.*

class DebugActivity : BaseActivity() {
    override val isFragmentActivity = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(layout_debug.id, DiscoverFragment() as Fragment)
        fragmentTransaction.commit()
        btn_debug.setOnClickListener {
            startActivity<DiscoverHidingActivity>()
        }
    }
}
