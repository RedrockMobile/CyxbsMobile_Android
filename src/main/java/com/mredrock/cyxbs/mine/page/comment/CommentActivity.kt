package com.mredrock.cyxbs.mine.page.comment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.mine.util.ui.BaseTabLayoutActivity


/**
 * Created by zzzia on 2018/8/14.
 * 与我相关
 */
class CommentActivity : BaseTabLayoutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        common_toolbar.title = "评论"

        val fragmentList = listOf<Fragment>(CommentFragment(), CommentReceivedFragment())
        val titleList = listOf<String>("发出评论", "收到评论")
        init(fragmentList, titleList)
        isSlideable = false
    }
}
