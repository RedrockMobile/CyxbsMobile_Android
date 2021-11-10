package com.cyxbsmobile_single.module_todo.ui.activity;

import java.lang.System;

@com.alibaba.android.arouter.facade.annotation.Route(path = "/todo/todo_detail/entry")
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u00192\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0019B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\b\u0010\u0012\u001a\u00020\u000fH\u0002J\b\u0010\u0013\u001a\u00020\u000fH\u0002J\b\u0010\u0014\u001a\u00020\u000fH\u0016J\u0012\u0010\u0015\u001a\u00020\u000f2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0014J\b\u0010\u0018\u001a\u00020\u000fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u00020\tX\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\r\u00a8\u0006\u001a"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/ui/activity/TodoDetailActivity;", "Lcom/mredrock/cyxbs/common/ui/BaseViewModelActivity;", "Lcom/cyxbsmobile_single/module_todo/viewmodel/TodoDetailViewModel;", "()V", "backTime", "", "repeatAdapter", "Lcom/cyxbsmobile_single/module_todo/adapter/RepeatInnerAdapter;", "todo", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "getTodo", "()Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "setTodo", "(Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;)V", "changeModifyStatus", "", "isChanged", "", "initClick", "initView", "onBackPressed", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setCheckedStatus", "Companion", "module_todo_release"})
public final class TodoDetailActivity extends com.mredrock.cyxbs.common.ui.BaseViewModelActivity<com.cyxbsmobile_single.module_todo.viewmodel.TodoDetailViewModel> {
    @org.jetbrains.annotations.NotNull()
    public com.cyxbsmobile_single.module_todo.model.bean.Todo todo;
    private com.cyxbsmobile_single.module_todo.adapter.RepeatInnerAdapter repeatAdapter;
    private int backTime = 2;
    public static final com.cyxbsmobile_single.module_todo.ui.activity.TodoDetailActivity.Companion Companion = null;
    private java.util.HashMap _$_findViewCache;
    
    @org.jetbrains.annotations.NotNull()
    public final com.cyxbsmobile_single.module_todo.model.bean.Todo getTodo() {
        return null;
    }
    
    public final void setTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initView() {
    }
    
    private final void initClick() {
    }
    
    private final void changeModifyStatus() {
    }
    
    private final void changeModifyStatus(boolean isChanged) {
    }
    
    @java.lang.Override()
    public void onBackPressed() {
    }
    
    private final void setCheckedStatus() {
    }
    
    public TodoDetailActivity() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b\u00a8\u0006\t"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/ui/activity/TodoDetailActivity$Companion;", "", "()V", "startActivity", "", "todo", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "context", "Landroid/content/Context;", "module_todo_release"})
    public static final class Companion {
        
        public final void startActivity(@org.jetbrains.annotations.NotNull()
        com.cyxbsmobile_single.module_todo.model.bean.Todo todo, @org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
        
        private Companion() {
            super();
        }
    }
}