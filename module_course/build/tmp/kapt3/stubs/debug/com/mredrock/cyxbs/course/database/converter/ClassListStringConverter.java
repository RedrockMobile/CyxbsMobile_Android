package com.mredrock.cyxbs.course.database.converter;

import java.lang.System;

/**
 * @author Jovines
 * @create 2020-02-11 5:55 PM
 *
 * 描述:
 *  用于转换老师所教班级的room转换器
 *  【另外，有一说一，别通宵写代码，这下看了都怕这个时间】
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u000e\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0006H\u0007J\u001a\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u00062\b\u0010\b\u001a\u0004\u0018\u00010\u0004H\u0007\u00a8\u0006\t"}, d2 = {"Lcom/mredrock/cyxbs/course/database/converter/ClassListStringConverter;", "", "()V", "strListToString", "", "intList", "", "stringToStrList", "string", "module_course_debug"})
public final class ClassListStringConverter {
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.TypeConverter()
    public final java.lang.String strListToString(@org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> intList) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.TypeConverter()
    public final java.util.List<java.lang.String> stringToStrList(@org.jetbrains.annotations.Nullable()
    java.lang.String string) {
        return null;
    }
    
    public ClassListStringConverter() {
        super();
    }
}