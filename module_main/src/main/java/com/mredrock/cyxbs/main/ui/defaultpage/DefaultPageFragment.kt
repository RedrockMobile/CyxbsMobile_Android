package com.mredrock.cyxbs.main.ui.defaultpage

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.DEFAULT_FRAGMENT_PAGE
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.main.R

/**
 * @author : why
 * @time   : 2022/10/30 21:02
 * @bless  : God bless my code
 */
@Route(path = DEFAULT_FRAGMENT_PAGE)
class DefaultPageFragment: BaseFragment(R.layout.main_activity_default_page) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}