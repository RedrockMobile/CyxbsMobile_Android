package com.redrock.api_mine.api

import com.alibaba.android.arouter.facade.template.IProvider

interface IGetDaySignClassService : IProvider {

    fun getDaySignClassService(): Class<Any>

}