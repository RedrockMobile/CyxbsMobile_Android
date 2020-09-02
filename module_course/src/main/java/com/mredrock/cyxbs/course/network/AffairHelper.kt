package com.mredrock.cyxbs.course.network

import com.google.gson.Gson
import java.util.*

/**
 * Created by anriku on 2018/9/12.
 */

object AffairHelper {

    private val mRandom = Random()
    private val mGson = Gson()

    /**
     * This function is used to generate a affair id, affair id is composed of a timestamp + 4 figures.
     */
    fun generateAffairId() = "${System.currentTimeMillis()}${mRandom.nextInt(10000 - 1000) + 1000}"

    /**
     * There use [Gson] to generate a String conveniently.
     */
    fun generateAffairDateString(classAndDays: List<Pair<Int, Int>>, weeks: List<Int>): String {
        val dates = generateAffairDate(classAndDays, weeks)
        return mGson.toJson(dates)
    }

    /**
     * This function is used to generate a affair [Affair.Date] list.
     */
    fun generateAffairDate(classAndDays: List<Pair<Int, Int>>, weeks: List<Int>): List<Affair.Date> {
        val dates = mutableListOf<Affair.Date>()
        for (classAndDay in classAndDays) {
            dates.add(Affair.Date(classAndDay.first, classAndDay.second, weeks))
        }
        return dates
    }
}