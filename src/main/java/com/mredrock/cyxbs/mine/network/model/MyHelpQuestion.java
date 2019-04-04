package com.mredrock.cyxbs.mine.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Create By Hosigus at 2019/4/4
 */
public class MyHelpQuestion implements Serializable {

    /**
     * id : 91
     * question_id : 230
     * content : 嘿嘿
     * created_at : 2019-03-19 13:15:51
     * question_title : 这个代码太难写了:smile:
     * disappear_at : 2028-04-30 01:11:20
     * updated_at :
     */

    private String id;
    @SerializedName("question_id")
    private String questionId;
    private String content;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("question_title")
    private String questionTitle;
    @SerializedName("disappear_at")
    private String disappearAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getDisappearAt() {
        return disappearAt;
    }

    public void setDisappearAt(String disappearAt) {
        this.disappearAt = disappearAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
