package com.cyxbsmobile_single.module_todo.service;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-09 15:58
 * Describe: 提供Feed的类
 */
@com.alibaba.android.arouter.facade.annotation.Route(path = "/todo/service", name = "/todo/service")
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\u0012\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016\u00a8\u0006\t"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/service/TodoService;", "Lcom/cyxbsmobile_single/api_todo/ITodoService;", "()V", "getTodoFeed", "Landroidx/fragment/app/Fragment;", "init", "", "context", "Landroid/content/Context;", "module_todo_debug"})
public final class TodoService implements com.cyxbsmobile_single.api_todo.ITodoService {
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public androidx.fragment.app.Fragment getTodoFeed() {
        return null;
    }
    
    @java.lang.Override()
    public void init(@org.jetbrains.annotations.Nullable()
    android.content.Context context) {
    }
    
    public TodoService() {
        super();
    }
}