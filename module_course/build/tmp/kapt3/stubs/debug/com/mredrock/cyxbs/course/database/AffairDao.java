package com.mredrock.cyxbs.course.database;

import java.lang.System;

/**
 * Created by anriku on 2018/8/14.
 */
@androidx.room.Dao()
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'J\b\u0010\u0006\u001a\u00020\u0003H\'J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\'J\u0016\u0010\n\u001a\u00020\u00032\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\fH\'J\u0014\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\f0\u000eH\'J\u0010\u0010\u000f\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\'\u00a8\u0006\u0010"}, d2 = {"Lcom/mredrock/cyxbs/course/database/AffairDao;", "", "deleteAffairById", "", "id", "", "deleteAllAffairs", "insertAffair", "affair", "Lcom/mredrock/cyxbs/course/network/Affair;", "insertAffairs", "affairs", "", "queryAllAffairs", "Lio/reactivex/Flowable;", "updateAffair", "module_course_debug"})
public abstract interface AffairDao {
    
    @androidx.room.Insert()
    public abstract void insertAffairs(@org.jetbrains.annotations.NotNull()
    java.util.List<com.mredrock.cyxbs.course.network.Affair> affairs);
    
    @androidx.room.Insert()
    public abstract void insertAffair(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.network.Affair affair);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM affairs")
    public abstract io.reactivex.Flowable<java.util.List<com.mredrock.cyxbs.course.network.Affair>> queryAllAffairs();
    
    @androidx.room.Query(value = "DELETE FROM affairs")
    public abstract void deleteAllAffairs();
    
    @androidx.room.Query(value = "DELETE FROM affairs WHERE id = :id")
    public abstract void deleteAffairById(long id);
    
    @androidx.room.Update()
    public abstract void updateAffair(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.network.Affair affair);
}