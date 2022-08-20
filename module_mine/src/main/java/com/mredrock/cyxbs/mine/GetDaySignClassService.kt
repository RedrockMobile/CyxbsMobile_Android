package com.mredrock.cyxbs.mine

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.mine.page.sign.DailySignActivity
import com.redrock.api_mine.MINE_SERVICE
import com.redrock.api_mine.api.IGetDaySignClassService

@Route(path = MINE_SERVICE, name = MINE_SERVICE)
class GetDaySignClassService : IGetDaySignClassService {
    private var mContext: Context? = null

    override fun getDaySignClassService(): Class<Any> {
        return DailySignActivity::class.java as Class<Any>
    }

    override fun init(context: Context?) {
        mContext = context
    }
}