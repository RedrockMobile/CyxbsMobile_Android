package com.mredrock.cyxbs.center

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.CENTER_ENTRY
import com.mredrock.cyxbs.lib.base.ui.BaseFragment

/**
 * Create by bangbangp on 2023/3/26 18:55
 * Email:1678921845@qq.com
 * Description:
 */
@Route(path = CENTER_ENTRY)
class CenterFragment:BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.center_fragment_center, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}