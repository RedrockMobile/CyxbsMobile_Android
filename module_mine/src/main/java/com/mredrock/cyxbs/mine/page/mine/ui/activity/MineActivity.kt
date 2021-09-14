package com.mredrock.cyxbs.mine.page.mine.ui.activity


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHomepageBinding
import com.mredrock.cyxbs.mine.page.mine.viewmodel.MineViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.mine.page.mine.adapter.MineAdapter
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.IdentityFragment
import kotlinx.android.synthetic.main.mine_activity_homepage.*


class MineActivity : BaseViewModelActivity<MineViewModel>() {


    private lateinit var dataBinding:MineActivityHomepageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = MineActivityHomepageBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        initView()
    }





    fun initView(){

        val dynamicFragment =
            ARouter.getInstance().build("/identity/mine/entry").navigation() as Fragment
        val identityFragment = IdentityFragment()

        val list = arrayListOf<Fragment>(dynamicFragment,identityFragment)
        Log.e("wxtag","(MineActivity.kt:37)我是报错的上一行->>${ dynamicFragment  } ")
        dataBinding.btMineBack.setOnClickListener {

            val s=1/0
        }
     // .adapter = MineAdapter(this,list)
        Log.e("wxtag","(MineActivity.kt:37)->>${ vp2_mine.adapter } ")


    }

}