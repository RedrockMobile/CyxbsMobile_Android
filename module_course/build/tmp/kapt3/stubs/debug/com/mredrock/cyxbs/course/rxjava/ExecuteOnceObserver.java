package com.mredrock.cyxbs.course.rxjava;

import java.lang.System;

/**
 * [ExecuteOnceObserver] is used to only get one [onNext] Result.
 *
 * @param onExecuteOnceNext The concrete implement of the [onNext]
 * @param onExecuteOnceComplete The concrete implement of the [onComplete]
 * @param onExecuteOnceError The concrete implement of the [onError]
 * @param onExecuteOnFinal When everything is done,[onExecuteOnFinal] is called
 *
 * Created by anriku on 2018/9/18.
 */
@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002BQ\u0012\u0014\b\u0002\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u0012\u0014\b\u0002\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\u0002\u0010\u000bJ\b\u0010\u0014\u001a\u00020\u0005H\u0016J\u0010\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\tH\u0016J\u0015\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00028\u0000H\u0016\u00a2\u0006\u0002\u0010\u0019J\u0010\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\rH\u0016R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u001d\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001d\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012\u00a8\u0006\u001c"}, d2 = {"Lcom/mredrock/cyxbs/course/rxjava/ExecuteOnceObserver;", "T", "Lio/reactivex/Observer;", "onExecuteOnceNext", "Lkotlin/Function1;", "", "onExecuteOnceComplete", "Lkotlin/Function0;", "onExecuteOnceError", "", "onExecuteOnFinal", "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)V", "mDisposable", "Lio/reactivex/disposables/Disposable;", "getOnExecuteOnFinal", "()Lkotlin/jvm/functions/Function0;", "getOnExecuteOnceComplete", "getOnExecuteOnceError", "()Lkotlin/jvm/functions/Function1;", "getOnExecuteOnceNext", "onComplete", "onError", "e", "onNext", "t", "(Ljava/lang/Object;)V", "onSubscribe", "d", "module_course_debug"})
public final class ExecuteOnceObserver<T extends java.lang.Object> implements io.reactivex.Observer<T> {
    private io.reactivex.disposables.Disposable mDisposable;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<T, kotlin.Unit> onExecuteOnceNext = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<kotlin.Unit> onExecuteOnceComplete = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<java.lang.Throwable, kotlin.Unit> onExecuteOnceError = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<kotlin.Unit> onExecuteOnFinal = null;
    
    @java.lang.Override()
    public void onComplete() {
    }
    
    @java.lang.Override()
    public void onSubscribe(@org.jetbrains.annotations.NotNull()
    io.reactivex.disposables.Disposable d) {
    }
    
    @java.lang.Override()
    public void onNext(T t) {
    }
    
    @java.lang.Override()
    public void onError(@org.jetbrains.annotations.NotNull()
    java.lang.Throwable e) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function1<T, kotlin.Unit> getOnExecuteOnceNext() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnExecuteOnceComplete() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function1<java.lang.Throwable, kotlin.Unit> getOnExecuteOnceError() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnExecuteOnFinal() {
        return null;
    }
    
    public ExecuteOnceObserver(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super T, kotlin.Unit> onExecuteOnceNext, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onExecuteOnceComplete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Throwable, kotlin.Unit> onExecuteOnceError, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onExecuteOnFinal) {
        super();
    }
    
    public ExecuteOnceObserver() {
        super();
    }
}