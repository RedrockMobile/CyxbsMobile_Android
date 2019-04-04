package com.mredrock.cyxbs.mine.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yan on 2018/6/2.
 */

public class MyAskQuestion implements Serializable {

    public String title;
    public String description;
    @SerializedName("question_id")
    public String questionId;
    @SerializedName("updated_at")
    public String updatedAt;
    @SerializedName("disappear_at")
    public String disappearAt;
    @SerializedName("created_at")
    public String createdAt;
    public List<Answer> answer;

    @Override
    public String toString() {
        return "{" +
                "questionId='" + questionId + '\'' +
                ", description='" + description + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", title='" + title + '\'' +
                ", disappearAt='" + disappearAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", answer='" + answer.toString() + '\'' +
                '}';
    }

}
