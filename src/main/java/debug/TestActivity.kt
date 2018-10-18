package debug

import android.os.Bundle
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.UserFragment
import kotlinx.android.synthetic.main.mine_activity_main.*

class TestActivity : BaseActivity() {
    override val isFragmentActivity: Boolean
        get() = false

    private val mainFragment = UserFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_main)

        BaseApp.user = User(stuNum = "2016211541",idNum = "191613",nickname = "zia",introduction = "...")
//        BaseApp.user = User(stuNum = "2016214689",idNum = "230044",nickname = "2016214689",introduction = "...")
//        BaseApp.user = User(stuNum = "2016210441",idNum = "170854",nickname = "2016210441",introduction = "...")
//        BaseApp.user = User(stuNum = "2016210049",idNum = "27001X",nickname = "2016210049",introduction = "...")

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(mine_main_fl.id, mainFragment)
        transaction.commit()

    }
}
