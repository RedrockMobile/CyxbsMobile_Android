package com.mredrock.cyxbs.mine.page.mine.ui.activity

import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
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
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityHomepageBinding
import com.mredrock.cyxbs.mine.databinding.MineActivityIdentityBinding
import com.mredrock.cyxbs.mine.page.mine.adapter.MineAdapter
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.ApproveStatusFragment
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.IdentityFragment
import com.mredrock.cyxbs.mine.page.mine.ui.fragment.PersonalityStatusFragment
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel
import com.mredrock.cyxbs.mine.util.widget.loadBitmap
import com.mredrock.cyxbs.store.utils.transformer.ScaleInTransformer
import kotlinx.android.synthetic.main.mine_activity_identity.*

class IdentityActivity : BaseViewModelActivity<IdentityViewModel>() {

    lateinit var dataBinding: MineActivityIdentityBinding
    val redid by lazy {
        intent.getStringExtra("redid")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = MineActivityIdentityBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        initView()
        initData()
        initLisener()
    }



    fun initView(){
        val tabNames= listOf<String>("认证身份","个性身份")
        val approveStatusFragment = ApproveStatusFragment(redid)
        val personalityStatusFragment = PersonalityStatusFragment(redid)
        val list = arrayListOf<Fragment>(approveStatusFragment,personalityStatusFragment)
        dataBinding.vpStatus.adapter= MineAdapter(this,list)


        dataBinding.vpStatus.
        setPageTransformer(ScaleInTransformer())
        dataBinding.vpStatus.offscreenPageLimit = 2
        TabLayoutMediator(dataBinding.mineTabStatus, dataBinding.vpStatus,true) { tab, position ->
            tab.text = tabNames[position]
        }.attach()

    }

    fun initData(){
        viewModel.getShowIdentify(redid)
        dataBinding.tvItemIdentityName.setTypeface(Typeface.createFromAsset(assets,"YouSheBiaoTiHei-2.ttf"))
        dataBinding.tvItemIdentity.setTypeface(Typeface.createFromAsset(assets,"YouSheBiaoTiHei-2.ttf"))
        dataBinding.tvItemIdentityTime.setTypeface(Typeface.createFromAsset(assets,"YouSheBiaoTiHei-2.ttf"))

    }
    fun initLisener(){
        viewModel.showStatu.observeForever {
            dataBinding.tvItemIdentityName.text=it.data.form
            dataBinding.tvItemIdentity.text = it.data.position
            dataBinding.tvItemIdentityTime.text = it.data.date
            loadBitmap(it.data.background){
                dataBinding.clContentView.background= BitmapDrawable(context.resources,it)
            }
            Log.i("身份设置","initLisener"+it.data.position)
            viewModel.isFinsh.value=true
            viewModel.isUpdata=false
        }
        dataBinding.mineImageview.setOnClickListener {
            onBackPressed()
        }
        dataBinding.tvIdentity.setOnClickListener {
            onBackPressed()
        }
    }

}