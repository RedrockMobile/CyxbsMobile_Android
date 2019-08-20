package com.mredrock.cyxbs.freshman.interfaces.network

import com.mredrock.cyxbs.freshman.bean.EnrollmentRequirementsBean
import com.mredrock.cyxbs.freshman.config.API_ENROLLMENT_REQUIREMENTS
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Create by yuanbing
 * on 2019/8/8
 */
interface EnrollmentRequirementsService {
    @GET(API_ENROLLMENT_REQUIREMENTS)
    fun requestEnrollmentRequirements(): Observable<EnrollmentRequirementsBean>
}