package com.mredrock.cyxbs.mine.page.sign

/**
 * Created by roger on 2019/11/19
 */
class WeekGenerator {
    companion object {
        @JvmStatic
        val COLOR_GREY = 2
        @JvmStatic
        val COLOR_BLUE = 1
        @JvmStatic
        val COLOR_DEFAULT = 0
        @JvmStatic
        val WEEK_HAS_SIGN = 1
        @JvmStatic
        val WEEK_UN_SIGN = 0

        fun getDividerArr(weekDays: Array<Int>): Array<Int> {
            val dividerArr = Array(6) { COLOR_DEFAULT }
            //得到当前weekDay，星期一为0,星期天为6

            for (i in 0..5) {
                if (weekDays[i] == WEEK_HAS_SIGN && weekDays[i + 1] == WEEK_HAS_SIGN ||
                        (weekDays[i] == WEEK_HAS_SIGN && isToDayOrSunDay(i + 1)
                                )) {
                    dividerArr[i] = COLOR_BLUE
                } else if (weekDays[i] == WEEK_HAS_SIGN && weekDays[i + 1] == WEEK_UN_SIGN && !isToDayOrSunDay(i + 1)) {
                    dividerArr[i] = COLOR_GREY
                } else {
                    dividerArr[i] = COLOR_DEFAULT
                }
            }
            return dividerArr
        }

        /***
         * @param index : 表示需要判断的星期, 从0到6依次表示星期一到星期日
         * 判断是否为今天或者星期天
         */
        fun isToDayOrSunDay(index: Int): Boolean {
//            val toDay = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
            //测试用，发布时需要更改
            val toDay = 5
            return (toDay == index) || (index == 6)
        }

        fun isToDay(index: Int): Boolean {
            //测试用，发布时需要更改
            val toDay = 5
            return (toDay == index)
        }

        fun getToDay(): Int {
            //测试用，发布时需要更改
            val toDay = 5
            return toDay
        }
    }


}