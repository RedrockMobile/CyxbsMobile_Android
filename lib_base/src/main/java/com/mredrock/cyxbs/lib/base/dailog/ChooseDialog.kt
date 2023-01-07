package com.mredrock.cyxbs.lib.base.dailog

import android.content.Context
import android.util.SizeF
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
 * ## 我的 dialog 需要带有一个标题 ? 一个输入框 ?
 * 你可以参考 module_login 中的 UserAgreementDialog，他是我写的带有标题的一个示例。
 * 如果你还有其他扩展需求，可以继承 ChooseDialog 或者 BaseDialog 进行自定义
 *
 * ## 为什么不用 DialogFragment ?
 * 虽然官方推荐使用 DialogFragment，但是 Fragment 与父容器通信很麻烦，并且目前掌邮强制竖屏，所以不打算使用 DialogFragment
 *
 * ## DialogFragment 推荐用法
 * DialogFragment 主要有两个坑
 * - Fragment 重建导致的数据丢失问题
 * - Fragment 不好与父容器直接通信
 *
 * 问题一可以采用 ViewModel 或者 savedInstanceState 解决
 * 问题二可以采用 ViewModel 或者 DialogFragment 直接 getActivity()/getParentFragment() 强转解决(强烈建议用接口约束下有哪些方法)
 *
 * ## 本 Dialog 可直接变为 DialogFragment
 * DialogFragment 提供了 onCreateDialog(): Dialog 方法用于自定义 Dialog，如果有必要的话，可以重写该方法。
 * 但请遵守 Fragment 的使用规范 (详细请查看飞书易错点文档)
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
   * @param width dialog 的宽，默认为 wrap_content
   * @param height dialog 的高，默认为 wrap_content
   * @param backgroundId dialog 的背景，默认背景应该能满足
   */
  data class Data(
    val content: String = "弹窗内容为空",
    val contentSize: Float = 13F,
    val contentGravity: Int = Gravity.CENTER,
    override val type: DialogType = DialogType.TWO_BUT,
    override val positiveButtonText: String = "确定",
    override val negativeButtonText: String = "取消",
    @ColorRes
    override val positiveButtonColor: Int = R.color.config_choose_dialog_btn_positive,
    @ColorRes
    override val negativeButtonColor: Int = R.color.config_choose_dialog_btn_negative,
    override val width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    override val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    @DrawableRes
    override val backgroundId: Int = R.drawable.config_shape_round_corner_dialog,
    override val buttonSize: SizeF = BaseDialog.Data.buttonSize,
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
      textSize = data.contentSize
      gravity = data.contentGravity
    }
  }
  
  override fun initContentView(view: View) {
    view as TextView
    view.text = data.content
  }
}

