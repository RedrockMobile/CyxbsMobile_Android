package com.mredrock.cyxbs.account;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0007\bf\u0018\u00002\u00020\u0001:\u0002!\"J+\u0010\u0002\u001a\u00020\u00032!\u0010\u0004\u001a\u001d\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b\u0007\u0012\b\b\b\u0012\u0004\b\b(\t\u0012\u0004\u0012\u00020\u00030\u0005H&J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\nH&J\u0018\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\'J\b\u0010\u0010\u001a\u00020\u0011H&J\b\u0010\u0012\u001a\u00020\u0011H&J\b\u0010\u0013\u001a\u00020\u0011H&J\b\u0010\u0014\u001a\u00020\u0011H&J \u0010\u0015\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u000fH\'J\b\u0010\u0018\u001a\u00020\u0003H&J\u0010\u0010\u0019\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH&J=\u0010\u001a\u001a\u00020\u00032\u000e\b\u0002\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00030\u001c2#\b\u0002\u0010\u001d\u001a\u001d\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\u0007\u0012\b\b\b\u0012\u0004\b\b(\u001e\u0012\u0004\u0012\u00020\u00030\u0005H&J\b\u0010\u001f\u001a\u00020\u0003H&J\u0010\u0010 \u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\nH&\u00a8\u0006#"}, d2 = {"Lcom/mredrock/cyxbs/account/IUserStateService;", "", "addOnStateChangedListener", "", "listener", "Lkotlin/Function1;", "Lcom/mredrock/cyxbs/account/IUserStateService$UserState;", "Lkotlin/ParameterName;", "name", "state", "Lcom/mredrock/cyxbs/account/IUserStateService$StateListener;", "askLogin", "context", "Landroid/content/Context;", "reason", "", "isExpired", "", "isLogin", "isRefreshTokenExpired", "isTouristMode", "login", "uid", "passwd", "loginByTourist", "logout", "refresh", "onError", "Lkotlin/Function0;", "action", "token", "removeAllStateListeners", "removeStateChangedListener", "StateListener", "UserState", "account_api_debug"})
public abstract interface IUserStateService {
    
    @androidx.annotation.MainThread()
    public abstract void askLogin(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String reason);
    
    @androidx.annotation.WorkerThread()
    public abstract void login(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String uid, @org.jetbrains.annotations.NotNull()
    java.lang.String passwd) throws java.lang.Exception;
    
    public abstract void logout(@org.jetbrains.annotations.NotNull()
    android.content.Context context);
    
    public abstract void loginByTourist();
    
    public abstract void refresh(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> action);
    
    public abstract boolean isLogin();
    
    public abstract boolean isExpired();
    
    public abstract boolean isTouristMode();
    
    public abstract boolean isRefreshTokenExpired();
    
    public abstract void addOnStateChangedListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.mredrock.cyxbs.account.IUserStateService.UserState, kotlin.Unit> listener);
    
    public abstract void addOnStateChangedListener(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.account.IUserStateService.StateListener listener);
    
    public abstract void removeStateChangedListener(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.account.IUserStateService.StateListener listener);
    
    public abstract void removeAllStateListeners();
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lcom/mredrock/cyxbs/account/IUserStateService$UserState;", "", "(Ljava/lang/String;I)V", "LOGIN", "NOT_LOGIN", "EXPIRED", "TOURIST", "REFRESH", "account_api_debug"})
    public static enum UserState {
        /*public static final*/ LOGIN /* = new LOGIN() */,
        /*public static final*/ NOT_LOGIN /* = new NOT_LOGIN() */,
        /*public static final*/ EXPIRED /* = new EXPIRED() */,
        /*public static final*/ TOURIST /* = new TOURIST() */,
        /*public static final*/ REFRESH /* = new REFRESH() */;
        
        UserState() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lcom/mredrock/cyxbs/account/IUserStateService$StateListener;", "", "onStateChanged", "", "state", "Lcom/mredrock/cyxbs/account/IUserStateService$UserState;", "account_api_debug"})
    public static abstract interface StateListener {
        
        public abstract void onStateChanged(@org.jetbrains.annotations.NotNull()
        com.mredrock.cyxbs.account.IUserStateService.UserState state);
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 3)
    public final class DefaultImpls {
    }
}