package com.mredrock.cyxbs.login.network

import com.mredrock.cyxbs.login.bean.IdsBean
import com.mredrock.cyxbs.login.bean.IdsStatus
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
interface BindIdsApiService {
    //绑定ids
    @POST("/magipoke/ids/bind")
    fun bindIds(@Body idsBean: IdsBean) : Observable<IdsStatus>
}