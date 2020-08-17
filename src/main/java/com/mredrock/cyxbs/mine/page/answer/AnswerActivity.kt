package com.mredrock.cyxbs.mine.page.answer

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.mine.util.ui.BaseTabLayoutActivity


/**
 * Created by zzzia on 2018/8/14.
 * 帮一帮
 */
class AnswerActivity(override val isFragmentActivity: Boolean = false) : BaseTabLayoutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        common_toolbar.title = "我的回答"

        val fragmentList = listOf<Fragment>(AnswerPostedFm(), AnswerDraftFm())
        val titleList = listOf<String>("已回答", "草稿箱")
        init(fragmentList, titleList)
        isSlideable = false
    }
}
