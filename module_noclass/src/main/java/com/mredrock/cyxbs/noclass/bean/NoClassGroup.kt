package com.mredrock.cyxbs.noclass.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

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

data class NoClassGroup(
    @SerializedName("id")
    override val id: String,
    @SerializedName("is_top")
    val isTop: Boolean,
    @SerializedName("members")
    override var members: List<Student>,
    @SerializedName("name")
    val name: String,

    var isOpen : Boolean = false
) : Serializable, Group