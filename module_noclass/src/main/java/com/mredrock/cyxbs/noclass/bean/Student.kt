package com.mredrock.cyxbs.noclass.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.bean
 * @ClassName:      Student
 * @Author:         Yan
 * @CreateDate:     2022年09月02日 00:20:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */


data class Student(
  @SerializedName("classnum")
    val classNum: String,
  @SerializedName("gender")
    val gender: String,
  @SerializedName("grade")
    val grade: String,
  @SerializedName("major")
    val major: String,
  @SerializedName("name")
  val name: String,
  @SerializedName("stunum")
  override val id: String,
  ) : Serializable,NoClassItem
