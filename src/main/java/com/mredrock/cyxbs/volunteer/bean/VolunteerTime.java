package com.mredrock.cyxbs.volunteer.bean;

import java.util.List;

public class VolunteerTime {

    /**
     * code : 0
     * msg : success
     * hours : 43
     * record : [{"hours":"2.0","content":"系统导入 三下乡志愿者 ","start_time":"2018-09-29 13:02:21","title":"国际学院三下乡时长补录5","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学国际学院青年志愿服务队","uid":"2016214765"},{"hours":"8.0","content":"系统导入 后勤 ","start_time":"2018-09-28 16:45:49","title":"留动中国后勤部补录3","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学青年志愿服务队","uid":"2016214765"},{"hours":"8.0","content":"系统导入 后勤 ","start_time":"2018-09-28 16:08:25","title":"留动中国后勤部补录2","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学青年志愿服务队","uid":"2016214765"},{"hours":"4.0","content":"系统导入 签到 ","start_time":"2018-09-28 13:26:33","title":"留动中国后勤部补录1","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学青年志愿服务队","uid":"2016214765"},{"hours":"2.0","content":"系统导入 五月护跑时长补录 ","start_time":"2018-09-26 00:46:06","title":"五月\u201c天天护跑\u201d时长补录2","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学青年志愿服务队","uid":"2016214765"},{"hours":"3.0","content":"辅导外国语同学计算机二级过级 ","start_time":"2018-09-25 20:56:29","title":"国际学院2017至2018年志愿时长补录1","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学国际学院青年志愿服务队","uid":"2016214765"},{"hours":"2.0","content":"系统导入 一次三清 ","start_time":"2018-09-25 15:22:16","title":"国际学院2017至2018年志愿时长补录2","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学国际学院青年志愿服务队","uid":"2016214765"},{"hours":"6.0","content":"暑假作业评定工作 ","start_time":"2018-09-24 16:31:02","title":"国际学院2017至2018年志愿时长补录1","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学国际学院青年志愿服务队","uid":"2016214765"},{"hours":"6.0","content":"系统导入 2017-2018上学年红岩网校工作站移动开发部干事良好，2017-2018下学年红岩网校工作站移动开发部干事良好 ","start_time":"2018-09-23 01:36:41","title":"国际学院2017至2018年志愿时长补录3","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学国际学院青年志愿服务队","uid":"2016214765"},{"hours":"2.0","content":"系统导入 2018-5-4风华运动场天天护跑 ","start_time":"2018-09-21 20:05:15","title":"\u201c天天护跑\u201d补录21","addWay":"团体录入","status":"已生效","server_group":"重庆邮电大学青年志愿服务队","uid":"2016214765"}]
     */

    private String code;
    private String msg;
    private int hours;
    private List<RecordBean> record;

    @Override
    public String toString() {
        return "VolunteerTime{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", hours=" + hours +
                ", record=" + record +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public List<RecordBean> getRecord() {
        return record;
    }

    public void setRecord(List<RecordBean> record) {
        this.record = record;
    }

    public static class RecordBean {
        /**
         * hours : 2.0
         * content : 系统导入 三下乡志愿者
         * start_time : 2018-09-29 13:02:21
         * title : 国际学院三下乡时长补录5
         * addWay : 团体录入
         * status : 已生效
         * server_group : 重庆邮电大学国际学院青年志愿服务队
         * uid : 2016214765
         */

        private String hours;
        private String content;
        private String start_time;
        private String title;
        private String addWay;
        private String status;
        private String server_group;
        private String uid;

        @Override
        public String toString() {
            return "RecordBean{" +
                    "hours='" + hours + '\'' +
                    ", content='" + content + '\'' +
                    ", start_time='" + start_time + '\'' +
                    ", title='" + title + '\'' +
                    ", addWay='" + addWay + '\'' +
                    ", status='" + status + '\'' +
                    ", server_group='" + server_group + '\'' +
                    ", uid='" + uid + '\'' +
                    '}';
        }

        public String getHours() {
            return hours;
        }

        public void setHours(String hours) {
            this.hours = hours;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAddWay() {
            return addWay;
        }

        public void setAddWay(String addWay) {
            this.addWay = addWay;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getServer_group() {
            return server_group;
        }

        public void setServer_group(String server_group) {
            this.server_group = server_group;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }
}
