package com.mredrock.cyxbs.mine.page.setting

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.config.COURSE_SHOW_STATE
import com.mredrock.cyxbs.common.config.WIDGET_SETTING
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.defaultSharedPreferences
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_setting.*

class SettingActivity : BaseActivity() {

    /*override val isFragmentActivity: Boolean
        get() = false*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_setting)

        //初始化toolbar
        mine_setting_toolbar.withSplitLine(true)
        mine_setting_toolbar.setTitleLocationAtLeft(false)

        //启动App优先显示课表
        mine_setting_switch.setOnCheckedChangeListener { _, isChecked ->
            defaultSharedPreferences.editor {
                if (isChecked) {
                    putBoolean(COURSE_SHOW_STATE, true)
                } else {
                    putBoolean(COURSE_SHOW_STATE, false)
                }
            }
        }
        mine_setting_switch.isChecked = context?.defaultSharedPreferences?.getBoolean(COURSE_SHOW_STATE, false)
                ?: false

        //自定义桌面小组件
        mine_setting_fm_edit_widget.setOnClickListener {
            ARouter.getInstance().build(WIDGET_SETTING).navigation()
        }

        //账号安全
        //TODO:合并dev分支
        //屏蔽此人
    }
}