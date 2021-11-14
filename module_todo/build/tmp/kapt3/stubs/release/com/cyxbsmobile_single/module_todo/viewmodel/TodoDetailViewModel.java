package com.cyxbsmobile_single.module_todo.viewmodel;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-24 6:47
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\t2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0012J\u000e\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\tJ\u001c\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\t2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0012R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0003\u0010\u0005\"\u0004\b\u0006\u0010\u0007R\u001c\u0010\b\u001a\u0004\u0018\u00010\tX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\r\u00a8\u0006\u0016"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/viewmodel/TodoDetailViewModel;", "Lcom/mredrock/cyxbs/common/viewmodel/BaseViewModel;", "()V", "isChanged", "", "()Z", "setChanged", "(Z)V", "rawTodo", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "getRawTodo", "()Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "setRawTodo", "(Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;)V", "delTodo", "", "todo", "onSuccess", "Lkotlin/Function0;", "judgeChange", "todoAfterChange", "updateTodo", "module_todo_release"})
public final class TodoDetailViewModel extends com.mredrock.cyxbs.common.viewmodel.BaseViewModel {
    @org.jetbrains.annotations.Nullable()
    private com.cyxbsmobile_single.module_todo.model.bean.Todo rawTodo;
    private boolean isChanged = false;
    
    @org.jetbrains.annotations.Nullable()
    public final com.cyxbsmobile_single.module_todo.model.bean.Todo getRawTodo() {
        return null;
    }
    
    public final void setRawTodo(@org.jetbrains.annotations.Nullable()
    com.cyxbsmobile_single.module_todo.model.bean.Todo p0) {
    }
    
    public final boolean isChanged() {
        return false;
    }
    
    public final void setChanged(boolean p0) {
    }
    
    public final void updateTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final boolean judgeChange(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todoAfterChange) {
        return false;
    }
    
    public final void delTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public TodoDetailViewModel() {
        super();
    }
}