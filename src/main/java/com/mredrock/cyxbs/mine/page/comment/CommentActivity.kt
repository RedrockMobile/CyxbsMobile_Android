package com.mredrock.cyxbs.mine.page.comment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.mine.util.ui.BaseTabLayoutActivity
import kotlinx.android.synthetic.main.mine_activity_tablayout_common.*


/**
 * Created by zzzia on 2018/8/14.
 * 与我相关
 */
class CommentActivity : BaseTabLayoutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mine_tl_tv_title.text = "评论回复"

        val fragmentList = listOf<Fragment>(CommentFragment(), ResponseFragment())
        val titleList = listOf<String>("发出评论", "收到回复")
        init(fragmentList, titleList)
    }
}
