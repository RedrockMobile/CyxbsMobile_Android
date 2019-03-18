package com.mredrock.cyxbs.common.bean

import java.io.Serializable

/**
 * Created by zia on 2018/10/9.
 */
class WidgetCourse: Serializable {

    /**
     * status : 200
     * success : true
     * version : 18.9.8
     * term : 2018-2019学年第1学期
     * stuNum : 2016211541
     * data : [{"hash_day":0,"hash_lesson":1,"begin_lesson":3,"day":"星期一","lesson":"三四节","course":"飞盘中级","course_num":"A1090030","teacher":"王燕妮","classroom":"待定","rawWeek":"1-15周单周","weekModel":"single","weekBegin":1,"weekEnd":15,"week":[1,3,5,7,9,11,13,15],"type":"必修","period":2},{"hash_day":0,"hash_lesson":2,"begin_lesson":5,"day":"星期一","lesson":"五六节","course":"嵌入式系统设计A","course_num":"A2040080","teacher":"龙林波","classroom":"3104","rawWeek":"2-13周,16周","weekModel":"all","weekBegin":2,"weekEnd":16,"week":[2,3,4,5,6,7,8,9,10,11,12,13,16],"type":"选修","period":2},{"hash_day":1,"hash_lesson":0,"begin_lesson":1,"day":"星期二","lesson":"一二节","course":"计算机图形学B","course_num":"A2040291","teacher":"魏秉铎","classroom":"3101","rawWeek":"3-13周,16-17周","weekModel":"all","weekBegin":3,"weekEnd":17,"week":[3,4,5,6,7,8,9,10,11,12,13,16,17],"type":"选修","period":2},{"hash_day":1,"hash_lesson":0,"begin_lesson":1,"day":"星期二","lesson":"一二节","course":"电装实习","course_num":"A2080010","teacher":"罗萍","classroom":"电力系统仿真实验室(综合实验楼A606/A607)","rawWeek":"1周","weekModel":"all","weekBegin":1,"weekEnd":1,"week":[1],"type":"必修","period":4},{"hash_day":1,"hash_lesson":2,"begin_lesson":5,"day":"星期二","lesson":"五六节","course":"计算机网络B","course_num":"A2040050","teacher":"黄俊(信息)","classroom":"3508","rawWeek":"2-13周,16-18周","weekModel":"all","weekBegin":2,"weekEnd":18,"week":[2,3,4,5,6,7,8,9,10,11,12,13,16,17,18],"type":"必修","period":2},{"hash_day":1,"hash_lesson":2,"begin_lesson":5,"day":"星期二","lesson":"五六节","course":"课程设计（计算机网络）","course_num":"A2040300","teacher":"杜欢","classroom":"数据科学与大数据技术实验室(综合实验楼B516/B517)","rawWeek":"18周","weekModel":"all","weekBegin":18,"weekEnd":18,"week":[18],"type":"必修","period":4},{"hash_day":1,"hash_lesson":2,"begin_lesson":5,"day":"星期二","lesson":"五六节","course":"课程设计（软件综合设计开发）","course_num":"A2040450","teacher":"刘伯红","classroom":"计算机系统实验室(综合实验楼B505/B506)","rawWeek":"19周","weekModel":"all","weekBegin":19,"weekEnd":19,"week":[19],"type":"必修","period":4},{"hash_day":1,"hash_lesson":2,"begin_lesson":5,"day":"星期二","lesson":"五六节","course":"金工实习B","course_num":"A2140010","teacher":"朱小均","classroom":"先进设计技术实训室207","rawWeek":"14周","weekModel":"all","weekBegin":14,"weekEnd":14,"week":[14],"type":"必修","period":4},{"hash_day":2,"hash_lesson":0,"begin_lesson":1,"day":"星期三","lesson":"一二节","course":"电装实习","course_num":"A2080010","teacher":"王大军","classroom":"PCB实训室(实训楼301）","rawWeek":"1周","weekModel":"all","weekBegin":1,"weekEnd":1,"week":[1],"type":"必修","period":4},{"hash_day":2,"hash_lesson":1,"begin_lesson":3,"day":"星期三","lesson":"三四节","course":"WEB动态网页设计","course_num":"A2040010","teacher":"刘立","classroom":"3101","rawWeek":"2-13周,16-18周","weekModel":"all","weekBegin":2,"weekEnd":18,"week":[2,3,4,5,6,7,8,9,10,11,12,13,16,17,18],"type":"选修","period":2},{"hash_day":2,"hash_lesson":2,"begin_lesson":5,"day":"星期三","lesson":"五六节","course":"课程设计（计算机网络）","course_num":"A2040300","teacher":"杜欢","classroom":"数据科学与大数据技术实验室(综合实验楼B516/B517)","rawWeek":"18周","weekModel":"all","weekBegin":18,"weekEnd":18,"week":[18],"type":"必修","period":4},{"hash_day":2,"hash_lesson":2,"begin_lesson":5,"day":"星期三","lesson":"五六节","course":"课程设计（软件综合设计开发）","course_num":"A2040450","teacher":"刘伯红","classroom":"计算机系统实验室(综合实验楼B505/B506)","rawWeek":"19周","weekModel":"all","weekBegin":19,"weekEnd":19,"week":[19],"type":"必修","period":4},{"hash_day":2,"hash_lesson":2,"begin_lesson":5,"day":"星期三","lesson":"五六节","course":"金工实习B","course_num":"A2140010","teacher":"朱小均","classroom":"先进设计技术实训室207","rawWeek":"14周","weekModel":"all","weekBegin":14,"weekEnd":14,"week":[14],"type":"必修","period":4},{"hash_day":2,"hash_lesson":3,"begin_lesson":7,"day":"星期三","lesson":"七八节","course":"数据挖掘基础B","course_num":"A2040501","teacher":"刘群","classroom":"3207","rawWeek":"2-13周,16-17周","weekModel":"all","weekBegin":2,"weekEnd":17,"week":[2,3,4,5,6,7,8,9,10,11,12,13,16,17],"type":"选修","period":2},{"hash_day":3,"hash_lesson":0,"begin_lesson":1,"day":"星期四","lesson":"一二节","course":"计算机网络B","course_num":"A2040050","teacher":"黄俊(信息)","classroom":"3508","rawWeek":"3-13周单周,17周","weekModel":"single","weekBegin":3,"weekEnd":17,"week":[3,5,7,9,11,13,17],"type":"必修","period":2},{"hash_day":3,"hash_lesson":0,"begin_lesson":1,"day":"星期四","lesson":"一二节","course":"电装实习","course_num":"A2080010","teacher":"陈绍明","classroom":"SMT实训室(实训楼302)","rawWeek":"1周","weekModel":"all","weekBegin":1,"weekEnd":1,"week":[1],"type":"必修","period":4},{"hash_day":3,"hash_lesson":1,"begin_lesson":3,"day":"星期四","lesson":"三四节","course":"嵌入式系统设计A","course_num":"A2040080","teacher":"曾素华","classroom":"智能终端实验室(综合实验楼B509/B510)","rawWeek":"9-16周","weekModel":"all","weekBegin":9,"weekEnd":16,"week":[9,10,11,12,13,14,15,16],"type":"选修","period":2},{"hash_day":3,"hash_lesson":2,"begin_lesson":5,"day":"星期四","lesson":"五六节","course":"课程设计（计算机网络）","course_num":"A2040300","teacher":"杜欢","classroom":"数据科学与大数据技术实验室(综合实验楼B516/B517)","rawWeek":"18周","weekModel":"all","weekBegin":18,"weekEnd":18,"week":[18],"type":"必修","period":4},{"hash_day":3,"hash_lesson":2,"begin_lesson":5,"day":"星期四","lesson":"五六节","course":"课程设计（软件综合设计开发）","course_num":"A2040450","teacher":"刘伯红","classroom":"计算机系统实验室(综合实验楼B505/B506)","rawWeek":"19周","weekModel":"all","weekBegin":19,"weekEnd":19,"week":[19],"type":"必修","period":4},{"hash_day":3,"hash_lesson":2,"begin_lesson":5,"day":"星期四","lesson":"五六节","course":"金工实习B","course_num":"A2140010","teacher":"朱小均","classroom":"先进设计技术实训室207","rawWeek":"14周","weekModel":"all","weekBegin":14,"weekEnd":14,"week":[14],"type":"必修","period":4},{"hash_day":3,"hash_lesson":4,"begin_lesson":9,"day":"星期四","lesson":"九十节","course":"嵌入式系统设计A","course_num":"A2040080","teacher":"龙林波","classroom":"3104","rawWeek":"2-12周双周,16周","weekModel":"double","weekBegin":2,"weekEnd":16,"week":[2,4,6,8,10,12,16],"type":"选修","period":2},{"hash_day":4,"hash_lesson":0,"begin_lesson":1,"day":"星期五","lesson":"一二节","course":"中国近现代史纲要","course_num":"A1100040","teacher":"邓庆伟","classroom":"3301","rawWeek":"2-11周","weekModel":"all","weekBegin":2,"weekEnd":11,"week":[2,3,4,5,6,7,8,9,10,11],"type":"必修","period":2},{"hash_day":4,"hash_lesson":0,"begin_lesson":1,"day":"星期五","lesson":"一二节","course":"课程设计（计算机网络）","course_num":"A2040300","teacher":"杜欢","classroom":"数据科学与大数据技术实验室(综合实验楼B516/B517)","rawWeek":"18周","weekModel":"all","weekBegin":18,"weekEnd":18,"week":[18],"type":"必修","period":4},{"hash_day":4,"hash_lesson":0,"begin_lesson":1,"day":"星期五","lesson":"一二节","course":"课程设计（软件综合设计开发）","course_num":"A2040450","teacher":"刘伯红","classroom":"计算机系统实验室(综合实验楼B505/B506)","rawWeek":"19周","weekModel":"all","weekBegin":19,"weekEnd":19,"week":[19],"type":"必修","period":4},{"hash_day":4,"hash_lesson":0,"begin_lesson":1,"day":"星期五","lesson":"一二节","course":"电装实习","course_num":"A2080010","teacher":"林海波","classroom":"电装实训室(先进制造实训楼A303)","rawWeek":"1周","weekModel":"all","weekBegin":1,"weekEnd":1,"week":[1],"type":"必修","period":4},{"hash_day":4,"hash_lesson":2,"begin_lesson":5,"day":"星期五","lesson":"五六节","course":"形势与政策","course_num":"A1100010","teacher":"郑兴刚","classroom":"2402","rawWeek":"5-8周","weekModel":"all","weekBegin":5,"weekEnd":8,"week":[5,6,7,8],"type":"必修","period":2},{"hash_day":4,"hash_lesson":2,"begin_lesson":5,"day":"星期五","lesson":"五六节","course":"金工实习B","course_num":"A2140010","teacher":"朱小均","classroom":"先进设计技术实训室207","rawWeek":"14周","weekModel":"all","weekBegin":14,"weekEnd":14,"week":[14],"type":"必修","period":4}]
     * cachedTimestamp : 1539089073698
     * outOfDateTimestamp : 1539693873698
     * nowWeek : 5
     */

    var status: Int = 0
    var isSuccess: Boolean = false
    var version: String? = null
    var term: String? = null
    var stuNum: String? = null
    var cachedTimestamp: Long = 0
    var outOfDateTimestamp: Long = 0
    var nowWeek: Int = 0
    var data: List<DataBean>? = null

    override fun toString(): String {
        return "WidgetCourse{" +
                "status=" + status +
                ", success=" + isSuccess +
                ", version='" + version + '\''.toString() +
                ", term='" + term + '\''.toString() +
                ", stuNum='" + stuNum + '\''.toString() +
                ", cachedTimestamp=" + cachedTimestamp +
                ", outOfDateTimestamp=" + outOfDateTimestamp +
                ", nowWeek=" + nowWeek +
                ", data=" + data +
                '}'.toString()
    }

    class DataBean: Serializable {
        /**
         * hash_day : 0
         * hash_lesson : 1
         * begin_lesson : 3
         * day : 星期一
         * lesson : 三四节
         * course : 飞盘中级
         * course_num : A1090030
         * teacher : 王燕妮
         * classroom : 待定
         * rawWeek : 1-15周单周
         * weekModel : single
         * weekBegin : 1
         * weekEnd : 15
         * week : [1,3,5,7,9,11,13,15]
         * type : 必修
         * period : 2
         */

        var hash_day: Int = 0
        var hash_lesson: Int = 0
        var begin_lesson: Int = 0
        var day: String? = null
        var lesson: String? = null
        var course: String? = null
        var course_num: String? = null
        var teacher: String? = null
        var classroom: String? = null
        var rawWeek: String? = null
        var weekModel: String? = null
        var weekBegin: Int = 0
        var weekEnd: Int = 0
        var type: String? = null
        var period: Int = 0
        var week: List<Int>? = null

        override fun toString(): String {
            return "DataBean{" +
                    "hash_day=" + hash_day +
                    ", hash_lesson=" + hash_lesson +
                    ", begin_lesson=" + begin_lesson +
                    ", day='" + day + '\''.toString() +
                    ", lesson='" + lesson + '\''.toString() +
                    ", course='" + course + '\''.toString() +
                    ", course_num='" + course_num + '\''.toString() +
                    ", classroom='" + classroom + '\''.toString() +
                    ", rawWeek='" + rawWeek + '\''.toString() +
                    ", weekModel='" + weekModel + '\''.toString() +
                    ", weekBegin=" + weekBegin +
                    ", weekEnd=" + weekEnd +
                    ", type='" + type + '\''.toString() +
                    ", period=" + period +
                    ", week=" + week +
                    '}'.toString()
        }
    }
}
