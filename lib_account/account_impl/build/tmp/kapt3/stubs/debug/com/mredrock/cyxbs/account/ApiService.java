package com.mredrock.cyxbs.account;

import java.lang.System;

/**
 * Created By jay68 on 2018/8/10.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u0007H\'J\u001e\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\t\u001a\u00020\nH\'\u00a8\u0006\u000b"}, d2 = {"Lcom/mredrock/cyxbs/account/ApiService;", "", "login", "Lretrofit2/Call;", "Lcom/mredrock/cyxbs/common/bean/RedrockApiWrapper;", "Lcom/mredrock/cyxbs/account/bean/TokenWrapper;", "loginParams", "Lcom/mredrock/cyxbs/account/bean/LoginParams;", "refresh", "refreshParams", "Lcom/mredrock/cyxbs/account/bean/RefreshParams;", "account_impl_debug"})
public abstract interface ApiService {
    
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/app/token")
    public abstract retrofit2.Call<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<com.mredrock.cyxbs.account.bean.TokenWrapper>> login(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.mredrock.cyxbs.account.bean.LoginParams loginParams);
    
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/app/token/refresh")
    public abstract retrofit2.Call<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<com.mredrock.cyxbs.account.bean.TokenWrapper>> refresh(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.mredrock.cyxbs.account.bean.RefreshParams refreshParams);
}