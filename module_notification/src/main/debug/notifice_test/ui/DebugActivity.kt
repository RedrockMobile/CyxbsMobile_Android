package notifice_test.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.mredrock.cyxbs.lib.base.BaseDebugActivity
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.redrock.module_notification.ui.activity.NotificationActivity

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/2
 * @Description:
 *
 */
class DebugActivity: BaseDebugActivity() {
    override val isNeedLogin: Boolean
        get() = true

    override fun onDebugCreate(savedInstanceState: Bundle?) {
//        val token = ApiGenerator.myToken
//        Log.d("token", token)
        startActivity(Intent(this, NotificationActivity::class.java))
    }

}