package debug.ui

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.food.ui.activity.FoodMainActivity
import com.mredrock.cyxbs.lib.base.BaseDebugActivity

class DebugActivity : BaseDebugActivity() {
    override val isNeedLogin: Boolean
        get() = true


    override fun onDebugCreate(savedInstanceState: Bundle?) {
        startActivity(
            Intent(this, FoodMainActivity::class.java
        ))
    }
}