package com.mredrock.cyxbs.discover.pages.hiding

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.discover.KEY_SP_HIDING_DISCOVER
import com.mredrock.cyxbs.discover.R
import com.mredrock.cyxbs.discover.SP_HIDING_DISCOVER
import kotlinx.android.synthetic.main.discover_activity_hiding.*

class DiscoverHidingActivity : BaseActivity() {
    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_activity_hiding)
        common_toolbar.init("选择功能")
        discover_rv_hiding.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val mAdapter = DiscoverHidingRvAdapter(checkDiscoverHiding())
        discover_rv_hiding.adapter = mAdapter
        discover_btn_hiding_confirm.setOnClickListener {
            setHidingSP(mAdapter.mHideList)
            finish()
        }
    }

    private fun checkDiscoverHiding(): MutableList<Boolean> {
        val sp = sharedPreferences(SP_HIDING_DISCOVER)
        val mListHide = mutableListOf<Boolean>()
        for (i in 0..10) {
            mListHide.add(sp.getBoolean("$KEY_SP_HIDING_DISCOVER$i", true))
        }
        return mListHide
    }

    private fun setHidingSP(mList: List<Boolean>) {
        sharedPreferences(SP_HIDING_DISCOVER).editor {
            for (i in 0..10) {
                putBoolean("$KEY_SP_HIDING_DISCOVER$i", mList[i])
            }
        }

    }
}
