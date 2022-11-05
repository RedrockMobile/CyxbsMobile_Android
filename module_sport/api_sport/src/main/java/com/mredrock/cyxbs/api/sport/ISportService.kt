package com.mredrock.cyxbs.api.sport

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author : why
 * @time   : 2022/8/12 17:00
 * @bless  : God bless my code
 */
interface ISportService: IProvider {
    fun getSportFeed(): Fragment
}