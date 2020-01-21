package com.mredrock.cyxbs.discover.pages.morefunction

import android.os.Bundle
import android.widget.Toast
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

                if (!this.getIsSettingMode()) {
                    changeSettingMode()
                    iv_discover_more_function_settings.setImageResource(R.drawable.discover_ic_more_function_finish)
                } else {

                    if(getChosenList().size>=3) {
                        changeSettingMode()
                        iv_discover_more_function_settings.setImageResource(R.drawable.discover_ic_settings)
                        MoreFunctionProvider.saveHomePageFunctionsToSp(this.getChosenList())
                        this.resetChosenList()
                        Toast.makeText(this@MoreFunctionActivity,"更新成功",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@MoreFunctionActivity,"要选择三个哦",Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}
