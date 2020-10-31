package com.mredrock.cyxbs.mine.page.security.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivitySetPasswordProtectBinding
import com.mredrock.cyxbs.mine.page.security.viewmodel.SetPasswordProtectViewModel
import kotlinx.android.synthetic.main.mine_activity_set_password_protect.*

/**
 * Author: RayleighZ
 * Time: 2020-10-29 15:06
 * describe: 设置密保的活动
 */
class SetPasswordProtectActivity : BaseViewModelActivity<SetPasswordProtectViewModel>() {

    override val isFragmentActivity = false
    override val viewModelClass =  SetPasswordProtectViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //使用dataBinding
        val binding = DataBindingUtil.inflate<MineActivitySetPasswordProtectBinding>(LayoutInflater.from(this),R.layout.mine_activity_set_password_protect
        ,null , false)
        //设置viewModel
        binding.viewModel = viewModel
        setContentView(binding.root)

        viewModel.getSecurityQuestions {  }//不需要回调的获取所有的密保问题
        mine_bt_security_confirm.setOnClickListener {
            //TODO: 除了toast，是否需要额外操作？
            viewModel.setSecurityQA()
        }
    }
}