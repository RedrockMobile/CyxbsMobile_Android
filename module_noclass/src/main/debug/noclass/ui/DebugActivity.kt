package noclass.ui

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.lib.base.BaseDebugActivity
import com.mredrock.cyxbs.noclass.page.ui.NoClassActivity

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        noclass.ui
 * @ClassName:      DebugActivity
 * @Author:         Yan
 * @CreateDate:     2022年08月11日 16:27:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */

class DebugActivity : BaseDebugActivity() {

    override val isNeedLogin: Boolean
        get() = true

    override fun onDebugCreate(savedInstanceState: Bundle?) {
        startActivity(
            Intent(this, NoClassActivity::class.java)
        )
    }
}