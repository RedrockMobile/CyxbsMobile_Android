package com.mredrock.cyxbs.mine.util.widget

import com.mredrock.cyxbs.common.utils.SchoolCalendar

/**
 * Created by roger on 2019/11/19
 */
class WeekGenerator(var weekSignStateArr: List<Int> = mutableListOf()) {
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
             * 1.divider为蓝色的情况：左右原点均为1，左原点为1且右原点为今天，或左原点为今天且左原点为1
             * 2.divider为淡蓝色的情况：排除1的情况后，左原点为1且右原点为0
             * 3.divider为灰色的情况：其他
             */
            if (weekSignStateArr[i] == WEEK_HAS_SIGN && weekSignStateArr[i + 1] == WEEK_HAS_SIGN || (weekSignStateArr[i] == WEEK_HAS_SIGN && getToday() == (i + 1)) || (getToday() == i) && weekSignStateArr[i] == WEEK_HAS_SIGN) {
                dividerColorArr[i] = ColorState.COLOR_BLUE
            } else if (weekSignStateArr[i] == WEEK_HAS_SIGN && weekSignStateArr[i + 1] == WEEK_UN_SIGN && (i + 1) != getToday()) {
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
            } else if (weekSignStateArr[i] == WEEK_UN_SIGN && (i == getToday())) {
                weekImageStateArr[i] = ImageState.IMAGE_DIAMOND
            } else {
                weekImageStateArr[i] = ImageState.IMAGE_GREY
            }
        }
        return weekImageStateArr
    }

    //获取今天的签到积分数：通过week_info计算出本周内已经连续签到多少天，然后通过这个参数返回给气泡TextView的text值
    /**
     * 一周内连续签到积分制度：
     *首次签到积分+10
     *第二次签到积分+15
     *第三次签到积分+20
     *第四次签到积分+25
     *第五次签到积分+30（封顶）
     *此后连续签到均+30
     *一周内最多连续签到7天
     */
    fun getTodayScore(): Int {
        var count = 0;
        for (i in (getToday() - 1) downTo 0) {
            if (weekSignStateArr[i] == WEEK_HAS_SIGN) {
                count++
            } else {
                break
            }
        }
        val arrayOfIntegral = arrayOf(10, 15, 20, 25, 30, 30, 30)
        return arrayOfIntegral[count]
    }

    fun getToday(): Int {
        //由于这里的表示的星期一是0，星期天是6，所以需要对齐
        val toDay = SchoolCalendar().dayOfWeek - 1;
        return toDay
    }


}