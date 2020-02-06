package com.mredrock.cyxbs.mine.page.ask

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.mine.util.ui.BaseTabLayoutActivity
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*


/**
 * Created by zzzia on 2018/8/14.
 * 问一问
 */
class AskActivity(override val isFragmentActivity: Boolean = false) : BaseTabLayoutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        common_toolbar.title = "我的提问"
        val fragmentList = listOf<Fragment>(AskPostedFm(), AskDraftFm())
        val titleList = listOf<String>("已发布", "草稿箱")
        init(fragmentList, titleList)
    }
}
