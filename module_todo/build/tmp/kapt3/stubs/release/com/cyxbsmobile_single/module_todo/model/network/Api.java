package com.cyxbsmobile_single.module_todo.model.network;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-24 11:33
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u0007H\'J\u001e\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\t\u001a\u00020\nH\'J\u001e\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\f\u001a\u00020\rH\'J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00040\u0003H\'J\u001e\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00040\u00032\b\b\u0001\u0010\u0012\u001a\u00020\nH\'\u00a8\u0006\u0013"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/network/Api;", "", "delTodo", "Lio/reactivex/Observable;", "Lcom/mredrock/cyxbs/common/bean/RedrockApiWrapper;", "Lcom/cyxbsmobile_single/module_todo/model/bean/SyncTime;", "delPushWrapper", "Lcom/cyxbsmobile_single/module_todo/model/bean/DelPushWrapper;", "getLastSyncTime", "sync_time", "", "pushTodo", "pushWrapper", "Lcom/cyxbsmobile_single/module_todo/model/bean/TodoListPushWrapper;", "queryAllTodo", "Lcom/cyxbsmobile_single/module_todo/model/bean/TodoListSyncTimeWrapper;", "queryChangedTodo", "Lcom/cyxbsmobile_single/module_todo/model/bean/TodoListGetWrapper;", "syncTime", "module_todo_release"})
public abstract interface Api {
    
    /**
     * 从数据库获取全部todo
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.GET(value = "/magipoke-todo/list")
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<com.cyxbsmobile_single.module_todo.model.bean.TodoListSyncTimeWrapper>> queryAllTodo();
    
    /**
     * 获取自上次同步到现在之间修改的所有todo
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.GET(value = "/magipoke-todo/todos")
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<com.cyxbsmobile_single.module_todo.model.bean.TodoListGetWrapper>> queryChangedTodo(@retrofit2.http.Query(value = "sync_time")
    long syncTime);
    
    /**
     * 上传todo到数据库
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.POST(value = "/magipoke-todo/batch-create")
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<com.cyxbsmobile_single.module_todo.model.bean.SyncTime>> pushTodo(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.cyxbsmobile_single.module_todo.model.bean.TodoListPushWrapper pushWrapper);
    
    /**
     * 获取最后修改的时间戳
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.GET(value = "/magipoke-todo/sync-time")
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<com.cyxbsmobile_single.module_todo.model.bean.SyncTime>> getLastSyncTime(@retrofit2.http.Query(value = "sync_time")
    long sync_time);
    
    /**
     * 删除todo
     */
    @org.jetbrains.annotations.NotNull()
    @retrofit2.http.HTTP(method = "DELETE", path = "/magipoke-todo/todos", hasBody = true)
    public abstract io.reactivex.Observable<com.mredrock.cyxbs.common.bean.RedrockApiWrapper<com.cyxbsmobile_single.module_todo.model.bean.SyncTime>> delTodo(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.cyxbsmobile_single.module_todo.model.bean.DelPushWrapper delPushWrapper);
}