package com.mredrock.cyxbs.mine.base.presenter

import androidx.lifecycle.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.IVM
import com.taobao.agoo.IRegister
import java.lang.ref.WeakReference

/**
 *@author ZhiQiang Tu
 *@time 2021/8/7  11:51
 *@signature 我们不明前路，却已在路上
 */

/**
 *  Presenter的基类，内部自动获取了ViewModel的实例
 */
abstract class BasePresenter</*V : IView,*/ VM : IVM> : LifecycleObserver,IPresenter<VM>{
    protected var vm: VM? = null

    /*protected var view: WeakReference<V>? = null
    fun onAttachView(view: IView) {
        this.view = WeakReference(view as V)
    }
    fun detachView() {
        this.view?.clear()
    }*/


    /**
     *  在Activity或者Fragment创建的时候传入
     *
     * @see com.mredrock.cyxbs.common.ui.BaseMVPVMFragment
     * @see com.mredrock.cyxbs.common.ui.BaseMVPVMActivity
     */
    override fun onAttachVM(vm: IVM) {
        this.vm = vm as VM
    }

    /**
     *  在Activity或者Fragment销毁的时候销毁
     *
     * @see com.mredrock.cyxbs.common.ui.BaseMVPVMFragment
     * @see com.mredrock.cyxbs.common.ui.BaseMVPVMActivity
     */
    override fun onDetachVM(){
        vm = null
    }

    /**
     *  1.在Activity被Destroy掉的时候调用
     *  2.改方法是用以清楚Presenter中一些可能会存在内存泄漏的变量
     */
    open fun clear(){}


    /**
     * 生命周期相关的回调
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate(lifecycleOwner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume(lifecycleOwner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart(lifecycleOwner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause(lifecycleOwner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop(lifecycleOwner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy(lifecycleOwner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    open fun onAny(lifecycleOwner: LifecycleOwner) {
    }
}