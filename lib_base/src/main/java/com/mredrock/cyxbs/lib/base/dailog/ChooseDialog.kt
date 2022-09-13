package com.mredrock.cyxbs.lib.base.dailog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.lifecycle.*
import com.google.android.material.button.MaterialButton
import com.mredrock.cyxbs.config.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * 由于掌邮的 Dialog 都长一个样，特意封装了一下
 *
 * 功能有：
 * - 自动管理 Fragment 的生命周期问题
 *
 * 注意事项：
 * - 构造器本来是该设置成 private，但因为系统重建 Fragment 只能反射调用公开的无参构造器，所以只能使用 internal
 * - 不设置成 abstract 或者 open class，是为了防止不正确的使用 Fragment
 * - 不建议之后修改代码，将 [ChooseDialog] 对象向外提供，正确做法是将回调设置在 [ChooseDialogViewModel] 中
 *
 * 必须调用 [Builder.show] 才能展示
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/23 21:08
 */
class ChooseDialog internal constructor(): DialogFragment() {

  private val mViewModel: ChooseDialogViewModel by viewModels()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // dismiss 事件应该交给 Fragment 整个生命周期来处理，而不是 View 的生命周期
    // 所以写在 onCreate 中，并且使用 Fragment 的 lifecycle
    lifecycleScope.launch {
      mViewModel.dismissEvent
        .collect {
          this@ChooseDialog.dismiss()
        }
    }
  }
  
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    // 取消 dialog 默认背景
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return inflater.inflate(mViewModel.data.type.layoutId, container, false)
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    /*
    * 设置根布局的宽和高
    * 这里设置是因为在 xml 中写的宽和高是会失效的，失效后会变成 wrap_content 值
    * 失效原因请看 22 年上半年郭祥瑞的自定义 View 课件，里面有分析
    * */
    val lp = view.layoutParams
    lp.width = mViewModel.data.width
    lp.height = mViewModel.data.height
    
    // 根据不同类型进行不同的设置
    when (mViewModel.data.type) {
      DialogType.ONR_BUT -> {
        val ivBg: ImageView = view.findViewById(R.id.config_iv_choose_dialog_one_btn_background)
        val tvContent: TextView = view.findViewById(R.id.config_tv_choose_dialog_one_btn_content)
        val btnPositive: MaterialButton = view.findViewById(R.id.config_btn_choose_dialog_one_btn_positive)
        ivBg.setImageResource(mViewModel.data.backgroundId)
        tvContent.text = mViewModel.data.content
        btnPositive.text = mViewModel.data.positiveButtonText
        btnPositive.setBackgroundColor(
          ContextCompat.getColor(requireContext(), mViewModel.data.positiveButtonColor))
        btnPositive.setOnClickListener {
          mViewModel.sendPositiveClick()
        }
      }
      DialogType.TWO_BUT -> {
        val ivBg: ImageView = view.findViewById(R.id.config_iv_choose_dialog_two_btn_background)
        val tvContent: TextView = view.findViewById(R.id.config_tv_choose_dialog_two_btn_content)
        val btnPositive: MaterialButton = view.findViewById(R.id.config_btn_choose_dialog_two_btn_positive)
        val btnNegative: MaterialButton = view.findViewById(R.id.config_btn_choose_dialog_two_btn_negative)
        ivBg.setImageResource(mViewModel.data.backgroundId)
        tvContent.text = mViewModel.data.content
        btnPositive.text = mViewModel.data.positiveButtonText
        btnNegative.text = mViewModel.data.negativeButtonText
        btnPositive.setBackgroundColor(
          ContextCompat.getColor(requireContext(), mViewModel.data.positiveButtonColor))
        btnNegative.setBackgroundColor(
          ContextCompat.getColor(requireContext(), mViewModel.data.negativeButtonColor))
        btnPositive.setOnClickListener {
          mViewModel.sendPositiveClick()
        }
        btnNegative.setOnClickListener {
          mViewModel.sendNegativeClick()
        }
      }
    }
  }
  
  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    mViewModel.sendCancelCallback()
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    mViewModel.sendDismissCallback()
  }
  
  class Builder private constructor(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
  ) {
    
    constructor(
      lifecycleOwner: LifecycleOwner
    ) : this(
      when (lifecycleOwner) {
        is FragmentActivity -> lifecycleOwner.supportFragmentManager
        is Fragment -> lifecycleOwner.childFragmentManager
        else -> error("不支持该类型：${lifecycleOwner::class.simpleName}")
      },
      lifecycleOwner.lifecycle
    )

    private val mDialog: ChooseDialog

    init {
      val dialog = fragmentManager.findFragmentByTag(DIALOG_TAG)
      mDialog = if (dialog is ChooseDialog) {
        dialog // 说明之前已经初始化
      } else {
        ChooseDialog() // 说明没有初始化过
      }
    }
  
    /**
     * 设置数据
     */
    fun setData(data: Data): Builder {
      addOnceCallbackAfterCreated {
        mDialog.mViewModel.data = data
      }
      return this
    }
  
    /**
     * 设置确定按钮的点击监听
     */
    fun setPositiveClick(click: IDialogProxy.() -> Unit): Builder {
      addOnceCallbackAfterCreated {
        mDialog.mViewModel
          .positiveClick
          .onEach {
            click.invoke(it)
          }.launchIn(lifecycle.coroutineScope)
      }
      return this
    }
  
    /**
     * 设置取消按钮的点击监听
     */
    fun setNegativeClick(click: IDialogProxy.() -> Unit): Builder {
      addOnceCallbackAfterCreated {
        mDialog.mViewModel
          .negativeClick
          .onEach {
            click.invoke(it)
          }.launchIn(lifecycle.coroutineScope)
      }
      return this
    }
  
    /**
     * 设置所有关闭 dialog 的回调, 包括调用 dismiss() 和 返回键
     */
    fun setDismissCallback(call: IDialogProxy.() -> Unit): Builder {
      addOnceCallbackAfterCreated {
        mDialog.mViewModel
          .dismissCallback
          .onEach {
            call.invoke(it)
          }.launchIn(lifecycle.coroutineScope)
      }
      return this
    }
  
    /**
     * 设置只包含返回键关闭 Dialog 的回调
     */
    fun setCancelCallback(call: IDialogProxy.() -> Unit): Builder {
      // 只有在 Fragment 初始化后才能得到 ViewModel，所以只能这样设置监听
      addOnceCallbackAfterCreated {
        mDialog.mViewModel
          .cancelCallback
          .onEach {
            call.invoke(it)
          }.launchIn(lifecycle.coroutineScope)
      }
      return this
    }
  
    /**
     * 展示 Dialog
     *
     * 内部是 Fragment 较为正确的写法，不会出现 Fragment 被重复创建和 ViewModel 失效的问题
     */
    fun show() {
      val dialog = fragmentManager.findFragmentByTag(DIALOG_TAG)
      if (dialog is ChooseDialog) {
        // 说明之前已经初始化，一般情况下只显示一个 dialog 即可，没必要再次提交
      } else {
        mDialog.show(fragmentManager, DIALOG_TAG) // 说明没有初始化过
      }
    }
  
    /**
     * 添加一个在 DialogFragment 初始化后只会回调一次的回调，且回调后会立即取消该回调
     *
     * 为什么要这样？
     * 1、ViewModel 只能在 Created 后才能使用
     */
    private fun addOnceCallbackAfterCreated(call: () -> Unit) {
      // 只有在 Fragment 初始化后才能得到 ViewModel，所以只能这样设置监听
      mDialog.lifecycle.addObserver(
        object : LifecycleEventObserver {
          override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (source.lifecycle.currentState >= Lifecycle.State.CREATED) {
              call.invoke()
              // 回调后就 remove 掉
              mDialog.lifecycle.removeObserver(this)
            }
          }
        }
      )
    }

    companion object {
      private const val DIALOG_TAG = "BaseDialog"
      private const val LAST_LIFECYCLE = ""
    }
  }

  internal class ChooseDialogViewModel : ViewModel(), IDialogProxy {
    var data: Data = defaultData
    
    val dismissEvent = MutableSharedFlow<Unit>()
    val positiveClick = MutableSharedFlow<IDialogProxy>()
    val negativeClick = MutableSharedFlow<IDialogProxy>()
    val dismissCallback = MutableSharedFlow<IDialogProxy>()
    val cancelCallback = MutableSharedFlow<IDialogProxy>()

    fun sendPositiveClick() {
      viewModelScope.launch {
        positiveClick.emit(this@ChooseDialogViewModel)
      }
    }
    
    fun sendNegativeClick() {
      viewModelScope.launch {
        negativeClick.emit(this@ChooseDialogViewModel)
      }
    }
    
    fun sendDismissCallback() {
      viewModelScope.launch {
        dismissCallback.emit(this@ChooseDialogViewModel)
      }
    }
    
    fun sendCancelCallback() {
      viewModelScope.launch {
        cancelCallback.emit(this@ChooseDialogViewModel)
      }
    }

    companion object {
      private val defaultData = Data()
    }
  
    override fun dismiss() {
      viewModelScope.launch {
        dismissEvent.emit(Unit)
      }
    }
  }
  
  // Dialog 的代理类
  interface IDialogProxy {
    fun dismiss()
  }
  
  /**
   * @param content dialog 的文本内容
   * @param positiveButtonText 确定按钮文本
   * @param negativeButtonText 取消按钮文本
   * @param positiveButtonColor 确定按钮颜色
   * @param negativeButtonColor 取消按钮颜色
   * @param width dialog 的宽，默认为 wrap_content
   * @param height dialog 的高，默认为 wrap_content
   * @param backgroundId dialog 的背景，默认背景应该能满足
   */
  data class Data(
    val content: String = "弹窗内容为空",
    val type: DialogType = DialogType.TWO_BUT,
    val positiveButtonText: String = "确定",
    val negativeButtonText: String = "取消",
    @ColorRes
    val positiveButtonColor: Int = R.color.config_choose_dialog_btn_positive,
    @ColorRes
    val negativeButtonColor: Int = R.color.config_choose_dialog_btn_negative,
    val width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    @DrawableRes
    val backgroundId: Int = R.drawable.config_shape_round_corner_dialog
  )

  enum class DialogType(val layoutId: Int) {
    // 只有一个 Button
    ONR_BUT(R.layout.config_choose_dialog_one_btn),
    // 有两个 Button
    TWO_BUT(R.layout.config_choose_dialog_two_btn)
  }
}

