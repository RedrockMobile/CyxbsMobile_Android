package com.mredrock.cyxbs.mine.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Create By Hosigus at 2019/3/16
 */
public class PointDetail implements Serializable {
    /**
     * num : 10
     * event_type : 签到
     * createdAt : 2019-03-14 09:04:57
     */

    private String num;
    @SerializedName("event_type")
    private String eventType;
    @SerializedName("created_at")
    private String createdAt;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
