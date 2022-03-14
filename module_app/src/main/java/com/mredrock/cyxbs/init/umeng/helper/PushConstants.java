package com.mredrock.cyxbs.init.umeng.helper;

import com.mredrock.cyxbs.BuildConfig;
import com.mredrock.cyxbs.common.BaseApp;

/**
 * 常量类
 */
public class PushConstants {
    /**
     * 应用申请的Appkey
     */
    public static final String APP_KEY = BuildConfig.UM_APP_KEY;

    /**
     * 应用申请的UmengMessageSecret
     */
    public static final String MESSAGE_SECRET = BuildConfig.UM_PUSH_SECRET;

    /**
     * 渠道名称，修改为您App的发布渠道名称
     */
    //public static final String CHANNEL = WalleChannelReader.getChannel(BaseApp.Companion.getAppContext(), "debug");

    /**
     * 小米后台APP对应的xiaomi id
     */
    public static final String MI_ID = "";

    /**
     * 小米后台APP对应的xiaomi key
     */
    public static final String MI_KEY = "";

    /**
     * 魅族后台APP对应的xiaomi id
     */
    public static final String MEI_ZU_ID = "";

    /**
     * 魅族后台APP对应的xiaomi key
     */
    public static final String MEI_ZU_KEY = "";

    /**
     * OPPO后台APP对应的app key
     */
    public static final String OPPO_KEY = "";

    /**
     * OPPO后台APP对应的app secret
     */
    public static final String OPPO_SECRET = "";
}
