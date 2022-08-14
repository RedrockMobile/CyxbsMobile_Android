package com.mredrock.cyxbs.sport.service

import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.api.sport.ISportService
import com.mredrock.cyxbs.api.sport.SPORT_SERVICE
import com.mredrock.cyxbs.sport.ui.fragment.DiscoverSportFeedFragment

/**
 * @author : why
 * @time   : 2022/8/12 17:06
 * @bless  : God bless my code
 */
@Route(path = SPORT_SERVICE, name = SPORT_SERVICE)
class SportService : ISportService {

    override fun getSportFeed(): Fragment = DiscoverSportFeedFragment()

    override fun init(context: Context?) {
    }
}