package com.mredrock.cyxbs.qa.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *@Date 2020-11-19
 *@Time 19:28
 *@author SpreadWater
 *@description
 */
class CircleSquare (@SerializedName("circle_name")
                    var circle_name:String="",
                    @SerializedName("circle_person_number")
                    var cirecle_person_number:Int=0,
                    @SerializedName("circle_square_descriprion")
                    var circle_square_descriprion:String="",
                    @SerializedName("circle_square_image")
                    var circle_square_image:String=""):Serializable