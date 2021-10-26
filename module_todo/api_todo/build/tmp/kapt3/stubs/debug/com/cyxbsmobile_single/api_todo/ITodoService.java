package com.cyxbsmobile_single.api_todo;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-09 15:49
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&\u00a8\u0006\u0004"}, d2 = {"Lcom/cyxbsmobile_single/api_todo/ITodoService;", "Lcom/alibaba/android/arouter/facade/template/IProvider;", "getTodoFeed", "Landroidx/fragment/app/Fragment;", "api_todo_debug"})
public abstract interface ITodoService extends com.alibaba.android.arouter.facade.template.IProvider {
    
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.fragment.app.Fragment getTodoFeed();
}