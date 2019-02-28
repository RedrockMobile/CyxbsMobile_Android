package com.mredrock.cyxbs.mine.network.model;

import java.io.Serializable;

/**
 * Created by yan on 18-2-27.
 */

public class Question implements Serializable {


    /**
     * title : 这个代码太难写了\\ue056
     * description : 代码是真的难
     * kind : 其他
     * tags : PHP
     * reward : 2
     * answer_num : 1
     * disappear_at : 2018-04-30 01:11:20
     * created_at : 2018-04-22 14:28:54
     * is_anonymous : 0
     * id : 49
     * photo_thumbnail_src :
     * nickname : 。
     * gender : 女
     */

    private String title;
    private String description;
    private String kind;
    private String tags;
    private int reward;
    private int answer_num;
    private String disappear_at;
    private String created_at;
    private int is_anonymous;
    private int id;
    private String photo_thumbnail_src;
    private String nickname;
    private String gender;

    @Override
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", kind='" + kind + '\'' +
                ", tags='" + tags + '\'' +
                ", reward=" + reward +
                ", answer_num=" + answer_num +
                ", disappear_at='" + disappear_at + '\'' +
                ", created_at='" + created_at + '\'' +
                ", is_anonymous=" + is_anonymous +
                ", id=" + id +
                ", photo_thumbnail_src='" + photo_thumbnail_src + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getAnswer_num() {
        return answer_num;
    }

    public void setAnswer_num(int answer_num) {
        this.answer_num = answer_num;
    }

    public String getDisappear_at() {
        return disappear_at;
    }

    public void setDisappear_at(String disappear_at) {
        this.disappear_at = disappear_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getIs_anonymous() {
        return is_anonymous;
    }

    public void setIs_anonymous(int is_anonymous) {
        this.is_anonymous = is_anonymous;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoto_thumbnail_src() {
        return photo_thumbnail_src;
    }

    public void setPhoto_thumbnail_src(String photo_thumbnail_src) {
        this.photo_thumbnail_src = photo_thumbnail_src;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
