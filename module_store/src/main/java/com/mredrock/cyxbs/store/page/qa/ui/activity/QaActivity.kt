package com.mredrock.cyxbs.store.page.qa.ui.activity

import android.os.Bundle
import androidx.fragment.app.commit
import com.mredrock.cyxbs.common.config.QA_ENTRY
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.store.R

/**
 * 直接跳转到 module_qa 模块的 DynamicFragment
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/9/2
 * @time 23:23
 */
class QaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_qa)

        initFragment()
    }

    private fun initFragment() {
        val qaFragment = supportFragmentManager.findFragmentByTag(QA_ENTRY)
            ?: ServiceManager.getService(QA_ENTRY)
        supportFragmentManager.commit {
            replace(R.id.store_fragment_qa, qaFragment, QA_ENTRY)
            show(qaFragment)
        }
    }
}