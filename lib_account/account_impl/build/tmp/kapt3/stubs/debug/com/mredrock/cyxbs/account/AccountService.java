package com.mredrock.cyxbs.account;

import java.lang.System;

/**
 * Created By jay68 on 2019-11-12.
 */
@com.alibaba.android.arouter.facade.annotation.Route(path = "/account/service", name = "/account/service")
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\f\b\u0001\u0018\u0000 \u001d2\u00020\u0001:\u0005\u001d\u001e\u001f !B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0017\u001a\u00020\bH\u0016J\b\u0010\u0018\u001a\u00020\fH\u0016J\b\u0010\u0019\u001a\u00020\u0010H\u0016J\b\u0010\u001a\u001a\u00020\u000eH\u0016J\u0010\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u0006H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/mredrock/cyxbs/account/AccountService;", "Lcom/mredrock/cyxbs/account/IAccountService;", "()V", "isTouristMode", "", "mContext", "Landroid/content/Context;", "mUserEditorService", "Lcom/mredrock/cyxbs/account/IUserEditorService;", "mUserInfoEncryption", "Lcom/mredrock/cyxbs/account/utils/UserInfoEncryption;", "mUserService", "Lcom/mredrock/cyxbs/account/IUserService;", "mUserStateService", "Lcom/mredrock/cyxbs/account/IUserStateService;", "mUserTokenSerVice", "Lcom/mredrock/cyxbs/account/IUserTokenService;", "tokenWrapper", "Lcom/mredrock/cyxbs/account/bean/TokenWrapper;", "user", "Lcom/mredrock/cyxbs/account/bean/User;", "bind", "", "getUserEditorService", "getUserService", "getUserTokenService", "getVerifyService", "init", "context", "Companion", "UserEditorService", "UserService", "UserStateService", "UserTokenSerVice", "account_impl_debug"})
public final class AccountService implements com.mredrock.cyxbs.account.IAccountService {
    private final com.mredrock.cyxbs.account.IUserService mUserService = null;
    private final com.mredrock.cyxbs.account.IUserStateService mUserStateService = null;
    private final com.mredrock.cyxbs.account.IUserEditorService mUserEditorService = null;
    private final com.mredrock.cyxbs.account.IUserTokenService mUserTokenSerVice = null;
    private final com.mredrock.cyxbs.account.utils.UserInfoEncryption mUserInfoEncryption = null;
    private com.mredrock.cyxbs.account.bean.User user;
    private com.mredrock.cyxbs.account.bean.TokenWrapper tokenWrapper;
    private boolean isTouristMode = false;
    private android.content.Context mContext;
    private static final java.lang.String TAG = null;
    public static final com.mredrock.cyxbs.account.AccountService.Companion Companion = null;
    
    @java.lang.Override()
    public void init(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.mredrock.cyxbs.account.IUserService getUserService() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.mredrock.cyxbs.account.IUserStateService getVerifyService() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.mredrock.cyxbs.account.IUserEditorService getUserEditorService() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.mredrock.cyxbs.account.IUserTokenService getUserTokenService() {
        return null;
    }
    
    private final void bind(com.mredrock.cyxbs.account.bean.TokenWrapper tokenWrapper) {
    }
    
    public AccountService() {
        super();
    }
    
    public static final java.lang.String getTAG() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000b\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\u0004H\u0016J\b\u0010\b\u001a\u00020\u0004H\u0016J\b\u0010\t\u001a\u00020\u0006H\u0016J\b\u0010\n\u001a\u00020\u0004H\u0016J\b\u0010\u000b\u001a\u00020\u0004H\u0016J\b\u0010\f\u001a\u00020\u0004H\u0016J\b\u0010\r\u001a\u00020\u0004H\u0016J\b\u0010\u000e\u001a\u00020\u0004H\u0016J\b\u0010\u000f\u001a\u00020\u0004H\u0016J\b\u0010\u0010\u001a\u00020\u0004H\u0016\u00a8\u0006\u0011"}, d2 = {"Lcom/mredrock/cyxbs/account/AccountService$UserService;", "Lcom/mredrock/cyxbs/account/IUserService;", "(Lcom/mredrock/cyxbs/account/AccountService;)V", "getAvatarImgUrl", "", "getCheckInDay", "", "getCollege", "getGender", "getIntegral", "getIntroduction", "getNickname", "getPhone", "getQQ", "getRealName", "getRedid", "getStuNum", "account_impl_debug"})
    public final class UserService implements com.mredrock.cyxbs.account.IUserService {
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getRedid() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getStuNum() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getNickname() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getAvatarImgUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getIntroduction() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getPhone() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getQQ() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getGender() {
            return null;
        }
        
        @java.lang.Override()
        public int getIntegral() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getRealName() {
            return null;
        }
        
        @java.lang.Override()
        public int getCheckInDay() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getCollege() {
            return null;
        }
        
        public UserService() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000b\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\tH\u0016J\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u0006H\u0016J\u0010\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u0006H\u0016J\u0010\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0006H\u0016J\u0010\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u0006H\u0016\u00a8\u0006\u0014"}, d2 = {"Lcom/mredrock/cyxbs/account/AccountService$UserEditorService;", "Lcom/mredrock/cyxbs/account/IUserEditorService;", "(Lcom/mredrock/cyxbs/account/AccountService;)V", "setAvatarImgUrl", "", "avatarImgUrl", "", "setCheckInDay", "checkInDay", "", "setIntegral", "integral", "setIntroduction", "introduction", "setNickname", "nickname", "setPhone", "phone", "setQQ", "qq", "account_impl_debug"})
    public final class UserEditorService implements com.mredrock.cyxbs.account.IUserEditorService {
        
        @java.lang.Override()
        public void setNickname(@org.jetbrains.annotations.NotNull()
        java.lang.String nickname) {
        }
        
        @java.lang.Override()
        public void setAvatarImgUrl(@org.jetbrains.annotations.NotNull()
        java.lang.String avatarImgUrl) {
        }
        
        @java.lang.Override()
        public void setIntroduction(@org.jetbrains.annotations.NotNull()
        java.lang.String introduction) {
        }
        
        @java.lang.Override()
        public void setPhone(@org.jetbrains.annotations.NotNull()
        java.lang.String phone) {
        }
        
        @java.lang.Override()
        public void setQQ(@org.jetbrains.annotations.NotNull()
        java.lang.String qq) {
        }
        
        @java.lang.Override()
        public void setIntegral(int integral) {
        }
        
        @java.lang.Override()
        public void setCheckInDay(int checkInDay) {
        }
        
        public UserEditorService() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J+\u0010\u0006\u001a\u00020\u00072!\u0010\b\u001a\u001d\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0004\u0012\u00020\u00070\tH\u0016J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u0016J\u0018\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0017J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\b\u0010\u0015\u001a\u00020\u0014H\u0016J\b\u0010\u0016\u001a\u00020\u0014H\u0016J\b\u0010\u0017\u001a\u00020\u0014H\u0016J \u0010\u0018\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u0012H\u0017J\b\u0010\u001b\u001a\u00020\u0007H\u0016J\u000e\u0010\u001c\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0010J\u0010\u0010\u001d\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010\u001e\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\nH\u0002J9\u0010\u001f\u001a\u00020\u00072\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00070!2!\u0010\"\u001a\u001d\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(#\u0012\u0004\u0012\u00020\u00070\tH\u0016J\b\u0010$\u001a\u00020\u0007H\u0016J\u0010\u0010%\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u0016R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/mredrock/cyxbs/account/AccountService$UserStateService;", "Lcom/mredrock/cyxbs/account/IUserStateService;", "(Lcom/mredrock/cyxbs/account/AccountService;)V", "stateListeners", "", "Lcom/mredrock/cyxbs/account/IUserStateService$StateListener;", "addOnStateChangedListener", "", "listener", "Lkotlin/Function1;", "Lcom/mredrock/cyxbs/account/IUserStateService$UserState;", "Lkotlin/ParameterName;", "name", "state", "askLogin", "context", "Landroid/content/Context;", "reason", "", "isExpired", "", "isLogin", "isRefreshTokenExpired", "isTouristMode", "login", "uid", "passwd", "loginByTourist", "loginFromCache", "logout", "notifyAllStateListeners", "refresh", "onError", "Lkotlin/Function0;", "action", "token", "removeAllStateListeners", "removeStateChangedListener", "account_impl_debug"})
    public final class UserStateService implements com.mredrock.cyxbs.account.IUserStateService {
        private final java.util.List<com.mredrock.cyxbs.account.IUserStateService.StateListener> stateListeners = null;
        
        @java.lang.Override()
        public void addOnStateChangedListener(@org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super com.mredrock.cyxbs.account.IUserStateService.UserState, kotlin.Unit> listener) {
        }
        
        @java.lang.Override()
        public void addOnStateChangedListener(@org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.account.IUserStateService.StateListener listener) {
        }
        
        @java.lang.Override()
        public void removeStateChangedListener(@org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.account.IUserStateService.StateListener listener) {
        }
        
        @java.lang.Override()
        public void removeAllStateListeners() {
        }
        
        private final void notifyAllStateListeners(com.mredrock.cyxbs.account.IUserStateService.UserState state) {
        }
        
        @java.lang.Override()
        public boolean isLogin() {
            return false;
        }
        
        @java.lang.Override()
        public boolean isExpired() {
            return false;
        }
        
        @java.lang.Override()
        public boolean isTouristMode() {
            return false;
        }
        
        @java.lang.Override()
        public boolean isRefreshTokenExpired() {
            return false;
        }
        
        public final void loginFromCache(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
        
        @java.lang.Override()
        public void refresh(@org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function0<kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> action) {
        }
        
        @androidx.annotation.MainThread()
        @java.lang.Override()
        public void askLogin(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        java.lang.String reason) {
        }
        
        /**
         * 登录
         * @throws IllegalStateException
         */
        @androidx.annotation.WorkerThread()
        @java.lang.Override()
        public void login(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        java.lang.String uid, @org.jetbrains.annotations.NotNull()
        java.lang.String passwd) {
        }
        
        @java.lang.Override()
        public void logout(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
        
        @java.lang.Override()
        public void loginByTourist() {
        }
        
        public UserStateService() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0004H\u0016\u00a8\u0006\u0006"}, d2 = {"Lcom/mredrock/cyxbs/account/AccountService$UserTokenSerVice;", "Lcom/mredrock/cyxbs/account/IUserTokenService;", "(Lcom/mredrock/cyxbs/account/AccountService;)V", "getRefreshToken", "", "getToken", "account_impl_debug"})
    public final class UserTokenSerVice implements com.mredrock.cyxbs.account.IUserTokenService {
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getRefreshToken() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String getToken() {
            return null;
        }
        
        public UserTokenSerVice() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R$\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u00048\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0006\u0010\u0002\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/mredrock/cyxbs/account/AccountService$Companion;", "", "()V", "TAG", "", "kotlin.jvm.PlatformType", "TAG$annotations", "getTAG", "()Ljava/lang/String;", "account_impl_debug"})
    public static final class Companion {
        
        @java.lang.Deprecated()
        public static void TAG$annotations() {
        }
        
        public final java.lang.String getTAG() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}