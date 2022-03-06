package com.mredrock.cyxbs.volunteer.bean

import java.io.Serializable

data class VolunteerBase(var msg: String?,
                         var code: Int?) : Serializable {
    /**
     * msg : success
     * code : 0
     */
}