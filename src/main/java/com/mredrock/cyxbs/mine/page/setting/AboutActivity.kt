package com.mredrock.cyxbs.mine.page.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.mredrock.cyxbs.common.config.APP_WEBSITE
import com.mredrock.cyxbs.common.config.updateFile
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.getAppVersionName
import com.mredrock.cyxbs.common.utils.update.UpdateEvent
import com.mredrock.cyxbs.common.utils.update.UpdateUtils
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_about.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast

class AboutActivity(override val isFragmentActivity: Boolean = false) : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_about)

        mine_about_toolbar.init("关于",
                R.drawable.mine_ic_arrow_left)

        setAppVersionName()

        mine_about_website.setOnClickListener { clickWebsite() }
        mine_about_legal.setOnClickListener { clickLegal() }
        mine_about_update.setOnClickListener { clickUpdate() }
    }

    private fun clickUpdate() {
        UpdateUtils.checkUpdate(this) {
            toast("已经是最新版了哦")
        }
    }

    private fun clickWebsite() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(APP_WEBSITE))
        startActivity(intent)
    }

    private fun clickLegal() {
        MaterialDialog.Builder(this)
                .title("使用条款")
                .content("版权归红岩网校工作站所有,感谢您的使用")
                .positiveText("确定")
                .build()
                .show()
    }

    private fun setAppVersionName() {
        mine_about_version.text = StringBuilder("Version ").append(getAppVersionName(this@AboutActivity))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun installUpdate(event: UpdateEvent) {
        UpdateUtils.installApk(this, updateFile)
    }
}
