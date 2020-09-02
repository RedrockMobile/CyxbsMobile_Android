package com.mredrock.cyxbs.course.network;

import java.lang.System;

/**
 * This class is used to map the affair to course, so let it more easy to handle the display.
 *
 * Created by anriku on 2018/8/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \b2\u001a\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00020\u0001:\u0001\bB\u0005\u00a2\u0006\u0002\u0010\u0005J\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u00022\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u0016\u00a8\u0006\t"}, d2 = {"Lcom/mredrock/cyxbs/course/network/AffairMapToCourse;", "Lio/reactivex/functions/Function;", "", "Lcom/mredrock/cyxbs/course/network/Affair;", "Lcom/mredrock/cyxbs/course/network/Course;", "()V", "apply", "t", "Companion", "module_course_debug"})
public final class AffairMapToCourse implements io.reactivex.functions.Function<java.util.List<? extends com.mredrock.cyxbs.course.network.Affair>, java.util.List<? extends com.mredrock.cyxbs.course.network.Course>> {
    private static final java.lang.String TAG = "AffairMapToCourse";
    public static final com.mredrock.cyxbs.course.network.AffairMapToCourse.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.util.List<com.mredrock.cyxbs.course.network.Course> apply(@org.jetbrains.annotations.NotNull()
    java.util.List<com.mredrock.cyxbs.course.network.Affair> t) {
        return null;
    }
    
    public AffairMapToCourse() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/mredrock/cyxbs/course/network/AffairMapToCourse$Companion;", "", "()V", "TAG", "", "module_course_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}