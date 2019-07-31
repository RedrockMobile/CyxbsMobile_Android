package com.mredrock.cyxbs;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;

//import androidx.annotation.Keep;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 */
public class CySophixApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(App.class)
    static class RealApplicationStub { }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
         MultiDex.install(this);
        initSophix();
    }

    private void initSophix() {
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception ignored) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData("27732063-1", "cfc0333033e04d9b30f2d41bde17b7c5", "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC2Dp6fjhzRoO2OB78yt9LNszh6WKiFxiAuh6YRFhnljki6/4UF5GSQAsVkx1x2J/Drv9t3zGXCWR0ohkR8cXf/BsYPqsg2UrjBMSItnzEIkgKJpXqL56UPQABBt6JAjOYewWjyPx2Gllg/RLYipX6XW2IdSqYi0pFSN9TyGdztS61Al0ngCzzVQhJW7enmx//j7A7nLL8IiS0Xza19hhdFduZFebktBMvgKHW+hqN4YfXO9FfRP+4Jfn6rw4UWUXYcw4AjkFjEuSLbFy7859YwCxxybqGA+oXW0KDGdDib/CQJnft6WXq0CsfCPJNoAAWuYZehysMl54alzhF0R2K5AgMBAAECggEBAKwg+HKsv6ynddMCmhCO4p2fpHdwma0ZXNYBZM7k0YlL1JIRAqlGfpn2NMGLCG2iZ9PNqugCdfehn8Lv55KtCaIyulKXywIphgsKtGDkEU0kF4giz9G1K0dW4KdBwwM69FwBy4K2j+ju/vKauYXaaO0b9pDxQlDMO5+7r/dS4qHrf4OT1o+6RlCJ8hjmctmWsiJs1LQopNrVfer/lPug2tTDmejvGDf67WfNasSFgdHF5gyBYz4gyzyu5GkZXKDSb4ceNbtKmDyFu6kcLBxdxbkZt9yqvavRobe1m0oR0WyvXHO5V9gfzDdKejOkMj6zOuCUd2hE5p2hioMovVhyHTECgYEA8Gc9ymlRfK1vzp2Daf1RyHakzR9taRyfluUSfuOFqlpkTqpvM8fPncBytQr1S5LdDO46RYAIRVjPhudCjKgl9I9F4Iq6Nc/i6TEPQVorxqOefdsV/eKp9dJ6dOgdqCUgSy75LruvFmVkuAWJGY4acMe++byxIl7MW7I/5O4stBUCgYEAwd5UquiEFy2VbgPScN+HBC3knvylSmWVaJFgqkXgdDuKPvEEfhpaR4igPvXT2+rbtYR8Nz8gHK4UgM4m2va4ReQ+t8XAtVsEY81Ob3yG6avmfZZSwjSprDE9koqsxBbjnA8JPx136nkeG74lPLv9tho4PTCmyyTh2MbyULHAaRUCgYEA8Bwd0joIwn8zyej25Xi3I4KkrZ9zFuYY24ymQYgb6/7mas+9y7kJO6WIrBxqzXW9RPn3x4zONf0zIal/hZ84caBcCZ5lx/N/zqtKclqOmJK5bTjSKUcnvDfiSTvAyz1xmMYs6alZggP5afdVbOnKrNREgYkeXpbSg89wE8ZzbdUCgYA1LoJXZ/VkTlLhnRbLc+Yb1WMT4gaNxBQaXVcBHq4V9IdWANPUq/H8EZSz8MevWlvvDWrt1NlARNjHBMZP5sJiGdzCmPLmTROFNTrBBo4T594QsX3+XGf3HoLfgj8mg4jotI86yyCsJ5GE1sJsSL2uiL7IumHm9DUoPFsYm8pGiQKBgD3mnXwgI4su3mvgfib7+aQJK0z5sGypgdl2f5qpQSrtNaWqT6wc8MKJbfbx5rSslO4MKqUwZwdgyjP9tMDbEhzARdgR9w033JkHPHtIQgNEvtWaF48L2YjjhnxfQmtDvecCyfKFnJDsKcS7C4z1aAy51Om1LyS1MzbSiaprbq/b")
                .setEnableDebug(BuildConfig.DEBUG)
//                .setAesKey("AES")
                .setEnableFullLog()
                .setPatchLoadStatusStub((mode, code, info, handlePatchVersion) -> {
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.i(TAG, "sophix load patch success!");
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                    }
                }).initialize();
    }
}