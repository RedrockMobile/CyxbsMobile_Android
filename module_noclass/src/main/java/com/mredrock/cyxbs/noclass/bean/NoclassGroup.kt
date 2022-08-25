package com.mredrock.cyxbs.noclass.bean

import com.google.gson.annotations.SerializedName

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.bean
 * @ClassName:      Group
 * @Author:         Yan
 * @CreateDate:     2022年08月18日 17:56:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    每个分组的bean类
 */

data class NoclassGroup(
    @SerializedName("id")
    val id: String,
    @SerializedName("is_top")
    val isTop: Boolean,
    @SerializedName("members")
    val members: List<Member>,
    @SerializedName("name")
    val name: String
) : java.io.Serializable{
    data class Member(
        @SerializedName("stu_name")
        val stuName: String,
        @SerializedName("stu_num")
        val stuNum: String
    ) : java.io.Serializable
}