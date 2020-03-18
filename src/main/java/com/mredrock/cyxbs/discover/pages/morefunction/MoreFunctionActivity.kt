package com.mredrock.cyxbs.discover.pages.morefunction

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.utils.MoreFunctionProvider
import kotlinx.android.synthetic.main.discover_activity_more_function.*

/**
 * @author zixuan
 * 2019/11/20
 */
class MoreFunctionActivity : BaseActivity() {
    override val isFragmentActivity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_activity_more_function)


        initRv(rv_discover_more_function)
        initSettings()
    }

    private fun initRv(rv: RecyclerView) {
        rv.adapter = MoreFunctionRvAdapter(MoreFunctionProvider.functions)
        rv.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
    }

    private fun initSettings() {
        iv_discover_more_function_back.setOnClickListener { finish() }
        iv_discover_more_function_settings.setOnClickListener {
            (rv_discover_more_function.adapter as MoreFunctionRvAdapter).apply {
                getBindable().settingClicked(this,this@MoreFunctionActivity,iv_discover_more_function_settings)
                setBindable(if(getBindable() is MoreFunctionBindable)MoreFunctionSettingBindable() else MoreFunctionBindable())
            }
        }
    }
}
