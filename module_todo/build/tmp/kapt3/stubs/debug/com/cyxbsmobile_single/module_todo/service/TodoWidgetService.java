package com.cyxbsmobile_single.module_todo.service;

import java.lang.System;

/**
 * @date 2021-08-18
 * @author Sca RayleighZ
 * @describe 小组件中RemoteView的Adapter
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00072\u00020\u0001:\u0002\u0007\bB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0016\u00a8\u0006\t"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/service/TodoWidgetService;", "Landroid/widget/RemoteViewsService;", "()V", "onGetViewFactory", "Landroid/widget/RemoteViewsService$RemoteViewsFactory;", "intent", "Landroid/content/Intent;", "Companion", "TodoWidgetFactory", "module_todo_debug"})
public final class TodoWidgetService extends android.widget.RemoteViewsService {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList = null;
    public static final com.cyxbsmobile_single.module_todo.service.TodoWidgetService.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.widget.RemoteViewsService.RemoteViewsFactory onGetViewFactory(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    public TodoWidgetService() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H\u0016J\b\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010\r\u001a\u00020\f2\u0006\u0010\n\u001a\u00020\u0007H\u0016J\b\u0010\u000e\u001a\u00020\u0007H\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0012H\u0016J\b\u0010\u0014\u001a\u00020\u0012H\u0016R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/service/TodoWidgetService$TodoWidgetFactory;", "Landroid/widget/RemoteViewsService$RemoteViewsFactory;", "(Lcom/cyxbsmobile_single/module_todo/service/TodoWidgetService;)V", "todoList", "", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "getCount", "", "getItemId", "", "position", "getLoadingView", "Landroid/widget/RemoteViews;", "getViewAt", "getViewTypeCount", "hasStableIds", "", "onCreate", "", "onDataSetChanged", "onDestroy", "module_todo_debug"})
    public final class TodoWidgetFactory implements android.widget.RemoteViewsService.RemoteViewsFactory {
        private java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo> todoList;
        
        @java.lang.Override()
        public void onCreate() {
        }
        
        @java.lang.Override()
        public void onDataSetChanged() {
        }
        
        @java.lang.Override()
        public void onDestroy() {
        }
        
        @java.lang.Override()
        public int getCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public android.widget.RemoteViews getViewAt(int position) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public android.widget.RemoteViews getLoadingView() {
            return null;
        }
        
        @java.lang.Override()
        public int getViewTypeCount() {
            return 0;
        }
        
        @java.lang.Override()
        public long getItemId(int position) {
            return 0L;
        }
        
        @java.lang.Override()
        public boolean hasStableIds() {
            return false;
        }
        
        public TodoWidgetFactory() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R!\u0010\u0003\u001a\u0012\u0012\u0004\u0012\u00020\u00050\u0004j\b\u0012\u0004\u0012\u00020\u0005`\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/service/TodoWidgetService$Companion;", "", "()V", "todoList", "Ljava/util/ArrayList;", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "Lkotlin/collections/ArrayList;", "getTodoList", "()Ljava/util/ArrayList;", "module_todo_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.Todo> getTodoList() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}