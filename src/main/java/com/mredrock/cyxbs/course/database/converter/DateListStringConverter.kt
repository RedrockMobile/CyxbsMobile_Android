package com.mredrock.cyxbs.course.database.converter

import androidx.room.TypeConverter
import com.mredrock.cyxbs.course.network.Affair

/**
 * Created by anriku on 2018/8/17.
 */

class DateListStringConverter {

    @TypeConverter
    fun dateListToString(dateList: List<Affair.Date>?): String? = StringBuffer().let { returnString ->
        dateList?.let { affairDates ->
            for (date in affairDates) {
                returnString.append("$date*")
            }
        }
        returnString.toString()
    }

    @TypeConverter
    fun stringToDateList(string: String?): List<Affair.Date>? = string?.let { getString ->
        getString.trim().trim('*')
        val datesString = getString.split("*")
        val dates = ArrayList<Affair.Date>()
        for (dateString in datesString) {
            val dateStrings = dateString.trim().trim('-').split("-")
            when {
                dateStrings.size == 2 -> {
                    dates.add(Affair.Date(dateStrings[0].toInt(), dateStrings[1].toInt()))
                }
                dateStrings.size > 2 -> {
                    val weeks = ArrayList<Int>()
                    for (i in 2..(dateStrings.size - 1)) {
                        weeks.add(dateStrings[i].toInt())
                    }
                    dates.add(Affair.Date(dateStrings[0].toInt(), dateStrings[1].toInt(), weeks))
                }
            }
        }
        dates
    }

}