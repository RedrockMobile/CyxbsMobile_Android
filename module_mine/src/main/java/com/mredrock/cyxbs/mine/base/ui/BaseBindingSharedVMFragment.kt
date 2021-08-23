package com.mredrock.cyxbs.common.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import java.lang.reflect.ParameterizedType

/**
 *@author ZhiQiang Tu
 *@time 2021/8/6  14:21
 *@signature 我们不明前路，却已在路上
 */
abstract class BaseBindingSharedVMFragment<VM : BaseViewModel, T : ViewDataBinding> : BaseFragment() {
    protected var binding: T? = null
    protected var shardViewModel: VM? = null

    private var progressDialog: ProgressDialog? = null

    private fun initProgressBar() = ProgressDialog(context).apply {
        isIndeterminate = true
        setMessage("Loading...")
        setOnDismissListener { shardViewModel?.onProgressDialogDismissed() }
    }

    private fun configVM() {
        shardViewModel?.apply {
            toastEvent.observe { str -> str?.let { CyxbsToast.makeText(context, it, Toast.LENGTH_SHORT).show() } }
            longToastEvent.observe { str -> str?.let { CyxbsToast.makeText(context, it, Toast.LENGTH_LONG).show() } }
            progressDialogEvent.observe {
                it ?: return@observe
                // 确保只有一个对话框会被弹出
                if (it != ProgressDialogEvent.DISMISS_DIALOG_EVENT && progressDialog?.isShowing != true) {
                    progressDialog = progressDialog ?: initProgressBar()
                    progressDialog?.show()
                } else if (it == ProgressDialogEvent.DISMISS_DIALOG_EVENT && progressDialog?.isShowing != false) {
                    progressDialog?.dismiss()
                }
            }
        }
    }

    abstract fun getLayoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        //获取Binding并对binding进行配置
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding?.lifecycleOwner = this
        return binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = getViewModelFactory()
        //获取viewModel
        activity?.apply {
            shardViewModel = if (viewModelFactory == null){
                ViewModelProvider(this).get(getActivityVMClass())
            }else{
                getViewModelFactory()?.let {
                    ViewModelProvider(this,
                        it
                    ).get(getActivityVMClass())
                }
            }
        }

        //配置一些通用配置
        configVM()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    inline fun <T> LiveData<T>.observe(crossinline onChange: (T?) -> Unit) = observe(this@BaseBindingSharedVMFragment, Observer { onChange(value) })
    inline fun <T> LiveData<T>.observeNotNull(crossinline onChange: (T) -> Unit) = observe(this@BaseBindingSharedVMFragment, Observer {
        it ?: return@Observer
        onChange(it)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //初始化配置
        initConfiguration()

        //初始化view
        initView()

        //初始化监听器
        initListener()

        //初始化数据监听
        observeData()
    }

    private fun getActivityVMClass(): Class<VM> = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>

    protected open fun initConfiguration() {}

    protected open fun initData() {}

    protected open fun initView() {}

    protected open fun initListener() {}

    protected open fun observeData() {}

    protected open fun getViewModelFactory(): ViewModelProvider.Factory? = null
}