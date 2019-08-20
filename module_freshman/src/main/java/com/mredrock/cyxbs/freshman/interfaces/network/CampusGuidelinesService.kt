package com.mredrock.cyxbs.freshman.interfaces.network

import com.mredrock.cyxbs.freshman.bean.SubjectDataBean
import com.mredrock.cyxbs.freshman.bean.DormitoryAndCanteenBean
import com.mredrock.cyxbs.freshman.bean.ExpressBean
import com.mredrock.cyxbs.freshman.bean.SexRatioBean
import com.mredrock.cyxbs.freshman.config.API_SUBJECT_DATA
import com.mredrock.cyxbs.freshman.config.API_DORMITORY_AND_CANTEEN
import com.mredrock.cyxbs.freshman.config.API_EXPRESS
import com.mredrock.cyxbs.freshman.config.API_SEX_RATO
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Create by yuanbing
 * on 2019/8/7
 */
interface DormitoryAndCanteenService {
    @GET(API_DORMITORY_AND_CANTEEN)
    fun requestDormitoryAndCanteen(): Observable<DormitoryAndCanteenBean>
}

interface SubjectDataService {
    @GET(API_SUBJECT_DATA)
    fun requestSubjectData(): Observable<SubjectDataBean>
}

interface ExpressService {
    @GET(API_EXPRESS)
    fun requestExpress(): Observable<ExpressBean>
}

interface SexRatioService {
    @GET(API_SEX_RATO)
    fun requestSexRatio(): Observable<SexRatioBean>
}