package com.mredrock.cyxbs.update.model

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

/**
 * Create By Hosigus at 2019/5/11
 */
interface AppUpdateApiService {
    @GET("https://app.redrock.team/cyxbsAppUpdate.json")
    fun getUpdateInfo(): Single<UpdateInfo>

    //在官网查询更新失败时查询github的release更新
    @GET("https://api.github.com/repos/RedrockMobile/CyxbsMobile_Android/releases/latest")
    fun getUpdateInfoByGithub():Single<GithubUpdateInfo>
}