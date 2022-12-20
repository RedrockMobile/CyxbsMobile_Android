package com.mredrock.cyxbs.api.mine

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

interface IGetDaySignClassService : IProvider {

    fun getDaySignClassService(): Class<out Activity>

}