package com.mredrock.cyxbs.mine.page.sign

/**
 * Created by roger on 2019/11/19
 */
class WeekGenerator(val weekSignStateArr: Array<Int>) {
    companion object {
        @JvmStatic
        val WEEK_HAS_SIGN = 1
        @JvmStatic
        val WEEK_UN_SIGN = 0
    }

    fun getDividerColorArr(): Array<ColorState> {
        val dividerColorArr = Array(6) { ColorState.COLOR_GREY }
        //得到当前weekDay，星期一为0,星期天为6
        for (i in 0..5) {
            /**
             * divider为蓝色的情况：左右原点均为1，左原点为1且右原点为今天，或左原点为今天且左原点为1
             * divider为暗蓝色的情况：左原点为1且右原点为0
             * divider为灰色的情况：其他
             */
            if (weekSignStateArr[i] == WEEK_HAS_SIGN && weekSignStateArr[i + 1] == WEEK_HAS_SIGN || (weekSignStateArr[i] == WEEK_HAS_SIGN && getToDay() == (i + 1)) || (getToDay() == i) && weekSignStateArr[i + 1] == WEEK_HAS_SIGN) {
                dividerColorArr[i] = ColorState.COLOR_BLUE
            } else if (weekSignStateArr[i] == WEEK_HAS_SIGN && weekSignStateArr[i + 1] == WEEK_UN_SIGN && (i + 1) != getToDay()) {
                dividerColorArr[i] = ColorState.COLOR_DARK_BLUE
            } else {
                dividerColorArr[i] = ColorState.COLOR_GREY
            }
        }
        return dividerColorArr
    }

    fun getWeekImageStateArr(): Array<ImageState> {
        val weekImageStateArr = Array(7) { ImageState.IMAGE_GREY }
        for (i in 0..6) {
            if (weekSignStateArr[i] == WEEK_HAS_SIGN) {
                weekImageStateArr[i] = ImageState.IMAGE_BLUE
            } else if (weekSignStateArr[i] == WEEK_UN_SIGN && (i == getToDay())) {
                weekImageStateArr[i] = ImageState.IMAGE_DIAMOND
            } else {
                weekImageStateArr[i] = ImageState.IMAGE_GREY
            }
        }
        return weekImageStateArr
    }

    fun getToDay(): Int {
        //测试用，发布时需要更改
        val toDay = 6
        return toDay
    }




}