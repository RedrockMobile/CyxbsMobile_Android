package com.mredrock.cyxbs.mine.page.security.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.databinding.MineActivityFindPasswordIdsBinding
import com.mredrock.cyxbs.mine.page.security.fragment.LoginIdsFragment

class FindPasswordByIdsActivity :
    BaseBindActivity<MineActivityFindPasswordIdsBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        replaceFragment(R.id.mine_fcv_find_password_ids_container) { LoginIdsFragment() }
        binding.mineBtnFindPasswordIdsTopBack.setOnSingleClickListener {
            finish()
        }
    }

    fun replace(func: FragmentTransaction.() -> Fragment) {
        replaceFragment(R.id.mine_fcv_find_password_ids_container, func)
    }
}