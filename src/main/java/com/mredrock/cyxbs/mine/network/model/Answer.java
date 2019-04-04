package com.mredrock.cyxbs.mine.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Create By Hosigus at 2019/4/3
 */
public class Answer {
    /**
     * answer_id : 34
     * content : 幸运儿.jpg
     * created_at : 2018-05-27 19:37:05
     * updated_at : 2018-05-27 19:37:05
     * state : 1
     */

    public String content;
    public String state;
    @SerializedName("answer_id")
    public String answerId;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;

    @Override
    public String toString() {
        return "{" +
                "content='" + content + '\'' +
                ", state='" + state + '\'' +
                ", answerId='" + answerId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
