package com.mredrock.cyxbs.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.R
import java.util.*

/**
 * 掌邮内统一的 Toolbar，如果视觉给出其他样式，请自己单独实现
 *
 * 常用：
 * [init]              初始化
 *
 * 其他设置：
 * [setTitle]                       设置标题
 * [setSubtitle]                    设置副标题
 * [withSplitLine]                  是否绘制分割线
 * [setTitleLocationAtLeft]         设置 title 是否左对齐，否则居中
 * [setNavigationIcon]              设置图标
 * [setNavigationOnClickListener]   设置图标的点击监听
 * [setTitleTextColor]
 * [setBackgroundColor]
 * 等其他常见方法
 *
 * ## 使用方式为：
 * ```
 * 在 XML 中合适的位置引入
 * <include
 *     layout="@layout/common_toolbar" />
 *
 * 在代码处：
 * val toolbar = findViewById<JToolbar>(R.id.toolbar)
 * toolbar.init(this, "标题") // 还有其他默认参数
 * ```
 *
 * 标题居中显示的toolbar
 * Created by Jay on 2017/9/20.
 *
 * @author 985892345（修改为 kt）
 * 2023/3/1 19:54
 */
@Deprecated("使用 lib_config 中的 JToolbar 代替")
class JToolbar(context: Context, attrs: AttributeSet?) : Toolbar(context, attrs) {
  
  /**
   * 在 Activity 中初始化设置
   * @param title 标题
   * @param withSplitLine 是否加下划线
   * @param icon 图标，默认图标为返回图标
   * @param clickIcon 点击图标的监听，默认为 finishAfterTransition()
   * @param titleOnLeft title 是否左对齐，否则居中
   */
  fun init(
    activity: AppCompatActivity,
    title: String,
    withSplitLine: Boolean = true,
    @DrawableRes
    icon: Int = R.drawable.common_ic_back,
    clickIcon: OnClickListener? = View.OnClickListener { activity.finishAfterTransition() },
    titleOnLeft: Boolean = true
  ) {
    this.title = title
    withSplitLine(withSplitLine)
    activity.setSupportActionBar(this@JToolbar)
    setTitleLocationAtLeft(titleOnLeft)
    if (clickIcon == null) {
      navigationIcon = null
    } else {
      setNavigationIcon(icon)
      setNavigationOnClickListener(clickIcon)
    }
  }
  
  /**
   * 设置 title 是否左对齐，否则居中
   */
  fun setTitleLocationAtLeft(isLeft: Boolean) {
    mIsTitleAtLeft = isLeft
    requestLayout()
  }
  
  /**
   * 是否绘制分割线
   */
  fun withSplitLine(withSplitLine: Boolean) {
    mWithSplitLine = withSplitLine
    invalidate()
  }
  
  private var mPaint = Paint().apply {
    color = ContextCompat.getColor(context, R.color.common_default_divide_line_color)
    alpha = 25
    strokeWidth = 1.dp2pxF
  }
  
  private var mTitleTextView: TextView? = null
  private var mSubtitleTextView: TextView? = null
  
  private var mIsTitleAtLeft = true
  private var mWithSplitLine = true
  
  override fun setTitle(title: CharSequence?) {
    super.setTitle(title)
    if (mTitleTextView == null) {
      mTitleTextView = getTitleTv("mTitleTextView")
    }
  }
  
  override fun setSubtitle(subtitle: CharSequence?) {
    super.setSubtitle(subtitle)
    if (mSubtitleTextView == null) {
      mSubtitleTextView = getTitleTv("mSubtitleTextView")
    }
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    super.onLayout(changed, l, t, r, b)
    if (mIsTitleAtLeft) {
      reLayoutTitleToLeft(mTitleTextView)
      reLayoutTitleToLeft(mSubtitleTextView)
    } else {
      reLayoutTitleToCenter(mTitleTextView)
      reLayoutTitleToCenter(mSubtitleTextView)
    }
  }
  
  private fun reLayoutTitleToLeft(title: TextView?) {
    if (title == null) return
    val ir = getChildAt(0).right
    title.layout(ir, title.top, ir + title.measuredWidth, title.bottom)
  }
  
  private fun reLayoutTitleToCenter(title: TextView?) {
    if (title == null) return
    //note: o for old ,t for temp, l for left...
    val ol = title.left
    val width = title.measuredWidth
    val tl = (measuredWidth - width) / 2
    if (tl > ol) {
      title.layout(tl, title.top, tl + width, title.bottom)
    }
  }
  
  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    if (mWithSplitLine) {
      val y = height - 0.5F
      canvas.drawLine(0F, y, width.toFloat(), y, mPaint)
    }
  }
  
  private val Int.dp2pxF
    get() = context.resources.displayMetrics.density * this
  
  @SuppressLint("ResourceAsColor")
  private fun getTitleTv(name: String): TextView? {
    try {
      // 以前学长遗留的代码，用的反射，如果后面不行了，可以采取 getChildAt() 在合适的位置拿子 View
      val field = Objects.requireNonNull(javaClass.superclass).getDeclaredField(name)
      field.isAccessible = true
      val view = field[this] as TextView
      view.paint.isFakeBoldText = true
      view.setTextColor(ContextCompat.getColor(context, R.color.common_level_two_font_color))
      return view
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }
}