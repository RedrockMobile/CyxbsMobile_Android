package com.cyxbsmobile_single.module_todo.model;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:08
 * Describe: Todo模块封装的用于维持本地和远程数据库同步的module
 * 多设备同步逻辑->
 * 最初功能设计的时候，提出了多设备同步的问题，主体处理逻辑如下
 * do 拉取todo
 *     if 本地同步时间和远程修改时间不相等
 *         if 本地存在离线修改
 *             多设备存档冲突，需要向用户展示，让用户选择冲突存档中的某一个
 *         else 本地不存在离线修改
 *             下载远程修改，同步到本地数据库，并更新本地的同步时间
 *     else 修改时间相同
 *         两端数据完全同步，不需要做出任何拉取
 * done
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\t\u0018\u0000 52\u00020\u0001:\u000256B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J1\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u00052!\u0010\u0012\u001a\u001d\u0012\u0013\u0012\u00110\r\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u00020\u000b0\u0013J\u001c\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\r2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0018J\u0014\u0010\u0019\u001a\u00020\u000b2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0018J9\u0010\u001a\u001a\u00020\u000b2\b\b\u0002\u0010\u001b\u001a\u00020\u001c2\'\u0010\u0012\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u00050\u001d\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0003\u0012\u0004\u0012\u00020\u000b0\u0013J{\u0010\u001e\u001a\u00020\u000b2\'\u0010\u0012\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u00050\u001d\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0003\u0012\u0004\u0012\u00020\u000b0\u00132\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00182:\b\u0002\u0010 \u001a4\u0012\u0013\u0012\u00110\r\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\"\u0012\u0013\u0012\u00110\r\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(#\u0012\u0004\u0012\u00020\u000b\u0018\u00010!H\u0002J\b\u0010$\u001a\u00020\rH\u0002J\b\u0010%\u001a\u00020\rH\u0002J\u0016\u0010&\u001a\b\u0012\u0004\u0012\u00020\r0\u001d2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J?\u0010\'\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\r2!\u0010\u0012\u001a\u001d\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0011\u0012\u0004\u0012\u00020\u000b0\u00132\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0018J=\u0010(\u001a\u00020\u000b2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\r0\u001d2\'\u0010\u0012\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u00050\u001d\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0011\u0012\u0004\u0012\u00020\u000b0\u0013Jk\u0010\u0006\u001a\u00020\u000b2\'\u0010\u0012\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u00050\u001d\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0003\u0012\u0004\u0012\u00020\u000b0\u00132:\b\u0002\u0010 \u001a4\u0012\u0013\u0012\u00110\r\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(*\u0012\u0013\u0012\u00110\r\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(+\u0012\u0004\u0012\u00020\u000b\u0018\u00010!J7\u0010,\u001a\u00020\u000b2\u0006\u0010-\u001a\u00020.2\'\u0010\u0012\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u00050\u001d\u00a2\u0006\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0003\u0012\u0004\u0012\u00020\u000b0\u0013J\b\u0010/\u001a\u00020\u000bH\u0002J\u0010\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\rH\u0002J\u0010\u00102\u001a\u00020\u000b2\u0006\u00103\u001a\u00020\rH\u0002J \u00104\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u00052\u0010\b\u0002\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u0018R!\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007\u00a8\u00067"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/TodoModel;", "", "()V", "todoList", "Ljava/util/ArrayList;", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "getTodoList", "()Ljava/util/ArrayList;", "todoList$delegate", "Lkotlin/Lazy;", "addOffLineModifyTodo", "", "id", "", "type", "Lcom/cyxbsmobile_single/module_todo/model/TodoModel$ModifyType;", "addTodo", "todo", "onSuccess", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "name", "todoId", "delTodo", "Lkotlin/Function0;", "forcePush", "getAllTodoFromNet", "withDatabaseSync", "", "", "getFromNet", "onError", "onConflict", "Lkotlin/Function2;", "remoteTime", "localTime", "getLastModifyTime", "getLastSyncTime", "getOfflineModifyTodo", "getTodoById", "getTodoByIdList", "idList", "remoteSyncTime", "localSyncTime", "queryByIsDone", "isDone", "", "resendModifyList", "setLastModifyTime", "modifyTime", "setLastSyncTime", "syncTime", "updateTodo", "Companion", "ModifyType", "module_todo_debug"})
public final class TodoModel {
    private final kotlin.Lazy todoList$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy INSTANCE$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy apiGenerator$delegate = null;
    public static final com.cyxbsmobile_single.module_todo.model.TodoModel.Companion Companion = null;
    
    private final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.Todo> getTodoList() {
        return null;
    }
    
    public final void getTodoList(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo>, kotlin.Unit> onSuccess, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function2<? super java.lang.Long, ? super java.lang.Long, kotlin.Unit> onConflict) {
    }
    
    public final void updateTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final void delTodo(long todoId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final void getTodoById(long todoId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.cyxbsmobile_single.module_todo.model.bean.Todo, kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onError) {
    }
    
    public final void getTodoByIdList(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> idList, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo>, kotlin.Unit> onSuccess) {
    }
    
    public final void addTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onSuccess) {
    }
    
    private final java.util.List<java.lang.Long> getOfflineModifyTodo(com.cyxbsmobile_single.module_todo.model.TodoModel.ModifyType type) {
        return null;
    }
    
    /**
     * 用于记录离线修改的方法
     * @param id: 被操作todo的id，这里约定，如果id为-1，则表示对type类型进行删除
     * @param type: 操作的类型，DEL表示删除类型，CHANGE表示修改对应类型
     */
    private final void addOffLineModifyTodo(long id, com.cyxbsmobile_single.module_todo.model.TodoModel.ModifyType type) {
    }
    
    private final long getLastModifyTime() {
        return 0L;
    }
    
    private final long getLastSyncTime() {
        return 0L;
    }
    
    private final void setLastModifyTime(long modifyTime) {
    }
    
    private final void setLastSyncTime(long syncTime) {
    }
    
    public final void queryByIsDone(int isDone, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo>, kotlin.Unit> onSuccess) {
    }
    
    private final void getFromNet(kotlin.jvm.functions.Function1<? super java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo>, kotlin.Unit> onSuccess, kotlin.jvm.functions.Function0<kotlin.Unit> onError, kotlin.jvm.functions.Function2<? super java.lang.Long, ? super java.lang.Long, kotlin.Unit> onConflict) {
    }
    
    public final void getAllTodoFromNet(boolean withDatabaseSync, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.cyxbsmobile_single.module_todo.model.bean.Todo>, kotlin.Unit> onSuccess) {
    }
    
    private final void resendModifyList() {
    }
    
    public final void forcePush(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public TodoModel() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/TodoModel$ModifyType;", "", "(Ljava/lang/String;I)V", "CHANGE", "DEL", "module_todo_debug"})
    public static enum ModifyType {
        /*public static final*/ CHANGE /* = new CHANGE() */,
        /*public static final*/ DEL /* = new DEL() */;
        
        ModifyType() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u000e"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/model/TodoModel$Companion;", "", "()V", "INSTANCE", "Lcom/cyxbsmobile_single/module_todo/model/TodoModel;", "getINSTANCE", "()Lcom/cyxbsmobile_single/module_todo/model/TodoModel;", "INSTANCE$delegate", "Lkotlin/Lazy;", "apiGenerator", "Lcom/cyxbsmobile_single/module_todo/model/network/Api;", "getApiGenerator", "()Lcom/cyxbsmobile_single/module_todo/model/network/Api;", "apiGenerator$delegate", "module_todo_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final com.cyxbsmobile_single.module_todo.model.TodoModel getINSTANCE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cyxbsmobile_single.module_todo.model.network.Api getApiGenerator() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}