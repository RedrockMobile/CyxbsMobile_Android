package com.mredrock.cyxbs.course.ui;

import java.lang.System;

/**
 * @author Jovines
 * @date 2020/3/20 16:27
 * description：事务Activity的Transition动画的逻辑代码,
 *             以此来使activity主要逻辑代码显得更加清晰，让activity专心处理逻辑代码
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u000f\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0005\u001a\u00020\u0006J\u0006\u0010\u0007\u001a\u00020\u0006J\u0006\u0010\b\u001a\u00020\u0006J\u0006\u0010\t\u001a\u00020\u0006J\u0006\u0010\n\u001a\u00020\u0006J\u0006\u0010\u000b\u001a\u00020\u0006R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/mredrock/cyxbs/course/ui/AffairTransitionAnimHelper;", "", "affairEditActivity", "Lcom/mredrock/cyxbs/course/ui/activity/AffairEditActivity;", "(Lcom/mredrock/cyxbs/course/ui/activity/AffairEditActivity;)V", "addContentNextMonitor", "", "addTitleNextMonitor", "backAddContentMonitor", "backAddTitleMonitor", "modifyPageLayout", "unBindActivity", "module_course_debug"})
public final class AffairTransitionAnimHelper {
    private com.mredrock.cyxbs.course.ui.activity.AffairEditActivity affairEditActivity;
    
    /**
     * 添加标题之后跳转到添加内容动画
     */
    public final void addTitleNextMonitor() {
    }
    
    /**
     * 返回到添加标题的动画
     */
    public final void backAddTitleMonitor() {
    }
    
    /**
     * 添加内容之后跳转到选择时间动画
     */
    public final void addContentNextMonitor() {
    }
    
    /**
     * 返回到添加内容的动画
     */
    public final void backAddContentMonitor() {
    }
    
    /**
     * 如果是修改事务，这个方法用于将此activity转换到最后一个状态
     */
    public final void modifyPageLayout() {
    }
    
    public final void unBindActivity() {
    }
    
    public AffairTransitionAnimHelper(@org.jetbrains.annotations.Nullable()
    com.mredrock.cyxbs.course.ui.activity.AffairEditActivity affairEditActivity) {
        super();
    }
}