package com.mredrock.cyxbs.mine.page.security.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivitySetPasswordProtectBinding
import com.mredrock.cyxbs.mine.page.security.viewmodel.SetPasswordProtectViewModel

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
        val binding =  DataBindingUtil.inflate<MineActivitySetPasswordProtectBinding>(LayoutInflater.from(this),
                R.layout.mine_activity_set_password_protect, null, false)
        binding.viewModel = viewModel
        setContentView(binding.root)

        viewModel.getSecurityQuestions {  }
    }
}