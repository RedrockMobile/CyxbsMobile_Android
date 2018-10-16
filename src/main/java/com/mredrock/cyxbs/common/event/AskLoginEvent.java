package com.mredrock.cyxbs.common.event;


/**
 * Created by  : ACEMURDER
 * Created at  : 16/8/9.
 * Created for : CyxbsMobile_Android
 */
public class AskLoginEvent {
    private String msg;

    public AskLoginEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
