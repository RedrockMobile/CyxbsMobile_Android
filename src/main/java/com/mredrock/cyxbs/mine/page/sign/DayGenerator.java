package com.mredrock.cyxbs.mine.page.sign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2018/8/19.
 * 需求：
 * 今日签到尽量保持在中心
 * 逻辑：
 * 以连续签到天数为中心，左边加一个，右边加一个，直到加满5天
 */
public class DayGenerator {

    private int serialDays;
    private boolean isSign;

    private final int[] moneyArray = {0, 10, 10, 20, 10, 30, 10, 40};

    public DayGenerator(int serialDays, boolean isSign) throws Exception {
        this.serialDays = serialDays;
        this.isSign = isSign;
        if ((serialDays == 0 && this.isSign) || (serialDays == 7 && !this.isSign)) {
            throw new Exception("你这数据怕是有问题哦");
        }
    }

    public List<Data> getDatas() {
        //如果今天没有签到，把中心向右移动一格
        int today = 0;
        if (isSign) {
            today = serialDays;
        } else {
            today = serialDays + 1;
        }

        List<Data> list = new ArrayList<>();

        int left = today;
        int right = today;

        //计算左右两边的天数
        while (true) {
            if (left > 1) {
                left--;
            }
            if (right - left >= 4) break;
            if (right < 7) {
                right++;
            }
            if (right - left >= 4) break;
        }

        //生成左边的Data
        for (int i = left; i < today; i++) {
            int money = moneyArray[i];
            list.add(new Data(i, money, true));
        }

        //生成当天Data
        list.add(new Data(today, moneyArray[today], isSign));

        //生成右边的Data
        for (int i = today + 1; i <= right; i++) {
            int money = moneyArray[i];
            list.add(new Data(i, money, false));
        }
        return list;
    }

    public class Data {
        int day;//天数
        int money;//积分
        boolean ischecked;//是否实心

        public Data(int day, int money, boolean ischecked) {
            //这里直接+1对齐星期了，不知道有没有啥隐患
            this.day = day;
            this.money = money;
            this.ischecked = ischecked;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "day=" + day +
                    ", money=" + money +
                    ", ischecked=" + ischecked +
                    '}' +
                    "\n";
        }
    }
}