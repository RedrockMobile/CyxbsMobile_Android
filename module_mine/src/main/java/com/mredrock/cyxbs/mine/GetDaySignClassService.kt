package com.mredrock.cyxbs.mine

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.mine.page.sign.DailySignActivity
import com.mredrock.cyxbs.api.mine.MINE_SERVICE
import com.mredrock.cyxbs.api.mine.IGetDaySignClassService

@Route(path = MINE_SERVICE, name = MINE_SERVICE)
class GetDaySignClassService : IGetDaySignClassService {
    private var mContext: Context? = null

    override fun getDaySignClassService(): Class<out Activity> {
        return DailySignActivity::class.java
    }

    override fun init(context: Context?) {
        mContext = context
    }
}