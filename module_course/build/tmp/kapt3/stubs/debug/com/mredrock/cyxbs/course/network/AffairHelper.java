package com.mredrock.cyxbs.course.network;

import java.lang.System;

/**
 * Created by anriku on 2018/9/12.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J4\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0018\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u000b0\b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\bJ.\u0010\u000e\u001a\u00020\u000f2\u0018\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u000b0\b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\bJ\u0006\u0010\u0010\u001a\u00020\u000fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/mredrock/cyxbs/course/network/AffairHelper;", "", "()V", "mGson", "Lcom/google/gson/Gson;", "mRandom", "Ljava/util/Random;", "generateAffairDate", "", "Lcom/mredrock/cyxbs/course/network/Affair$Date;", "classAndDays", "Lkotlin/Pair;", "", "weeks", "generateAffairDateString", "", "generateAffairId", "module_course_debug"})
public final class AffairHelper {
    private static final java.util.Random mRandom = null;
    private static final com.google.gson.Gson mGson = null;
    public static final com.mredrock.cyxbs.course.network.AffairHelper INSTANCE = null;
    
    /**
     * This function is used to generate a affair id, affair id is composed of a timestamp + 4 figures.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateAffairId() {
        return null;
    }
    
    /**
     * There use [Gson] to generate a String conveniently.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateAffairDateString(@org.jetbrains.annotations.NotNull()
    java.util.List<kotlin.Pair<java.lang.Integer, java.lang.Integer>> classAndDays, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> weeks) {
        return null;
    }
    
    /**
     * This function is used to generate a affair [Affair.Date] list.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.mredrock.cyxbs.course.network.Affair.Date> generateAffairDate(@org.jetbrains.annotations.NotNull()
    java.util.List<kotlin.Pair<java.lang.Integer, java.lang.Integer>> classAndDays, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Integer> weeks) {
        return null;
    }
    
    private AffairHelper() {
        super();
    }
}