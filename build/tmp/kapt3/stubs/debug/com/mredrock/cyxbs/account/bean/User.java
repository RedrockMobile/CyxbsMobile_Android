package com.mredrock.cyxbs.account.bean;

import java.lang.System;

/**
 * Created By jay68 on 2019-11-13.
 */
@androidx.annotation.Keep()
@android.annotation.SuppressLint(value = {"CI_ByteDanceKotlinRules_Parcelable_Annotation"})
@kotlin.Metadata(mv = {1, 1, 15}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b*\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000 :2\u00020\u0001:\u0001:B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0005\u00a2\u0006\u0002\u0010\u0005J\b\u00106\u001a\u00020\rH\u0016J\u0018\u00107\u001a\u0002082\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u00109\u001a\u00020\rH\u0016R \u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001e\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001e\u0010\u0012\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\t\"\u0004\b\u0014\u0010\u000bR \u0010\u0015\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\t\"\u0004\b\u0017\u0010\u000bR\u001e\u0010\u0018\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\t\"\u0004\b\u001a\u0010\u000bR\u001e\u0010\u001b\u001a\u00020\r8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u000f\"\u0004\b\u001d\u0010\u0011R \u0010\u001e\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\t\"\u0004\b \u0010\u000bR\u001e\u0010!\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\t\"\u0004\b#\u0010\u000bR \u0010$\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\t\"\u0004\b&\u0010\u000bR \u0010\'\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b(\u0010\t\"\u0004\b)\u0010\u000bR \u0010*\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b+\u0010\t\"\u0004\b,\u0010\u000bR\u001e\u0010-\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b.\u0010\t\"\u0004\b/\u0010\u000bR\u001e\u00100\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b1\u0010\t\"\u0004\b2\u0010\u000bR\u001e\u00103\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b4\u0010\t\"\u0004\b5\u0010\u000b\u00a8\u0006;"}, d2 = {"Lcom/mredrock/cyxbs/account/bean/User;", "Landroid/os/Parcelable;", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "()V", "avatarImgUrl", "", "getAvatarImgUrl", "()Ljava/lang/String;", "setAvatarImgUrl", "(Ljava/lang/String;)V", "checkInDay", "", "getCheckInDay", "()I", "setCheckInDay", "(I)V", "exp", "getExp", "setExp", "gender", "getGender", "setGender", "iat", "getIat", "setIat", "integral", "getIntegral", "setIntegral", "introduction", "getIntroduction", "setIntroduction", "nickname", "getNickname", "setNickname", "phone", "getPhone", "setPhone", "qq", "getQq", "setQq", "realName", "getRealName", "setRealName", "redid", "getRedid", "setRedid", "stuNum", "getStuNum", "setStuNum", "sub", "getSub", "setSub", "describeContents", "writeToParcel", "", "flags", "CREATOR", "lib_account_debug"})
public final class User implements android.os.Parcelable {
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "redid")
    private java.lang.String redid;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "stuNum")
    private java.lang.String stuNum;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "nickname")
    private java.lang.String nickname;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "headImgUrl")
    private java.lang.String avatarImgUrl;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "introduction")
    private java.lang.String introduction;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "phone")
    private java.lang.String phone;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "qq")
    private java.lang.String qq;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "gender")
    private java.lang.String gender;
    @com.google.gson.annotations.SerializedName(value = "integral")
    private int integral;
    @org.jetbrains.annotations.Nullable()
    @com.google.gson.annotations.SerializedName(value = "realName")
    private java.lang.String realName;
    @com.google.gson.annotations.SerializedName(value = "checkInDay")
    private int checkInDay;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "exp")
    private java.lang.String exp;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "iat")
    private java.lang.String iat;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "sub")
    private java.lang.String sub;
    public static final com.mredrock.cyxbs.account.bean.User.CREATOR CREATOR = null;
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRedid() {
        return null;
    }
    
    public final void setRedid(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStuNum() {
        return null;
    }
    
    public final void setStuNum(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNickname() {
        return null;
    }
    
    public final void setNickname(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAvatarImgUrl() {
        return null;
    }
    
    public final void setAvatarImgUrl(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getIntroduction() {
        return null;
    }
    
    public final void setIntroduction(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPhone() {
        return null;
    }
    
    public final void setPhone(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getQq() {
        return null;
    }
    
    public final void setQq(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getGender() {
        return null;
    }
    
    public final void setGender(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final int getIntegral() {
        return 0;
    }
    
    public final void setIntegral(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRealName() {
        return null;
    }
    
    public final void setRealName(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    public final int getCheckInDay() {
        return 0;
    }
    
    public final void setCheckInDay(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getExp() {
        return null;
    }
    
    public final void setExp(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getIat() {
        return null;
    }
    
    public final void setIat(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSub() {
        return null;
    }
    
    public final void setSub(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @java.lang.Override()
    public void writeToParcel(@org.jetbrains.annotations.NotNull()
    android.os.Parcel parcel, int flags) {
    }
    
    @java.lang.Override()
    public int describeContents() {
        return 0;
    }
    
    public User() {
        super();
    }
    
    public User(@org.jetbrains.annotations.NotNull()
    android.os.Parcel parcel) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 15}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0016\u0010\u0007\u001a\n \b*\u0004\u0018\u00010\u00020\u00022\u0006\u0010\t\u001a\u00020\nJ\u001d\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/mredrock/cyxbs/account/bean/User$CREATOR;", "Landroid/os/Parcelable$Creator;", "Lcom/mredrock/cyxbs/account/bean/User;", "()V", "createFromParcel", "parcel", "Landroid/os/Parcel;", "fromJson", "kotlin.jvm.PlatformType", "json", "", "newArray", "", "size", "", "(I)[Lcom/mredrock/cyxbs/account/bean/User;", "lib_account_debug"})
    public static final class CREATOR implements android.os.Parcelable.Creator<com.mredrock.cyxbs.account.bean.User> {
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public com.mredrock.cyxbs.account.bean.User createFromParcel(@org.jetbrains.annotations.NotNull()
        android.os.Parcel parcel) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public com.mredrock.cyxbs.account.bean.User[] newArray(int size) {
            return null;
        }
        
        public final com.mredrock.cyxbs.account.bean.User fromJson(@org.jetbrains.annotations.NotNull()
        java.lang.String json) {
            return null;
        }
        
        private CREATOR() {
            super();
        }
    }
}