package com.mredrock.cyxbs.common.config
import com.mredrock.cyxbs.common.BuildConfig

/**
 * Created By jay68 on 2018/8/10.
 */

//6.1.0版本之后后端环境更新，baseUrl分为测试环境和线上环境
const val END_POINT_REDROCK_DEV = "https://be-dev.redrock.team"//测试环境url
const val END_POINT_REDROCK_PROD = "https://be-prod.redrock.team"//线上环境url
const val END_POINT_REDROCK = "https://be-prod.redrock.team"

const val END_POINT_REDROCK_VERSION_TWO = "http://be-prod.redrock.team"
const val BASE_NORMAL_IMG_URL = "$END_POINT_REDROCK/app/Public/photo/"

const val BASE_THUMBNAIL_IMG_URL = BASE_NORMAL_IMG_URL + "thumbnail/"

//获取baseUrl的方法
fun getBaseUrl() = if (BuildConfig.DEBUG) END_POINT_REDROCK_PROD else END_POINT_REDROCK_PROD