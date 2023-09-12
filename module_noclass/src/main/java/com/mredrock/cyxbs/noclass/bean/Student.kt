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
/**
 * 要注意以下两点
 * 1：在计算hashmap值的时候会跳过类型为可空并且为空的，但是如果类型是不可空的，由于网络请求没有填装进去，所以为null，此时就会报空指针异常
 * 解决办法：让类型变成可空的，这样hashmap就会在值已空的情况下跳过了
 * 2：textView的text是可以为null的，所以下面你的major也不必担心
 * 3: alternate是可替代的映射，也就是前面的找不到就会往后找名称相同的
 */

data class Student(
  @SerializedName("classnum")
    val classNum: String?,
  @SerializedName("gender")
    val gender: String?,
  @SerializedName("grade")
    val grade: String?,
  @SerializedName("major")
    val major: String?,
  @SerializedName("name",alternate = ["stu_name"])
  val name: String,
  @SerializedName("stunum",alternate = ["stu_num"])
  override val id: String,
  @SerializedName("depart")
  val depart : String?,   //depart是学院
  ) : Serializable,NoClassItem
