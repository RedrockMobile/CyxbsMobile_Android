package com.cyxbsmobile_single.module_todo.ui.dialog;

import java.lang.System;

/**
 * Author: RayleighZ
 * Time: 2021-09-18 8:50
 * 增加todo用dialog
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001GB!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\u0010\bJ\b\u0010*\u001a\u00020\u0007H\u0002J\b\u0010+\u001a\u00020\u0007H\u0002J\b\u0010,\u001a\u00020\u0007H\u0002J\b\u0010-\u001a\u00020\u0007H\u0002J\b\u0010.\u001a\u00020\u0007H\u0002J\b\u0010/\u001a\u00020\u0007H\u0002J\b\u00100\u001a\u00020\u0007H\u0002J\b\u00101\u001a\u00020\u0007H\u0002J\b\u00102\u001a\u00020\u0007H\u0002J\b\u00103\u001a\u00020\u0007H\u0002J\b\u00104\u001a\u00020\u0007H\u0002J\b\u00105\u001a\u00020\u0007H\u0002J\u000e\u00106\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\u0006J\u000e\u00107\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\u0006J\u000e\u00108\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0010J\u0010\u00109\u001a\u00020\u00072\u0006\u0010:\u001a\u00020;H\u0016J\u0006\u0010<\u001a\u00020\u0007J\u0006\u0010=\u001a\u00020\u0007J\u001e\u0010>\u001a\u00020\u00072\u0006\u0010?\u001a\u00020\u00102\f\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00070AH\u0002J\f\u0010B\u001a\u00020C*\u00020DH\u0002J\f\u0010E\u001a\u00020\u0007*\u00020DH\u0002J\f\u0010F\u001a\u00020\u0007*\u00020DH\u0002R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000RA\u0010\u0011\u001a(\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\u0012j\u0018\u0012\u0014\u0012\u0012\u0012\u0004\u0012\u00020\u00130\u0012j\b\u0012\u0004\u0012\u00020\u0013`\u0014`\u00148BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0017\u0010\u000e\u001a\u0004\b\u0015\u0010\u0016R\'\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00190\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001c\u0010\u000e\u001a\u0004\b\u001b\u0010\u0016R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u001b\u0010!\u001a\u00020\"8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b%\u0010\u000e\u001a\u0004\b#\u0010$R\u001b\u0010&\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b)\u0010\u000e\u001a\u0004\b\'\u0010(\u00a8\u0006H"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/ui/dialog/AddItemDialog;", "Lcom/google/android/material/bottomsheet/BottomSheetDialog;", "context", "Landroid/content/Context;", "onConfirm", "Lkotlin/Function1;", "Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "", "(Landroid/content/Context;Lkotlin/jvm/functions/Function1;)V", "chooseYearAdapter", "Lcom/cyxbsmobile_single/module_todo/adapter/ChooseYearAdapter;", "getChooseYearAdapter", "()Lcom/cyxbsmobile_single/module_todo/adapter/ChooseYearAdapter;", "chooseYearAdapter$delegate", "Lkotlin/Lazy;", "curOperate", "Lcom/cyxbsmobile_single/module_todo/ui/dialog/AddItemDialog$CurOperate;", "dateBeenList", "Ljava/util/ArrayList;", "Lcom/cyxbsmobile_single/module_todo/model/bean/DateBeen;", "Lkotlin/collections/ArrayList;", "getDateBeenList", "()Ljava/util/ArrayList;", "dateBeenList$delegate", "dateBeenStringList", "", "", "getDateBeenStringList", "dateBeenStringList$delegate", "isFromDetail", "", "getOnConfirm", "()Lkotlin/jvm/functions/Function1;", "repeatTimeAdapter", "Lcom/cyxbsmobile_single/module_todo/adapter/RepeatTimeAdapter;", "getRepeatTimeAdapter", "()Lcom/cyxbsmobile_single/module_todo/adapter/RepeatTimeAdapter;", "repeatTimeAdapter$delegate", "todo", "getTodo", "()Lcom/cyxbsmobile_single/module_todo/model/bean/Todo;", "todo$delegate", "addNotify", "addRepeat", "changeRepeatType2EveryDay", "changeRepeatType2EveryMonth", "changeRepeatType2EveryWeek", "changeRepeatType2EveryYear", "hideWheel", "initClick", "initRepeatWheelListener", "onCancelClick", "onConfirmClick", "onDelClick", "resetAllRepeatMode", "resetNotifyTime", "setAsSinglePicker", "setContentView", "view", "Landroid/view/View;", "showNotifyDatePicker", "showRepeatDatePicker", "whenStatusDifDoAndChangeStatus", "targetStatus", "onDiff", "Lkotlin/Function0;", "curPos", "", "Lcom/aigestudio/wheelpicker/WheelPicker;", "gone", "visible", "CurOperate", "module_todo_debug"})
public final class AddItemDialog extends com.google.android.material.bottomsheet.BottomSheetDialog {
    private final kotlin.Lazy dateBeenList$delegate = null;
    private final kotlin.Lazy todo$delegate = null;
    private final kotlin.Lazy dateBeenStringList$delegate = null;
    private final kotlin.Lazy chooseYearAdapter$delegate = null;
    private final kotlin.Lazy repeatTimeAdapter$delegate = null;
    private boolean isFromDetail = false;
    private com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog.CurOperate curOperate = com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog.CurOperate.NONE;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<com.cyxbsmobile_single.module_todo.model.bean.Todo, kotlin.Unit> onConfirm = null;
    
    private final java.util.ArrayList<java.util.ArrayList<com.cyxbsmobile_single.module_todo.model.bean.DateBeen>> getDateBeenList() {
        return null;
    }
    
    private final com.cyxbsmobile_single.module_todo.model.bean.Todo getTodo() {
        return null;
    }
    
    private final java.util.ArrayList<java.util.List<java.lang.String>> getDateBeenStringList() {
        return null;
    }
    
    private final com.cyxbsmobile_single.module_todo.adapter.ChooseYearAdapter getChooseYearAdapter() {
        return null;
    }
    
    private final com.cyxbsmobile_single.module_todo.adapter.RepeatTimeAdapter getRepeatTimeAdapter() {
        return null;
    }
    
    @java.lang.Override()
    public void setContentView(@org.jetbrains.annotations.NotNull()
    android.view.View view) {
    }
    
    private final void initClick() {
    }
    
    public final void resetNotifyTime(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo) {
    }
    
    private final void onConfirmClick() {
    }
    
    private final void onCancelClick() {
    }
    
    private final void onDelClick() {
    }
    
    public final void showNotifyDatePicker() {
    }
    
    public final void showRepeatDatePicker() {
    }
    
    private final void initRepeatWheelListener() {
    }
    
    private final void changeRepeatType2EveryDay() {
    }
    
    private final void changeRepeatType2EveryWeek() {
    }
    
    private final void changeRepeatType2EveryMonth() {
    }
    
    private final void changeRepeatType2EveryYear() {
    }
    
    private final void addNotify() {
    }
    
    private final void addRepeat() {
    }
    
    private final void hideWheel() {
    }
    
    public final void setAsSinglePicker(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog.CurOperate curOperate) {
    }
    
    private final void whenStatusDifDoAndChangeStatus(com.cyxbsmobile_single.module_todo.ui.dialog.AddItemDialog.CurOperate targetStatus, kotlin.jvm.functions.Function0<kotlin.Unit> onDiff) {
    }
    
    public final void resetAllRepeatMode(@org.jetbrains.annotations.NotNull()
    com.cyxbsmobile_single.module_todo.model.bean.Todo todo) {
    }
    
    private final void gone(@org.jetbrains.annotations.NotNull()
    com.aigestudio.wheelpicker.WheelPicker $this$gone) {
    }
    
    private final void visible(@org.jetbrains.annotations.NotNull()
    com.aigestudio.wheelpicker.WheelPicker $this$visible) {
    }
    
    private final int curPos(@org.jetbrains.annotations.NotNull()
    com.aigestudio.wheelpicker.WheelPicker $this$curPos) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function1<com.cyxbsmobile_single.module_todo.model.bean.Todo, kotlin.Unit> getOnConfirm() {
        return null;
    }
    
    public AddItemDialog(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.cyxbsmobile_single.module_todo.model.bean.Todo, kotlin.Unit> onConfirm) {
        super(null);
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/cyxbsmobile_single/module_todo/ui/dialog/AddItemDialog$CurOperate;", "", "(Ljava/lang/String;I)V", "REPEAT", "NOTIFY", "NONE", "module_todo_debug"})
    public static enum CurOperate {
        /*public static final*/ REPEAT /* = new REPEAT() */,
        /*public static final*/ NOTIFY /* = new NOTIFY() */,
        /*public static final*/ NONE /* = new NONE() */;
        
        CurOperate() {
        }
    }
}