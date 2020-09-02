package com.mredrock.cyxbs.course.database;

import java.lang.System;

/**
 * Created by anriku on 2018/8/14.
 */
@androidx.room.Database(entities = {com.mredrock.cyxbs.course.network.Course.class, com.mredrock.cyxbs.course.network.Affair.class}, version = 4, exportSchema = false)
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\b"}, d2 = {"Lcom/mredrock/cyxbs/course/database/ScheduleDatabase;", "Landroidx/room/RoomDatabase;", "()V", "affairDao", "Lcom/mredrock/cyxbs/course/database/AffairDao;", "courseDao", "Lcom/mredrock/cyxbs/course/database/CourseDao;", "Companion", "module_course_debug"})
public abstract class ScheduleDatabase extends androidx.room.RoomDatabase {
    private static com.mredrock.cyxbs.course.database.ScheduleDatabase INSTANCE;
    public static final com.mredrock.cyxbs.course.database.ScheduleDatabase.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mredrock.cyxbs.course.database.AffairDao affairDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.mredrock.cyxbs.course.database.CourseDao courseDao();
    
    public ScheduleDatabase() {
        super();
    }
    
    /**
     * use singleton to avoid concurrent problem
     */
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J \u0010\u0005\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/mredrock/cyxbs/course/database/ScheduleDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/mredrock/cyxbs/course/database/ScheduleDatabase;", "getDatabase", "context", "Landroid/content/Context;", "isGetOther", "", "stuNum", "", "module_course_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.Nullable()
        public final com.mredrock.cyxbs.course.database.ScheduleDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context, boolean isGetOther, @org.jetbrains.annotations.NotNull()
        java.lang.String stuNum) {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}