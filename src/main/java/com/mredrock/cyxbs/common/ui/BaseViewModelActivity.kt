package com.mredrock.cyxbs.common.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import org.jetbrains.anko.indeterminateProgressDialog

/**
 * Created By jay68 on 2018/8/23.
 */
abstract class BaseViewModelActivity<T : BaseViewModel> : BaseActivity() {
    lateinit var viewModel: T

    protected abstract val viewModelClass: Class<T>

    private var progressDialog: ProgressDialog? = null

    private fun initProgressBar() = indeterminateProgressDialog(message = "Loading...") {
        setOnDismissListener { viewModel.onProgressDialogDismissed() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = getViewModelFactory()
        viewModel = if (viewModelFactory != null) {
            ViewModelProvider(this, viewModelFactory).get(viewModelClass)
        } else {
            ViewModelProvider(this).get(viewModelClass)
        }
        viewModel.apply {
            toastEvent.observe { str -> str?.let { CyxbsToast.makeText(baseContext, it, Toast.LENGTH_SHORT).show() } }
            longToastEvent.observe { str -> str?.let { CyxbsToast.makeText(baseContext, it, Toast.LENGTH_LONG).show() } }
            progressDialogEvent.observe {
                it ?: return@observe
                //确保只有一个对话框会被弹出
                if (it != ProgressDialogEvent.DISMISS_DIALOG_EVENT && progressDialog?.isShowing != true) {
                    progressDialog = progressDialog ?: initProgressBar()
                    progressDialog?.show()
                } else if (it == ProgressDialogEvent.DISMISS_DIALOG_EVENT && progressDialog?.isShowing != false) {
                    progressDialog?.dismiss()
                }
            }
        }
    }

    protected open fun getViewModelFactory(): ViewModelProvider.Factory? = null

    inline fun <T> LiveData<T>.observe(crossinline onChange: (T?) -> Unit) = observe(this@BaseViewModelActivity, Observer { onChange(it) })

    inline fun <T> LiveData<T>.observeNotNull(crossinline onChange: (T) -> Unit) = observe(this@BaseViewModelActivity, Observer {
        it ?: return@Observer
        onChange(it)
    })

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }
}