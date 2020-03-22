package com.mredrock.cyxbs.discover.grades.bean.analyze

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by roger on 2020/3/21
 */
data class GPAData (
        @SerializedName("term_grades")
        val termGrade : List<TermGrade>,
        @SerializedName("credits")
        val allCredit: List<Credit>,
        @SerializedName("a_credit")
        val aCredit: String,
        @SerializedName("b_credit")
        val bCredit: String
) : Serializable