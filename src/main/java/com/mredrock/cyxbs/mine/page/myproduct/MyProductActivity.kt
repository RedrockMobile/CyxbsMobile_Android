package com.mredrock.cyxbs.mine.page.myproduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.mine.util.ui.BaseTabLayoutActivity

class MyProductActivity : BaseTabLayoutActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        common_toolbar.title = "我的商品"
        val fragmentList = listOf<Fragment>(MyProductFragment(MyProductFragment.UNCLAIMED), MyProductFragment(MyProductFragment.CLAIMED))
        val titleList = listOf("未领取", "已领取")
        init(fragmentList, titleList)
    }
}
