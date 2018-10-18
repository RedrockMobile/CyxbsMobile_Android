package com.mredrock.cyxbs.mine.network.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by cc on 16/5/8.
 */
@Root(name = "updateInfo")
public class UpdateInfo {

    @Element(name = "versionCode")
    public int    versionCode;
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
