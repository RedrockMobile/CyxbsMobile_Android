package com.mredrock.cyxbs.mine.page.mine.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHomepageBinding
import com.mredrock.cyxbs.mine.databinding.MineActivityIdentityBinding
import com.mredrock.cyxbs.mine.page.mine.adapter.MineAdapter
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.ApproveStatusFragment
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.IdentityFragment
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.PersonalityStatusFragment
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.mine_activity_identity.*

class IdentityActivity : BaseViewModelActivity<IdentityViewModel>() {

    lateinit var dataBinding: MineActivityIdentityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = MineActivityIdentityBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        initView()
    }



    fun initView(){
        val tabNames= listOf<String>("认证身份","个性身份")
        val approveStatusFragment = ApproveStatusFragment()
        val personalityStatusFragment = PersonalityStatusFragment()
        val list = arrayListOf<Fragment>(approveStatusFragment,personalityStatusFragment)
        dataBinding.vpStatus.adapter= MineAdapter(this,list)
        TabLayoutMediator(dataBinding.mineTabStatus, dataBinding.vpStatus,true) { tab, position ->
            tab.text = tabNames[position]
        }.attach()




    }
}