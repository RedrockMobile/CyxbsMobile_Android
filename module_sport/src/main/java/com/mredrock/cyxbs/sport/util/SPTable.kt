package com.mredrock.cyxbs.sport.util

import androidx.core.content.edit
import com.mredrock.cyxbs.lib.utils.extensions.appContext
import com.mredrock.cyxbs.lib.utils.extensions.getSp

/**
 * @author : why
 * @time   : 2022/8/13 15:01
 * @bless  : God bless my code
 */
var sSpIdsIsBind: Boolean
    get() = appContext.getSp("sp_sport").getBoolean("sp_ids_is_bind", false)
    set(value) {
        appContext.getSp("sp_sport").edit { putBoolean("sp_ids_is_bind", value) }
    }