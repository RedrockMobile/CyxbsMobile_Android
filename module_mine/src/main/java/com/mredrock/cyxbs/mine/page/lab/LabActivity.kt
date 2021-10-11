package com.mredrock.cyxbs.mine.page.lab

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.SKIN_ENTRY
import com.mredrock.cyxbs.common.skin.SkinManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.mine.R
import kotlinx.android.synthetic.main.mine_activity_lab.*

class LabActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_activity_lab)
        initToolbar()
        mine_lab_fl_skin.setOnSingleClickListener { doIfLogin { ARouter.getInstance().build(SKIN_ENTRY).navigation() } }

    }

    private fun initToolbar() {
        common_toolbar.apply {
            initWithSplitLine("掌邮实验室",
                    false,
                    R.drawable.mine_ic_arrow_left,
                    View.OnClickListener {
                        finishAfterTransition()
                    })
            navigationIcon = SkinManager.getDrawable("mine_ic_arrow_left", R.drawable.mine_ic_arrow_left)
        }
        LabDialog.show(this)
    }
}