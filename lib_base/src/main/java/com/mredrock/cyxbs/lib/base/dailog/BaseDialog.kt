package com.mredrock.cyxbs.lib.base.dailog

import android.content.Context
import android.view.View
import androidx.activity.ComponentDialog
import androidx.annotation.StyleRes
import androidx.lifecycle.LifecycleOwner
import com.mredrock.cyxbs.lib.base.ui.BaseUi
import com.mredrock.cyxbs.lib.utils.utils.BindView

/**
 * 支持自定义内容视图的圆角 dialog，该 dialog 样式符合视觉要求的大部分场景
 *
 * 继承于 ComponentDialog，支持 LifecycleOwner，对应的也支持协程
 *
 * 你可以参考它的实现类 [ChooseDialog]、DebugUpdateDialog 和 UserAgreementDialog 来适配你需要的场景
 *
 * ## 1、为什么不用 DialogFragment ?
 * 虽然官方推荐使用 DialogFragment，但是 Fragment 与父容器通信很麻烦，并且目前掌邮强制竖屏，所以不打算使用 DialogFragment
 *
 * ## 2、DialogFragment 推荐用法
 * DialogFragment 主要有两个坑
 * - Fragment 重建导致的数据丢失问题
 * - Fragment 不好与父容器直接通信
 *
 * 问题一可以采用 ViewModel 或者 savedInstanceState 解决
 * 问题二可以采用 ViewModel 或者 DialogFragment 直接 getActivity()/getParentFragment() 强转解决(强烈建议用接口约束下有哪些方法)
 *
 * ## 3、本 Dialog 可直接变为 DialogFragment
 * DialogFragment 提供了 onCreateDialog(): Dialog 方法用于自定义 Dialog，如果有必要的话，可以重写该方法。
 * 但请遵守 Fragment 的使用规范 (详细请查看飞书易错点文档)
 * 请查看：https://redrock.feishu.cn/wiki/wikcnSDEtcCJzyWXSsfQGqWxqGe
 *
 *
 * 更多注释请查看 [ChooseDialog]，自定义 dialog 可以参考 DebugUpdateDialog
 *
 * @author 985892345
 * 2022/12/29 20:08
 */
abstract class BaseDialog : ComponentDialog, BaseUi {

  constructor(context: Context) : super(context)

  constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)

  override val rootView: View
    get() = this.window!!.decorView

  override fun getViewLifecycleOwner(): LifecycleOwner = this

  final override fun <T : View> Int.view(): BindView<T> = BindView(this, this@BaseDialog)

  // 是否已经创建了 ContentView
  private var mHasContentViewChanged = false

  // doOnContentViewChanged 添加的回调
  private val mOnCreateContentViewAction = ArrayList<(rootView: View) -> Any?>()

  final override fun doOnCreateContentView(action: (rootView: View) -> Any?) {
    if (mHasContentViewChanged) {
      if (action.invoke(rootView) != null) {
        mOnCreateContentViewAction.add(action)
      }
    } else mOnCreateContentViewAction.add(action)
  }

  override fun onContentChanged() {
    super.onContentChanged()
    if (mHasContentViewChanged) throw IllegalStateException("不允许多次调用 setContentView")
    mHasContentViewChanged = true
    val iterator = mOnCreateContentViewAction.iterator()
    while (iterator.hasNext()) {
      if (iterator.next().invoke(rootView) == null) {
        iterator.remove()
      }
    }
  }
}