package com.mredrock.cyxbs.account;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000b\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0003H&J\b\u0010\u0007\u001a\u00020\u0003H&J\b\u0010\b\u001a\u00020\u0005H&J\b\u0010\t\u001a\u00020\u0003H&J\b\u0010\n\u001a\u00020\u0003H&J\b\u0010\u000b\u001a\u00020\u0003H&J\b\u0010\f\u001a\u00020\u0003H&J\b\u0010\r\u001a\u00020\u0003H&J\b\u0010\u000e\u001a\u00020\u0003H&J\b\u0010\u000f\u001a\u00020\u0003H&\u00a8\u0006\u0010"}, d2 = {"Lcom/mredrock/cyxbs/account/IUserService;", "", "getAvatarImgUrl", "", "getCheckInDay", "", "getCollege", "getGender", "getIntegral", "getIntroduction", "getNickname", "getPhone", "getQQ", "getRealName", "getRedid", "getStuNum", "account_api_debug"})
public abstract interface IUserService {
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getRedid();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getStuNum();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getNickname();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getAvatarImgUrl();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getIntroduction();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getPhone();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getQQ();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getGender();
    
    public abstract int getIntegral();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getRealName();
    
    public abstract int getCheckInDay();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getCollege();
}