package com.mredrock.cyxbs.course.database.converter;

import java.lang.System;

/**
 * Created by anriku on 2018/8/17.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u000e\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006H\u0007J\u001a\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u00062\b\u0010\t\u001a\u0004\u0018\u00010\u0004H\u0007\u00a8\u0006\n"}, d2 = {"Lcom/mredrock/cyxbs/course/database/converter/DateListStringConverter;", "", "()V", "dateListToString", "", "dateList", "", "Lcom/mredrock/cyxbs/course/network/Affair$Date;", "stringToDateList", "string", "module_course_debug"})
public final class DateListStringConverter {
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.TypeConverter()
    public final java.lang.String dateListToString(@org.jetbrains.annotations.Nullable()
    java.util.List<com.mredrock.cyxbs.course.network.Affair.Date> dateList) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.TypeConverter()
    public final java.util.List<com.mredrock.cyxbs.course.network.Affair.Date> stringToDateList(@org.jetbrains.annotations.Nullable()
    java.lang.String string) {
        return null;
    }
    
    public DateListStringConverter() {
        super();
    }
}