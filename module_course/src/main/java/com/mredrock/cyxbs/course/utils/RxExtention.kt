package com.mredrock.cyxbs.course.utils

import android.widget.Toast
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.course.R
import io.reactivex.Observable

/**
 * Created by Jovines on 2020/05/24 23:08
 * description : 课表用到的rx的一些扩展方法
 */

fun <T> Observable<RedrockApiWrapper<T>>.affairFilter() = filter {
    if (it.info == "success" && it.status == 200) {
        true
    }else{
        CyxbsToast.makeText(BaseApp.context, R.string.course_network_error_failed, Toast.LENGTH_SHORT).show()
        false
    }
}

