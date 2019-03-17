package com.mredrock.cyxbs.volunteer.bean

import java.io.Serializable

data class VolunteerLogin(var msg : String?,
                          var code : String?): Serializable {
    /**
     * msg : success
     * code : 0
     */
}