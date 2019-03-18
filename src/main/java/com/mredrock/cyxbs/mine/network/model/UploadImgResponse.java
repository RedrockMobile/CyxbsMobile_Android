package com.mredrock.cyxbs.mine.network.model;

import java.io.Serializable;

/**
 * Created by zzzia on 2018/8/21.
 * 图片上传，返回地址
 */
public class UploadImgResponse implements Serializable {
    @Override
    public String toString() {
        return "UploadImgResponse{" +
                "date='" + date + '\'' +
                ", photosrc='" + photosrc + '\'' +
                ", thumbnail_src='" + thumbnail_src + '\'' +
                ", state=" + state +
                ", stunum='" + stunum + '\'' +
                '}';
    }

    /**
     * date : 2018-08-21 16:05:37
     * photosrc : http://wx.idsbllp.cn/app/Public/photo/1534838737_1029204034.jpg
     * thumbnail_src : http://wx.idsbllp.cn/app/Public/photo/thumbnail/1534838737_1029204034.jpg
     * state : 1
     * stunum : 2016210409
     */



    private String date;
    private String photosrc;
    private String thumbnail_src;
    private int state;
    private String stunum;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhotosrc() {
        return photosrc;
    }

    public void setPhotosrc(String photosrc) {
        this.photosrc = photosrc;
    }

    public String getThumbnail_src() {
        return thumbnail_src;
    }

    public void setThumbnail_src(String thumbnail_src) {
        this.thumbnail_src = thumbnail_src;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStunum() {
        return stunum;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }
}
