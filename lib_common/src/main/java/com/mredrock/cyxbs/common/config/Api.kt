package com.mredrock.cyxbs.common.config
import com.mredrock.cyxbs.common.BuildConfig

/**
 * Created By jay68 on 2018/8/10.
 */

//6.1.0版本之后后端环境更新，baseUrl分为测试环境和线上环境
const val END_POINT_REDROCK_DEV = "https://be-dev.redrock.cqupt.edu.cn"//测试环境url
const val END_POINT_REDROCK_PROD = "https://be-prod.redrock.cqupt.edu.cn"//线上环境url
const val BASE_NORMAL_IMG_URL = "$END_POINT_REDROCK_PROD/app/Public/photo/"

const val BASE_THUMBNAIL_IMG_URL = BASE_NORMAL_IMG_URL + "thumbnail/"

const val BASE_NORMAL_BACKUP_GET = "https://be-prod.redrock.team/cloud-manager/check"

//获取baseUrl的方法
//TODO: 暂时没有测试环境，就都返回了线上环境
fun getBaseUrl() = END_POINT_REDROCK_DEV