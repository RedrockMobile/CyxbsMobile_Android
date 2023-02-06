package com.mredrock.cyxbs.lib.base.dailog

import android.content.Context
import android.util.Size
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.mredrock.cyxbs.config.R
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px

/**
 * 封装了只有单按钮和双按钮的 dialog，自带圆角，符合大部分视觉需要的场景
 *
 * ## 用法
 * ```
 * ChooseDialog.Builder(
 *     context,
 *     ChooseDialog.Data(
 *         content = "你已有一位关联的同学\n确定要替换吗？",
 *         width = 255.dp2px,
 *         height = 167.dp2px
 *     )
 * ).setPositiveClick {
 *     // 点击确认按钮
 * }.setNegativeClick {
 *     // 点击取消按钮
 * }.setDismissCallback {
 *     // dialog 被关闭时的回调，包含返回键
 * }.setCancelCallback {
 *     // 使用返回键关闭 dialog 的回调
 * }.show()
 * ```
 *
 * ## 1、我的 dialog 需要带有一个标题 ? 一个输入框 ?
 * 你可以参考 module_login 中的 UserAgreementDialog，他是我写的带有标题的一个示例，并且是这个 dialog 的子类。
 * 如果你还有其他扩展需求，可以继承 ChooseDialog 或者 BaseDialog 进行自定义
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/4/23 21:08
 */
open class ChooseDialog protected constructor(
  context: Context,
  positiveClick: (ChooseDialog.() -> Unit)? = null,
  negativeClick: (ChooseDialog.() -> Unit)? = null,
  dismissCallback: (ChooseDialog.() -> Unit)? = null,
  cancelCallback: (ChooseDialog.() -> Unit)? = null,
  data: Data,
) : BaseDialog<ChooseDialog, ChooseDialog.Data>(
  context,
  positiveClick,
  negativeClick,
  dismissCallback,
  cancelCallback,
  data
) {
  
  open class Builder(context: Context, data: Data) : BaseDialog.Builder<ChooseDialog, Data>(context, data) {
    
    override fun build(): ChooseDialog {
      return ChooseDialog(
        context,
        positiveClick,
        negativeClick,
        dismissCallback,
        cancelCallback,
        data
      )
    }
  }
  
  /**
   * @param content dialog 的文本内容
   * @param contentSize content 的字体大小
   * @param contentGravity 文本的 gravity（是 TextView 的 gravity 属性，非 layout_gravity）
   * @param positiveButtonText 确定按钮文本
   * @param negativeButtonText 取消按钮文本
   * @param positiveButtonColor 确定按钮颜色
   * @param negativeButtonColor 取消按钮颜色
   * @param width dialog 的宽
   * @param height dialog 的高
   * @param backgroundId dialog 的背景，默认背景应该能满足
   * @param buttonSize button 的大小，单位 dp
   */
  open class Data(
    val content: String = "弹窗内容为空",
    val contentSize: Float = 13F,
    val contentGravity: Int = Gravity.CENTER,
    override val type: DialogType = BaseDialog.Data.type,
    override val positiveButtonText: String = BaseDialog.Data.positiveButtonText,
    override val negativeButtonText: String = BaseDialog.Data.negativeButtonText,
    @ColorRes
    override val positiveButtonColor: Int = BaseDialog.Data.positiveButtonColor,
    @ColorRes
    override val negativeButtonColor: Int = BaseDialog.Data.negativeButtonColor,
    override val buttonSize: Size = BaseDialog.Data.buttonSize,
    override val width: Int = BaseDialog.Data.width,
    override val height: Int = BaseDialog.Data.height,
    @DrawableRes
    override val backgroundId: Int = BaseDialog.Data.backgroundId,
  ) : BaseDialog.Data
  
  override fun createContentView(context: Context): View {
    return TextView(context).apply {
      layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
        Gravity.CENTER
      ).apply {
        topMargin = 15.dp2px
        bottomMargin = topMargin
        leftMargin = 25.dp2px
        rightMargin = leftMargin
      }
      setTextColor(R.color.config_level_four_font_color.color)
    }
  }
  
  override fun initContentView(view: View) {
    view as TextView
    view.text = data.content
    view.textSize = data.contentSize
    view.gravity = data.contentGravity
  }
}

