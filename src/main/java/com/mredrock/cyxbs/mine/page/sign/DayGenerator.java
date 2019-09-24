package com.mredrock.cyxbs.mine.page.sign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2018/8/19.\
 * 修改 by roger on 2019/9/24:优化了逻辑，修复第八天无法签到的bug
 * 需求：
 * 今日签到尽量保持在中心
 */
public class DayGenerator {

    private int serialDays;
    private boolean isSign;

    private final int[] moneyArray = {10, 10, 20, 10, 30, 10, 40};

    public DayGenerator(int serialDays, boolean isSign) throws Exception {
        this.serialDays = serialDays;
        this.isSign = isSign;
        if ((serialDays == 0 && this.isSign)) {
            throw new Exception("你这数据怕是有问题哦");
        }
    }

    public List<Data> getDatas() {
        int today;
        if (isSign) {
            today = serialDays;
        } else {
            today = serialDays + 1;
        }

        List<Data> list = new ArrayList<>();

        int left, right;
        if (today >= 3) {
            left = today - 2;
            right = today + 2;
        } else {
            left = 1;
            right = 5;
        }

        //生成左边的Data
        for (int i = left; i < today; i++) {
            //比如说是第9天，那么对应数组moneyArray[1]
            int money = moneyArray[(i - 1) % 7];
            list.add(new Data(i, money, true));
        }

        //生成当天Data
        list.add(new Data(today, moneyArray[today % 7 - 1], isSign));

        //生成右边的Data
        for (int i = today + 1; i <= right; i++) {
            int money = moneyArray[(i - 1) % 7];
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