package com.mredrock.cyxbs.course.adapters;

import java.lang.System;

/**
 * 这个类作为所有RecyclerView的Adapter的基类。然后通过[getThePositionLayoutId]来获取对应位置的View的LayoutId
 * 所有有View的操作逻辑放在[onBindViewHolder]中，这样可以不用自己重写ViewHolder了。当有多个LayoutId的时候，
 * 你只需要在[onBindViewHolder]中进行不同数据类型的判断然后进行不同操作就行。
 *
 * Created by anriku on 2018/9/10.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0016B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\b\u0010\n\u001a\u00020\u000bH&J\u0010\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000bH\u0016J\u0010\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000bH\'J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000bH&J\u0018\u0010\u0012\u001a\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000bH\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0084\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/BaseRecAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/mredrock/cyxbs/course/adapters/BaseRecAdapter$BaseViewHolder;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getContext", "()Landroid/content/Context;", "inflater", "Landroid/view/LayoutInflater;", "getItemCount", "", "getItemViewType", "position", "getThePositionLayoutId", "onBindViewHolder", "", "holder", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "BaseViewHolder", "module_course_debug"})
public abstract class BaseRecAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.mredrock.cyxbs.course.adapters.BaseRecAdapter.BaseViewHolder> {
    private final android.view.LayoutInflater inflater = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.mredrock.cyxbs.course.adapters.BaseRecAdapter.BaseViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public int getItemViewType(int position) {
        return 0;
    }
    
    /**
     * @return 返回对应位置View的LayoutId
     */
    @androidx.annotation.LayoutRes()
    public abstract int getThePositionLayoutId(int position);
    
    @java.lang.Override()
    public abstract int getItemCount();
    
    @java.lang.Override()
    public abstract void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.mredrock.cyxbs.course.adapters.BaseRecAdapter.BaseViewHolder holder, int position);
    
    @org.jetbrains.annotations.NotNull()
    protected final android.content.Context getContext() {
        return null;
    }
    
    public BaseRecAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/mredrock/cyxbs/course/adapters/BaseRecAdapter$BaseViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "(Landroid/view/View;)V", "module_course_debug"})
    public static final class BaseViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        
        public BaseViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View itemView) {
            super(null);
        }
    }
}