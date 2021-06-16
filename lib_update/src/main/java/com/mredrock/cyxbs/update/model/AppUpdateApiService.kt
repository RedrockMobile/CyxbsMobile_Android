package com.mredrock.cyxbs.update.model

import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Create By Hosigus at 2019/5/11
 */
interface AppUpdateApiService {
    @GET("https://app.redrock.team/cyxbsAppUpdate.json")
    fun getUpdateInfo(): Observable<UpdateInfo>
}