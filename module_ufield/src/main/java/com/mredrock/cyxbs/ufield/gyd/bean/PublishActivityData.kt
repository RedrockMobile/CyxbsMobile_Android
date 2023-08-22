package com.mredrock.cyxbs.ufield.gyd.bean

/**
 * description ： TODO:类的作用
 * author : 苟云东
 * email : 2191288460@qq.com
 * date : 2023/8/21 17:02
 */
data class PublishActivityData(
    val activity_title: String,
    val activity_type:String,
    val activity_start_at: Int,
    val activity_end_at:Int,
    val activity_place :String,
    val activity_registration_type:String,
    val activity_organizer:String,
    val creator_phone:String,
    val activity_detail:String
)
