package com.mredrock.cyxbs.freshman.interfaces.network

import com.mredrock.cyxbs.freshman.bean.EnrollmentProcessBean
import com.mredrock.cyxbs.freshman.config.API_ENROLLMENT_PROCESS
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Create by yuanbing
 * on 2019/8/3
 */
interface EnrollmentProcessService {
    @GET(API_ENROLLMENT_PROCESS)
    fun requestEnrollmentProcess(): Observable<EnrollmentProcessBean>
}