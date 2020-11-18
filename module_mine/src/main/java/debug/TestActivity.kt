package debug

import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.UserFragment
import kotlinx.android.synthetic.main.mine_activity_main.*

class TestActivity : BaseActivity() {

    private val mainFragment = UserFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(mine_main_fl.id, mainFragment)
        transaction.commit()

    }
}
