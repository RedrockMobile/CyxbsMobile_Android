package com.cyxbsmobile_single.module_todo.adapter;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-08-02 11:27
 * 双列表可折叠RecyclerView
 */
@kotlin.Suppress(names = {"UNCHECKED_CAST"})
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 K2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0003KLMB-\u0012\u0016\u0010\u0003\u001a\u0012\u0012\u0004\u0012\u00020\u00050\u0004j\b\u0012\u0004\u0012\u00020\u0005`\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\u000e\u00101\u001a\u00020$2\u0006\u00102\u001a\u000203J\u000e\u00104\u001a\u00020$2\u0006\u00105\u001a\u00020\u001dJ\u0010\u00106\u001a\u00020$2\u0006\u00107\u001a\u00020\u0017H\u0002J\u000e\u00108\u001a\u00020$2\u0006\u00109\u001a\u00020\u0005J\u000e\u0010:\u001a\u00020$2\u0006\u0010#\u001a\u00020\u0005J\u0010\u0010;\u001a\u00020$2\u0006\u0010<\u001a\u00020\u0002H\u0002J\u000e\u0010=\u001a\u00020$2\u0006\u0010#\u001a\u00020\u0005J\b\u0010>\u001a\u00020$H\u0002J\b\u0010?\u001a\u00020\nH\u0016J\u0010\u0010@\u001a\u00020\n2\u0006\u0010!\u001a\u00020\nH\u0016J\u0010\u0010A\u001a\u00020$2\u0006\u0010 \u001a\u00020\u001dH\u0002J\u0018\u0010B\u001a\u00020$2\u0006\u0010C\u001a\u00020\u00022\u0006\u0010!\u001a\u00020\nH\u0016J\u0018\u0010D\u001a\u00020\u00022\u0006\u0010E\u001a\u00020F2\u0006\u0010\"\u001a\u00020\nH\u0016J\b\u0010G\u001a\u00020$H\u0002J\b\u0010H\u001a\u00020$H\u0002J\u0010\u0010I\u001a\u00020$2\u0006\u0010 \u001a\u00020\u001dH\u0002J\u0012\u0010J\u001a\u00020$2\b\u00102\u001a\u0004\u0018\u000103H\u0002R!\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0011\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0012\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0010\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000Rt\u0010\u001b\u001a\\\u0012\u0013\u0012\u00110\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(!\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(\"\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(#\u0012\u0004\u0012\u00020$0\u001cX\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b\'\u0010(R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0003\u001a\u0012\u0012\u0004\u0012\u00020\u00050\u0004j\b\u0012\u0004\u0012\u00020\u0005`\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R!\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b,\u0010\u0010\u001a\u0004\b+\u0010\u000eR\u001b\u0010-\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b/\u0010\u0010\u001a\u0004\b.\u0010\u0014R\u001e\u00100\u001a\u0012\u0012\u0004\u0012\u00020\u00050\u0004j\b\u0012\u0004\u0012\u00020\u0005`\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006N"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/adapter/DoubleListFoldRvAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "todoItemWrapperArrayList", "Ljava/util/ArrayList;", "Lcom/cyxbsmobile_single/module_todo/model/bean/TodoItemWrapper;", "Lkotlin/collections/ArrayList;", "showType", "Lcom/cyxbsmobile_single/module_todo/adapter/DoubleListFoldRvAdapter$ShowType;", "itemRes", "", "(Ljava/util/ArrayList;Lcom/cyxbsmobile_single/module_todo/adapter/DoubleListFoldRvAdapter$ShowType;I)V", "checkedArray", "getCheckedArray", "()Ljava/util/ArrayList;", "checkedArray$delegate", "Lkotlin/Lazy;", "checkedTopMark", "downEmptyHolder", "getDownEmptyHolder", "()Lcom/cyxbsmobile_single/module_todo/model/bean/TodoItemWrapper;", "downEmptyHolder$delegate", "isAddedDownEmpty", "", "isAddedUpEmpty", "isFirstTimeGetItemCount", "isShowItem", "onBindView", "Lkotlin/Function4;", "Landroid/view/View;", "Lkotlin/ParameterName;", "name", "itemView", "position", "viewType", "wrapper", "", "getOnBindView", "()Lkotlin/jvm/functions/Function4;", "setOnBindView", "(Lkotlin/jvm/functions/Function4;)V", "uncheckColor", "uncheckedArray", "getUncheckedArray", "uncheckedArray$delegate", "upEmptyHolder", "getUpEmptyHolder", "upEmptyHolder$delegate", "wrapperCopyList", "addTodo", "todo", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "changeFoldStatus", "hideIcon", "checkEmptyItem", "needShow", "checkItemAndPopUp", "wrapperTodo", "checkItemAndSwap", "clearView", "viewHolder", "delItem", "foldItem", "getItemCount", "getItemViewType", "hideNotifyTime", "onBindViewHolder", "holder", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "refreshList", "showItem", "showNotifyTime", "updateTodo", "Companion", "DiffCallBack", "ShowType", "module_todo_release"})
public final class DoubleListFoldRvAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> {
    private final kotlin.Lazy uncheckedArray$delegate = null;
    private final kotlin.Lazy checkedArray$delegate = null;
    private boolean isFirstTimeGetItemCount = true;
    private int checkedTopMark = 0;
    private boolean isShowItem = true;
    private boolean isAddedUpEmpty = false;
    private boolean isAddedDownEmpty = false;
    private final kotlin.Lazy upEmptyHolder$delegate = null;
    private final kotlin.Lazy downEmptyHolder$delegate = null;
    private int uncheckColor = 0;
    private java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> wrapperCopyList;
    @org.jetbrains.annotations.NotNull()
    public kotlin.jvm.functions.Function4<? super android.view.View, ? super java.lang.Integer, ? super java.lang.Integer, ? super com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper, kotlin.Unit> onBindView;
    private final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> todoItemWrapperArrayList = null;
    private final com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.ShowType showType = null;
    private final int itemRes = 0;
    public static final int TODO = 0;
    public static final int TITLE = 1;
    public static final int EMPTY = 2;
    public static final com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.Companion Companion = null;
    
    private final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> getUncheckedArray() {
        return null;
    }
    
    private final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> getCheckedArray() {
        return null;
    }
    
    private final com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper getUpEmptyHolder() {
        return null;
    }
    
    private final com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper getDownEmptyHolder() {
        return null;
    }
    
    @java.lang.Override()
    public int getItemViewType(int position) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function4<android.view.View, java.lang.Integer, java.lang.Integer, com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper, kotlin.Unit> getOnBindView() {
        return null;
    }
    
    public final void setOnBindView(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function4<? super android.view.View, ? super java.lang.Integer, ? super java.lang.Integer, ? super com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper, kotlin.Unit> p0) {
    }
    
    public final void checkItemAndSwap(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper wrapper) {
    }
    
    public final void checkItemAndPopUp(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper wrapperTodo) {
    }
    
    public final void delItem(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper wrapper) {
    }
    
    private final void foldItem() {
    }
    
    private final void showItem() {
    }
    
    private final void checkEmptyItem(boolean needShow) {
    }
    
    public final void changeFoldStatus(@org.jetbrains.annotations.NotNull()
    android.view.View hideIcon) {
    }
    
    public final void addTodo(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo) {
    }
    
    private final void refreshList() {
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {
    }
    
    private final void clearView(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
    }
    
    private final void updateTodo(com.cyxbsmobile_single.module_todo.model.bean.Todo todo) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    private final void hideNotifyTime(android.view.View itemView) {
    }
    
    private final void showNotifyTime(android.view.View itemView) {
    }
    
    public DoubleListFoldRvAdapter(@org.jetbrains.annotations.NotNull()
    java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> todoItemWrapperArrayList, @org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.adapter.DoubleListFoldRvAdapter.ShowType showType, int itemRes) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/adapter/DoubleListFoldRvAdapter$ShowType;", "", "(Ljava/lang/String;I)V", "NORMAL", "THREE", "module_todo_release"})
    public static enum ShowType {
        /*public static final*/ NORMAL /* = new NORMAL() */,
        /*public static final*/ THREE /* = new THREE() */;
        
        ShowType() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u00020\u0001B5\u0012\u0016\u0010\u0002\u001a\u0012\u0012\u0004\u0012\u00020\u00040\u0003j\b\u0012\u0004\u0012\u00020\u0004`\u0005\u0012\u0016\u0010\u0006\u001a\u0012\u0012\u0004\u0012\u00020\u00040\u0003j\b\u0012\u0004\u0012\u00020\u0004`\u0005\u00a2\u0006\u0002\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0016J\u0018\u0010\r\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0016J\b\u0010\u000e\u001a\u00020\u000bH\u0016J\b\u0010\u000f\u001a\u00020\u000bH\u0016R\u001e\u0010\u0006\u001a\u0012\u0012\u0004\u0012\u00020\u00040\u0003j\b\u0012\u0004\u0012\u00020\u0004`\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0002\u001a\u0012\u0012\u0004\u0012\u00020\u00040\u0003j\b\u0012\u0004\u0012\u00020\u0004`\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/adapter/DoubleListFoldRvAdapter$DiffCallBack;", "Landroidx/recyclerview/widget/DiffUtil$Callback;", "oldList", "Ljava/util/ArrayList;", "Lcom/cyxbsmobile_single/module_todo/model/bean/TodoItemWrapper;", "Lkotlin/collections/ArrayList;", "newList", "(Lcom/cyxbsmobile_single/module_todo/adapter/DoubleListFoldRvAdapter;Ljava/util/ArrayList;Ljava/util/ArrayList;)V", "areContentsTheSame", "", "oldItemPosition", "", "newItemPosition", "areItemsTheSame", "getNewListSize", "getOldListSize", "module_todo_release"})
    public final class DiffCallBack extends androidx.recyclerview.widget.DiffUtil.Callback {
        private final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> oldList = null;
        private final java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> newList = null;
        
        @java.lang.Override()
        public int getOldListSize() {
            return 0;
        }
        
        @java.lang.Override()
        public int getNewListSize() {
            return 0;
        }
        
        @java.lang.Override()
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
        
        @java.lang.Override()
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
        
        public DiffCallBack(@org.jetbrains.annotations.NotNull()
        java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> oldList, @org.jetbrains.annotations.NotNull()
        java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.TodoItemWrapper> newList) {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/adapter/DoubleListFoldRvAdapter$Companion;", "", "()V", "EMPTY", "", "TITLE", "TODO", "module_todo_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}