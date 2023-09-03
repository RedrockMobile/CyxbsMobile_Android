package com.mredrock.cyxbs.noclass.bean

/**
 * 由于老接口的字段名称和新的不同，所以为了适配老接口
 */
data class Member(
    val name : String,
    override val id : String
) : NoClassItem
