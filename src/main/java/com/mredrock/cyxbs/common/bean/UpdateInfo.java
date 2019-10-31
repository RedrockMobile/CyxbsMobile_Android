package com.mredrock.cyxbs.common.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Create By Hosigus at 2019/5/11
 */
@Root(name = "updateInfo")
public class UpdateInfo {

    @Element(name = "versionCode")
    public int versionCode;
    @Element(name = "versionName")
    public String versionName;
    @Element(name = "updateContent")
    public String updateContent;
    @Element(name = "apkURL")
    public String apkURL;

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "apkURL=" + apkURL +
                ", versionCode=" + versionCode +
                ", versionName=" + versionName +
                ", updateContent=" + updateContent +
                '}';
    }
}
