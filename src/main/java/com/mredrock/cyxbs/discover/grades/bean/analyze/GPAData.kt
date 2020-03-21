package com.mredrock.cyxbs.discover.grades.bean.analyze

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/3/21
 */
data class GPAData (
        @SerializedName("teamGrade")
        val termGrade : List<TermGrade>,
        @SerializedName("allCredit")
        val allCredit: List<Credit>
) : Serializable