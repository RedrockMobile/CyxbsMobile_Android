package com.cyxbsmobile_single.module_todo.adapter;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-09-17 9:08
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B@\u0012\u0016\u0010\u0002\u001a\u0012\u0012\u0004\u0012\u00020\u00040\u0003j\b\u0012\u0004\u0012\u00020\u0004`\u0005\u0012!\u0010\u0006\u001a\u001d\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0007\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u0015\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\bH\u0016R\u001a\u0010\u000e\u001a\u00020\bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R)\u0010\u0006\u001a\u001d\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R!\u0010\u0002\u001a\u0012\u0012\u0004\u0012\u00020\u00040\u0003j\b\u0012\u0004\u0012\u00020\u0004`\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u0019"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/adapter/ChooseYearAdapter;", "Lcom/cyxbsmobile_single/module_todo/adapter/SimpleTextAdapter;", "stringArray", "Ljava/util/ArrayList;", "", "Lkotlin/collections/ArrayList;", "onItemClick", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "year", "", "(Ljava/util/ArrayList;Lkotlin/jvm/functions/Function1;)V", "curSelPosition", "getCurSelPosition", "()I", "setCurSelPosition", "(I)V", "getStringArray", "()Ljava/util/ArrayList;", "onBindViewHolder", "holder", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "position", "module_todo_release"})
public final class ChooseYearAdapter extends com.cyxbsmobile_single.module_todo.adapter.SimpleTextAdapter {
    private int curSelPosition = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.ArrayList<java.lang.String> stringArray = null;
    private final kotlin.jvm.functions.Function1<java.lang.Integer, kotlin.Unit> onItemClick = null;
    
    public final int getCurSelPosition() {
        return 0;
    }
    
    public final void setCurSelPosition(int p0) {
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<java.lang.String> getStringArray() {
        return null;
    }
    
    public ChooseYearAdapter(@org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.String> stringArray, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onItemClick) {
        super(null, 0);
    }
}