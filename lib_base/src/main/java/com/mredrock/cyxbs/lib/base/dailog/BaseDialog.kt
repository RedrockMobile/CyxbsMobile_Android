package com.mredrock.cyxbs.lib.base.dailog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.SizeF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.mredrock.cyxbs.config.R
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * .
 *
 * @author 985892345
 * 2022/12/29 20:08
 */
abstract class BaseDialog<T : BaseDialog<T, D>, D: BaseDialog.Data> protected constructor(
  context: Context,
  protected val positiveClick: (T.() -> Unit)? = null,
  protected val negativeClick: (T.() -> Unit)? = null,
  protected val dismissCallback: (T.() -> Unit)? = null,
  protected val cancelCallback: (T.() -> Unit)? = null,
  protected val data: D,
) : Dialog(context) {
  
  /**
   * 创建显示的内容，你可以在这里面返回你自己的内容视图
   */
  abstract fun createContentView(context: Context): View
  
  /**
   * @param view 你在 [createContentView] 返回的 View
   */
  abstract fun initContentView(view: View)
  
  override fun onCreate(savedInstanceState: Bundle?) {
    // 取消 dialog 默认背景
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    super.onCreate(savedInstanceState)
    
    val view = LayoutInflater.from(context).inflate(data.type.layoutId, null)
    val insertView = createContentView(view.context)
    view.findViewWithTag<FrameLayout>("choose_dialog_content").addView(insertView)
    setContentView(view, ViewGroup.LayoutParams(data.width, data.height))
    initViewInternal(view)
    initContentView(insertView)
  }
  
  @Suppress("UNCHECKED_CAST")
  private fun initViewInternal(view: View) {
    // 根据不同类型进行不同的设置
    when (data.type) {
      DialogType.ONR_BUT -> {
        val ivBg: ImageView = view.findViewById(R.id.config_iv_choose_dialog_one_btn_background)
        val btnPositive: MaterialButton =
          view.findViewById(R.id.config_btn_choose_dialog_one_btn_positive)
        ivBg.setImageResource(data.backgroundId)
        btnPositive.text = data.positiveButtonText
        btnPositive.layoutParams.apply {
          width = data.buttonSize.width.dp2px
          height = data.buttonSize.height.dp2px
        }
        btnPositive.setBackgroundColor(
          ContextCompat.getColor(context, data.positiveButtonColor)
        )
        btnPositive.setOnClickListener {
          positiveClick?.invoke(this as T)
        }
      }
      DialogType.TWO_BUT -> {
        val ivBg: ImageView = view.findViewById(R.id.config_iv_choose_dialog_two_btn_background)
        val btnPositive: MaterialButton =
          view.findViewById(R.id.config_btn_choose_dialog_two_btn_positive)
        val btnNegative: MaterialButton =
          view.findViewById(R.id.config_btn_choose_dialog_two_btn_negative)
        ivBg.setImageResource(data.backgroundId)
        btnPositive.text = data.positiveButtonText
        btnNegative.text = data.negativeButtonText
        btnPositive.layoutParams.apply {
          width = data.buttonSize.width.dp2px
          height = data.buttonSize.height.dp2px
        }
        btnNegative.layoutParams.apply {
          width = data.buttonSize.width.dp2px
          height = data.buttonSize.height.dp2px
        }
        btnPositive.setBackgroundColor(
          ContextCompat.getColor(context, data.positiveButtonColor)
        )
        btnNegative.setBackgroundColor(
          ContextCompat.getColor(context, data.negativeButtonColor)
        )
        btnPositive.setOnClickListener {
          positiveClick?.invoke(this as T)
        }
        btnNegative.setOnClickListener {
          negativeClick?.invoke(this as T)
        }
      }
    }
  }
  
  @Suppress("UNCHECKED_CAST")
  override fun dismiss() {
    super.dismiss()
    dismissCallback?.invoke(this as T)
  }
  
  @Suppress("UNCHECKED_CAST")
  override fun cancel() {
    super.cancel()
    cancelCallback?.invoke(this as T)
  }
  
  abstract class Builder<T : BaseDialog<T, D>, D: Data>(
    protected val context: Context,
    protected val data: D
  ) {
    
    protected var positiveClick: (T.() -> Unit)? = null
    protected var negativeClick: (T.() -> Unit)? = null
    protected var dismissCallback: (T.() -> Unit)? = null
    protected var cancelCallback: (T.() -> Unit)? = null
    
    /**
     * 设置确定按钮的点击监听
     */
    open fun setPositiveClick(click: T.() -> Unit): Builder<T, D> {
      positiveClick = click
      return this
    }
    
    /**
     * 设置取消按钮的点击监听
     */
    open fun setNegativeClick(click: T.() -> Unit): Builder<T, D> {
      negativeClick = click
      return this
    }
    
    /**
     * 设置所有关闭 dialog 的回调, 包括调用 dismiss() 和 返回键
     */
    open fun setDismissCallback(call: T.() -> Unit): Builder<T, D> {
      dismissCallback = call
      return this
    }
    
    /**
     * 设置只包含返回键关闭 Dialog 的回调
     */
    open fun setCancelCallback(call: T.() -> Unit): Builder<T, D> {
      cancelCallback = call
      return this
    }
    
    abstract fun build(): T
  
    /**
     * 直接展示
     */
    fun show() {
      build().show()
    }
  }
  
  interface Data {
    /**
     * Dialog 类型
     */
    val type: DialogType
  
    /**
     * 确定按钮文本，默认为
     */
    val positiveButtonText: String
  
    /**
     * 取消按钮文本
     */
    val negativeButtonText: String
  
    /**
     * 确定按钮颜色
     */
    val positiveButtonColor: Int
  
    /**
     * 取消按钮颜色
     */
    val negativeButtonColor: Int
  
    /**
     * button 的按钮大小，单位 dp
     */
    val buttonSize: SizeF
  
    /**
     * dialog 的宽，默认为 300dp
     */
    val width: Int
  
    /**
     * dialog 的高，默认为 wrap_content
     */
    val height: Int
  
    /**
     * dialog 的背景，默认背景应该能满足
     */
    @get:DrawableRes
    val backgroundId: Int
    
    companion object DEFAULT : Data {
      override val type: DialogType
        get() = DialogType.TWO_BUT
      override val positiveButtonText: String
        get() = "确定"
      override val negativeButtonText: String
        get() = "取消"
      override val positiveButtonColor: Int
        get() = R.color.config_choose_dialog_btn_positive
      override val negativeButtonColor: Int
        get() = R.color.config_choose_dialog_btn_negative
      override val buttonSize: SizeF
        get() = SizeF(80F, 34F)
      override val width: Int
        get() = 300.dp2px
      override val height: Int
        get() = ViewGroup.LayoutParams.WRAP_CONTENT
      override val backgroundId: Int
        get() = R.drawable.config_shape_round_corner_dialog
    }
  }
  
  enum class DialogType(val layoutId: Int) {
    // 只有一个 Button
    ONR_BUT(R.layout.config_choose_dialog_one_btn),
    
    // 有两个 Button
    TWO_BUT(R.layout.config_choose_dialog_two_btn)
  }
}