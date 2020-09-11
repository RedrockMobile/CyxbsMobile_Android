package com.mredrock.cyxbs.volunteer.bean

import java.io.Serializable

/*
{
  "code": 0,
  "data": "",
  "msg": "已经绑定了"
}
 */
data class VolunteerJudge(val code: Int, val data: String, val msg: String) : Serializable