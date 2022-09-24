package com.mredrock.cyxbs.noclass.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.bean
 * @ClassName:      NoclassGroupId
 * @Author:         Yan
 * @CreateDate:     2022年08月27日 20:50:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
data class NoclassGroupId(
    @SerializedName("group_id")
    val id : Int
) : Serializable