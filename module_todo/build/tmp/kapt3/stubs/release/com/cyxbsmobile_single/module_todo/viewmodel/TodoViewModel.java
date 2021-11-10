package com.cyxbsmobile_single.module_todo.viewmodel;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:16
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000f2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00190\u001cJ\u0014\u0010\u001d\u001a\u00020\u00192\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00190\u001cR!\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R!\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\t\u001a\u0004\b\f\u0010\u0007R!\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\t\u001a\u0004\b\u0010\u0010\u0007R!\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0014\u0010\t\u001a\u0004\b\u0013\u0010\u0007R!\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0017\u0010\t\u001a\u0004\b\u0016\u0010\u0007\u00a8\u0006\u001f"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/viewmodel/TodoViewModel;", "Lcom/mredrock/cyxbs/common/viewmodel/BaseViewModel;", "()V", "checkedTodoList", "Ljava/util/ArrayList;", "Lcom/cyxbsmobile_single/module_todo/model/bean/TodoItemWrapper;", "getCheckedTodoList", "()Ljava/util/ArrayList;", "checkedTodoList$delegate", "Lkotlin/Lazy;", "repeatList", "Lcom/cyxbsmobile_single/module_todo/model/bean/RepeatBean;", "getRepeatList", "repeatList$delegate", "todoList", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "getTodoList", "todoList$delegate", "uncheckTodoList", "getUncheckTodoList", "uncheckTodoList$delegate", "wrapperList", "getWrapperList", "wrapperList$delegate", "addTodo", "", "todo", "onSuccess", "Lkotlin/Function0;", "initDataList", "onLoadSuccess", "module_todo_release"})
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
    private final kotlin.Lazy repeatList$delegate = null;
    
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.RepeatBean> getRepeatList() {
        return null;
    }
    
    public final void initDataList(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLoadSuccess) {
    }
    
    public final void addTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public TodoViewModel() {
        super();
    }
}