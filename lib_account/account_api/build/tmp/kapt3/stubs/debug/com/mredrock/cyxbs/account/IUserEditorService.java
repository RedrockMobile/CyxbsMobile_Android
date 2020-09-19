package com.mredrock.cyxbs.account;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH&J\u0010\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\bH&J\u0010\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\u0005H&J\u0010\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u0005H&J\u0010\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u0005H&J\u0010\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u0005H&\u00a8\u0006\u0013"}, d2 = {"Lcom/mredrock/cyxbs/account/IUserEditorService;", "", "setAvatarImgUrl", "", "avatarImgUrl", "", "setCheckInDay", "checkInDay", "", "setIntegral", "integral", "setIntroduction", "introduction", "setNickname", "nickname", "setPhone", "phone", "setQQ", "qq", "account_api_debug"})
public abstract interface IUserEditorService {
    
    public abstract void setNickname(@org.jetbrains.annotations.NotNull()
    java.lang.String nickname);
    
    public abstract void setAvatarImgUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String avatarImgUrl);
    
    public abstract void setIntroduction(@org.jetbrains.annotations.NotNull()
    java.lang.String introduction);
    
    public abstract void setPhone(@org.jetbrains.annotations.NotNull()
    java.lang.String phone);
    
    public abstract void setQQ(@org.jetbrains.annotations.NotNull()
    java.lang.String qq);
    
    public abstract void setIntegral(int integral);
    
    public abstract void setCheckInDay(int checkInDay);
}