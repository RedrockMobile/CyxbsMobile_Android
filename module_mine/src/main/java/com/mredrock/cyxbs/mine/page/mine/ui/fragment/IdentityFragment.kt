package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.page.mine.viewmodel.IdentityViewModel

class IdentityFragment: BaseViewModelFragment<IdentityViewModel>() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?=inflater.inflate(R.layout.mine_fragment_identify, container, false)
}