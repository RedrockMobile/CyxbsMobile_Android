package com.mredrock.cyxbs.skin.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.skin.bean.SkinInfo
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by LinTong on 2021/9/18
 * Description: 换肤网络请求
 */
interface SkinApiService {

    @GET("/magipoke-theme/theme")
    fun getSkinInfo(): Observable<RedrockApiWrapper<List<SkinInfo.Data>>>
}
