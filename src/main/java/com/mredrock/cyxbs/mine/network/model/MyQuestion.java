package com.mredrock.cyxbs.mine.network.model;

/**
 * Created by yan on 2018/6/2.
 */

public class MyQuestion {

    /**
     * id : 18
     * question_id : 71
     * content : 因为这个学长并不想找女朋友（滑稽
     * updated_at : 2018-05-27 11:13:31
     * question_title : 为什么某学长没有对象
     * disappear_at : 2018-06-01 00:00:00
     * created_at :
     */

    public String id;
    public String question_id;
    public String content;
    public String updated_at;
    public String question_title;
    public String disappear_at;
    public String created_at;

    @Override
    public String toString() {
        return "MyQuestion{" +
                "id='" + id + '\'' +
                ", question_id='" + question_id + '\'' +
                ", content='" + content + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", question_title='" + question_title + '\'' +
                ", disappear_at='" + disappear_at + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
