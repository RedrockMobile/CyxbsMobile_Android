package com.cyxbsmobile_single.module_todo.viewmodel;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:16
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000b2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00150\u0018JP\u0010\u0019\u001a\u00020\u00152\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00150\u00182:\b\u0002\u0010\u001b\u001a4\u0012\u0013\u0012\u00110\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0013\u0012\u00110\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\u0015\u0018\u00010\u001cR!\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R!\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\t\u001a\u0004\b\f\u0010\u0007R!\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0010\u0010\t\u001a\u0004\b\u000f\u0010\u0007R!\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0013\u0010\t\u001a\u0004\b\u0012\u0010\u0007\u00a8\u0006\""}, d2 = {"Lcom/cyxbsmobile_single/module_todo/viewmodel/TodoViewModel;", "Lcom/mredrock/cyxbs/common/viewmodel/BaseViewModel;", "()V", "checkedTodoList", "Ljava/util/ArrayList;", "Lcom/cyxbsmobile_single/module_todo/model/bean/TodoItemWrapper;", "getCheckedTodoList", "()Ljava/util/ArrayList;", "checkedTodoList$delegate", "Lkotlin/Lazy;", "todoList", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "getTodoList", "todoList$delegate", "uncheckTodoList", "getUncheckTodoList", "uncheckTodoList$delegate", "wrapperList", "getWrapperList", "wrapperList$delegate", "addTodo", "", "todo", "onSuccess", "Lkotlin/Function0;", "initDataList", "onLoadSuccess", "onConflict", "Lkotlin/Function2;", "", "Lkotlin/ParameterName;", "name", "remoteSyncTime", "localSyncTime", "module_todo_release"})
public final class TodoViewModel extends com.mredrock.cyxbs.common.viewmodel.BaseViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy uncheckTodoList$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy checkedTodoList$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy wrapperList$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy todoList$delegate = null;
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> getUncheckTodoList() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> getCheckedTodoList() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> getWrapperList() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.Todo> getTodoList() {
        return null;
    }
    
    public final void initDataList(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLoadSuccess, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function2<? super java.lang.Long, ? super java.lang.Long, kotlin.Unit> onConflict) {
    }
    
    public final void addTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public TodoViewModel() {
        super();
    }
}