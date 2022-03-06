package com.cyxbsmobile_single.module_todo.ui.fragment;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-02 10:15
 */
@com.alibaba.android.arouter.facade.annotation.Route(path = "/todo/discover/feed")
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\u001a\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0016J\b\u0010\u0017\u001a\u00020\u0011H\u0002R\u001a\u0010\u0004\u001a\u00020\u0005X\u0094\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001b\u0010\n\u001a\u00020\u000b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0018"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/ui/fragment/TodoFeedFragment;", "Lcom/mredrock/cyxbs/common/ui/BaseFeedFragment;", "Lcom/cyxbsmobile_single/module_todo/viewmodel/TodoViewModel;", "()V", "hasTopSplitLine", "", "getHasTopSplitLine", "()Z", "setHasTopSplitLine", "(Z)V", "todoAdapter", "Lcom/cyxbsmobile_single/module_todo/adapter/TodoFeedAdapter;", "getTodoAdapter", "()Lcom/cyxbsmobile_single/module_todo/adapter/TodoFeedAdapter;", "todoAdapter$delegate", "Lkotlin/Lazy;", "onRefresh", "", "onViewCreated", "view", "Landroid/view/View;", "savedInstanceState", "Landroid/os/Bundle;", "refresh", "module_todo_release"})
public final class TodoFeedFragment extends com.mredrock.cyxbs.common.ui.BaseFeedFragment<com.cyxbsmobile_single.module_todo.viewmodel.TodoViewModel> {
    private boolean hasTopSplitLine = false;
    private final kotlin.Lazy todoAdapter$delegate = null;
    private java.util.HashMap _$_findViewCache;
    
    @java.lang.Override()
    protected boolean getHasTopSplitLine() {
        return false;
    }
    
    @java.lang.Override()
    protected void setHasTopSplitLine(boolean p0) {
    }
    
    private final com.cyxbsmobile_single.module_todo.adapter.TodoFeedAdapter getTodoAdapter() {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void refresh() {
    }
    
    @java.lang.Override()
    public void onRefresh() {
    }
    
    public TodoFeedFragment() {
        super();
    }
}