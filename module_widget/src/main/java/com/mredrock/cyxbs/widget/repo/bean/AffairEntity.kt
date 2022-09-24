package com.mredrock.cyxbs.widget.repo.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mredrock.cyxbs.api.affair.IAffairService
import java.io.Serializable

/**
 * author : Watermelon02
 */
@Entity
data class AffairEntity(
    val stuNum: String,
    @PrimaryKey
    val id: Int,
    val time: Int,
    val title: String,
    val content: String,
    val week: Int,
    val beginLesson: Int,
    val day: Int,
    val period: Int,
) : Serializable {
    companion object {
        //将api模块的Affair转化为widget模块的Affair
        fun convert(apiAffairs: List<IAffairService.Affair>): ArrayList<AffairEntity> {
            val affairs = arrayListOf<AffairEntity>()
            for (apiAffair in apiAffairs) {
                val affair = AffairEntity(
                    stuNum = apiAffair.stuNum,
                    id = apiAffair.id,
                    time = apiAffair.time,
                    title = apiAffair.title,
                    content = apiAffair.content,
                    week = apiAffair.week,
                    beginLesson = apiAffair.beginLesson,
                    day = apiAffair.day,
                    period = apiAffair.period
                )
                affairs.add(affair)
            }
            return affairs
        }
    }
}