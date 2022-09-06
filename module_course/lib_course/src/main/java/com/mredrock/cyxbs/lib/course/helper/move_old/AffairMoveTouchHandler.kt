//package com.mredrock.cyxbs.course2.page.course.helper.multitouch.entitymove
//
//import android.animation.ValueAnimator
//import android.annotation.SuppressLint
//import android.graphics.Canvas
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewConfiguration
//import android.view.animation.OvershootInterpolator
//import androidx.core.animation.addListener
//import com.mredrock.cyxbs.common.utils.extensions.lazyUnlock
//import com.mredrock.cyxbs.course2.page.course.helper.IItemTypeProvider
//import com.mredrock.cyxbs.course2.page.course.helper.multitouch.PointerFlag
//import com.mredrock.cyxbs.lib.utils.utils.VibratorUtil
//import com.mredrock.cyxbs.course2.page.course.widget.course.ItemType
//import com.mredrock.cyxbs.lib.course.course.AbstractCourseLayout
//import com.mredrock.cyxbs.lib.course.course.AbstractCourseLayout.Companion.DUSK_BOTTOM
//import com.mredrock.cyxbs.lib.course.course.AbstractCourseLayout.Companion.DUSK_TOP
//import com.mredrock.cyxbs.lib.course.course.AbstractCourseLayout.Companion.NOON_BOTTOM
//import com.mredrock.cyxbs.lib.course.course.AbstractCourseLayout.Companion.NOON_TOP
//import com.mredrock.cyxbs.lib.course.course.attrs.CourseLayoutParams
//import com.mredrock.cyxbs.lib.course.course.fold.RowState
//import com.mredrock.cyxbs.lib.course.net.attrs.NetLayoutParams
//import com.mredrock.cyxbs.lib.course.net.draw.ItemDecoration
//import com.mredrock.cyxbs.lib.course.net.touch.multiple.event.IPointerEvent
//import com.mredrock.cyxbs.lib.course.net.touch.multiple.event.IPointerEvent.Action.*
//import com.mredrock.cyxbs.lib.course.scroll.ICourseScrollView
//import kotlin.math.*
//
///**
// * 长按事务整体移动的事件处理者
// *
// * 该类作用：
// * 1、绑定一根手指的事件；
// * 2、封装长按整体移动功能；
// *
// * 注意事项：
// * 1、长按事务整体移动
// * 2、对于长按的那个 View 在激活长按时是会被移除父布局的，
// *    在移动动画结束后会重新添加（如果不移除父布局就会造成很多因重新布局大小改变的问题）
// * 3、采取的是 MOVE 中拦截，只要移动距离不大，不会拦截 View 的点击监听
// *
// *
// * 注：代码虽然有点小多，但其实主要多在了松手时位置的判断，那部分逻辑时很完善的，一般不会出 bug
// * @author 985892345 (Guo Xiangrui)
// * @email 2767465918@qq.com
// * @date 2022/2/18 14:40
// */
//internal open class AffairMoveTouchHandler(
//  val scroll: ICourseScrollView,
//  val course: AbstractCourseLayout,
//  val itemTypeProvider: IItemTypeProvider,
//  dispatcher: EntityMovePointerDispatcher
//) : EntityMovePointerDispatcher.AbstractEntityMoveTouchHandler(dispatcher),
//  ItemDecoration {
//
//  override fun start(event: IPointerEvent, child: View) {
//    flag = PointerFlag.START
//    mPointerId = event.pointerId
//    mIsInOverlay = false // 重置
//    mIsInLongPress = false // 重置
//    mDistanceDownToViewTop = event.y - child.top
//    mLongPressRunnable.start()
//    mLongPressView = child
//  }
//
//  override fun cancel() {
//    if (mLongPressView == null) return
//    moveTouchEnd(mPointerId, mLongPressView!!)
//    if (mIsInLongPress) {
//      restoreAffairViewToOldLocation(true)
//      mScrollRunnable.cancel()
//      mIsInLongPress = false // 重置
//      removeFromDispatcher(mPointerId)
//    } else {
//      mLongPressRunnable.cancel()
//      moveAnimEnd(mPointerId, mLongPressView!!) // 这个时候是没得动画的，但为了方便处理，需要调用该方法
//      flag = PointerFlag.OVER
//    }
//    mLongPressView = null // 重置
//  }
//
//  /**
//   * 是否开始拦截事件
//   *
//   * 在长按激活前，不会正式拦截事件，会处于一种准备状态，
//   * 但如果在长按激活前移动距离过大，会直接结束事件，此后都不再拦截
//   *
//   * 该函数(commit 22年)的写法是不会拦截子 View 的点击事件的
//   */
//  override fun isStartInterceptEvent(): Boolean {
//    if (flag == PointerFlag.OVER) {
//      mLongPressView = null // 重置
//      return false
//    }
//    if (mIsInLongPress) {
//      return true
//    } else {
//      val pointer = scroll.getPointer(mPointerId)
//      if (abs(pointer.diffMoveX) > mTouchSlop
//        || abs(pointer.diffMoveY) > mTouchSlop
//      ) {
//        // 如果在长按激活前移动距离大于 mTouchSlop 就结束事件，不再拦截
//        cancel()
//      }
//    }
//    return false
//  }
//
//  override fun isAlreadyHandle(child: View): Boolean {
//    return child === mLongPressView || child === mSubstituteView
//  }
//
//  override var flag: PointerFlag = PointerFlag.OVER
//
//  protected var mPointerId = 0
//    private set
//
//  // 认定是在滑动的最小移动值，其中 ScrollView 拦截事件就与该值有关，不建议修改该值
//  private var mTouchSlop = ViewConfiguration.get(course.context).scaledTouchSlop
//
//  protected var mIsInLongPress = false
//    private set
//  private val mLongPressRunnable = object : Runnable {
//    private val mLongPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
//    override fun run() {
//      mIsInLongPress = true
//      longPressStart()
//    }
//
//    fun start() = course.postDelayed(this, mLongPressTimeout)
//    fun cancel() = course.removeCallbacks(this)
//  }
//
//  protected var mIsInOverlay = false // mLongPressView 是否处于 overlay 中
//    private set
//  protected var mLongPressView: View? = null
//    private set
//
//  // LongPressView 的替身 View，用于提前占位，防止点击穿透。在长按激活时就会被添加到 course 中
//  private val mSubstituteView by lazyUnlock {
//    object : View(course.context) {
//
//      init {
//        // 不进行绘制操作时，可以调用该方法进行优化
//        setWillNotDraw(true)
//      }
//
//      private var mOnNextLayoutCallback: ((View) -> Unit)? = null
//
//      /**
//       * 设置紧接着下一次布局后的回调
//       *
//       * 因为动画的回调提前于布局的回调，所以在有些情况下需要得到重新布局后的高度值才能开启动画
//       */
//      /**
//       * 设置紧接着下一次布局后的回调
//       *
//       * 因为动画的回调提前于布局的回调，所以在有些情况下需要得到重新布局后的高度值才能开启动画
//       */
//      fun setOnNextLayoutCallback(call: (View) -> Unit) {
//        mOnNextLayoutCallback = call
//      }
//
//      override fun layout(l: Int, t: Int, r: Int, b: Int) {
//        super.layout(l, t, r, b)
//        mOnNextLayoutCallback?.invoke(this)
//        mOnNextLayoutCallback = null
//      }
//
//      @SuppressLint("ClickableViewAccessibility")
//      override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return false
//      }
//    }
//  }
//
//  // 替身 View 的 lp
//  private val mSubstituteLp = itemTypeProvider.newLayoutParams(
//    ItemType.SUBSTITUTE, -1, CourseLayoutParams(0, 0, 0))
//
//  // DOWN 时点击的位置到 LongPressView 顶部的距离
//  private var mDistanceDownToViewTop = 0F
//
//  override var isNoonFoldedLongPressStart = true // 长按开始时中午时间段是否处于折叠状态
//  override var isDuskFoldedLongPressStart = true // 长按开始时傍晚时间段是否处于折叠状态
//  override var isContainNoonLongPressStart = false // 长按开始时自身是否包含中午时间段
//  override var isContainDuskLongPressStart = false // 长按开始时自身是否包含傍晚时间段
//
//  protected var mMoveAnimation: MoveAnimation? = null // 抬手后回到原位置或者移动到新位置的动画
//    private set
//
//  override fun onPointerTouchEvent(event: IPointerEvent, view: View) {
//    when (event.action) {
//      DOWN -> { /* DOWN 事件不会被触发*/
//      }
//      MOVE -> {
//        putViewIntoOverlayIfCan()
//        scrollIsNecessary()
//        course.invalidate() // 直接重绘，交给 onDrawAbove() 处理移动
//      }
//      UP -> {
//        moveTouchEnd(mPointerId, mLongPressView!!)
//        changeLocationIfNecessary(event.event.action == MotionEvent.ACTION_UP)
//        mScrollRunnable.cancel()
//        removeFromDispatcher(mPointerId)
//        mLongPressView = null // 重置
//        mIsInLongPress = false // 重置
//      }
//      CANCEL -> {
//        cancel()
//      }
//    }
//  }
//
//  override fun onDrawBelow(canvas: Canvas, view: View) {
//    /*
//    * 为什么在这里平移呢？
//    * 1、一般情况下是直接在 onTouchEvent() 的 Move 中平移位置
//    * 2、但存在 onTouchEvent() 不回调的情况，而位置又因为 CourseScrollView 的滚动而改变
//    * 3、所以为了减少不必要的判断，直接放在绘图的回调里是最方便的
//    * */
//    mLongPressView?.let {
//      if (mIsInLongPress) {
//        translateView(it)
//      }
//    }
//  }
//
//  protected open fun longPressStart() {
//    // 禁止父布局拦截
//    course.parent.requestDisallowInterceptTouchEvent(true)
//    // 让 LongPressView 绘制在其他 View 之上，再增加一些阴影效果
//    mLongPressView!!.translationZ = 3F + mPointerId
//    VibratorUtil.start(course.context, 36) // 长按被触发来个震动提醒
//    val lp = mLongPressView!!.layoutParams as CourseLayoutParams
//    // 记录长按开始时的中午状态
//    isNoonFoldedLongPressStart = when (course.getNoonRowState()) {
//      RowState.FOLD, RowState.FOLD_ANIM -> true
//      RowState.UNFOLD, RowState.UNFOLD_ANIM -> false
//    }
//    // 记录长按开始时的傍晚状态
//    isDuskFoldedLongPressStart = when (course.getDuskRowState()) {
//      RowState.FOLD, RowState.FOLD_ANIM -> true
//      RowState.UNFOLD, RowState.UNFOLD_ANIM -> false
//    }
//    isContainNoonLongPressStart = course.isContainNoon(lp)
//    isContainDuskLongPressStart = course.isContainDusk(lp)
//
//    // 在添加进 Overlay 前调用
//    moveStart(mPointerId, mLongPressView!!)
//
//    // 如果需要就自动展开中午和傍晚时间段
//    unfoldNoonOrDuskIfNecessary()
//    putViewIntoOverlayIfCan() // 只能放在展开动画后
//
//    // 用一个透明的 View 去代替 LongPressView 的位置，因为使用 overlay 会使 View 被移除
//    // 这里还有一个原因，防止在回到正确位置的动画中点击导致穿透
//    val subLp = mSubstituteLp.changeLocation(lp)
//    course.addLesson(mSubstituteView, subLp)
//  }
//
//  /**
//   * 判断当前平移 LongPressView 中是否需要自动展开中午或者傍晚时间段
//   */
//  protected open fun unfoldNoonOrDuskIfNecessary() {
//    // 如果长按开始时或者当前与中午时间段有交集
//    val noonState = course.getNoonRowState()
//    if ((noonState == RowState.FOLD || noonState == RowState.FOLD_ANIM)
//      && (isContainNoonLongPressStart || isEntityInNoon())
//    ) {
//      course.unfoldNoonForce()
//    }
//    // 如果长按开始时或者当前与傍晚时间段有交集
//    val duskState = course.getDuskRowState()
//    if ((duskState == RowState.FOLD || duskState == RowState.FOLD_ANIM)
//      && (isContainDuskLongPressStart || isEntityInDusk())
//    ) {
//      course.unfoldDuskForce()
//    }
//  }
//
//  /**
//   * 作用：在没有进行动画的时候放进 overlay
//   *
//   * 原因：主要因为存在这种情况，当该 View 的中间部分包含了正处于展开或折叠时的中午(傍晚)时间段，
//   * 这个时候添加进 overlay 将会缺少中午(傍晚)时间段的高度
//   */
//  protected open fun putViewIntoOverlayIfCan(
//    noonState: RowState = course.getNoonRowState(),
//    duskState: RowState = course.getDuskRowState()
//  ) {
//    if (!mIsInOverlay) { // 用于判断只调用一次
//      if (isContainNoonLongPressStart
//        && (noonState == RowState.FOLD_ANIM || noonState == RowState.UNFOLD_ANIM)
//      ) {
//        return // 开始包含中午时间段，且此时中午时间段又处于动画中
//      }
//      if (isContainDuskLongPressStart
//        && (duskState == RowState.FOLD_ANIM || duskState == RowState.UNFOLD_ANIM)
//      ) {
//        return // 开始包含傍晚时间段，且此时傍晚时间段又处于动画中
//      }
//      /*
//      * overlay 是一个很神奇的东西，有了这个东西就可以防止布局对 View 的影响，
//      * 而且仍可以在父布局中显示
//      * 这个相当于是在父布局顶层专门绘制，View 的位置不会受到父布局重新布局的影响
//      * */
//      mLongPressView?.let {
//        mIsInOverlay = true
//        /*
//        * 这里使用两个 postOnAnimation 其实是有原因的，可能你之后也会遇到这样的问题
//        * 就是添加进 overlay 过后，会导致 View 不会再被绘制，
//        * 所以为了能绘制，只能在进入 overlay 之前设置一些东西
//        * 那就只能使用 postOnAnimation 延后进入 overlay
//        * */
//        course.postOnAnimation {
//          course.postOnAnimation {
//            course.overlay.add(it)
//          }
//        }
//      }
//    }
//  }
//
//  /**
//   * 如果你把 [mLongPressView] 滑到屏幕显示边缘区域时，则可能需要调用 [CourseScrollView] 滚动
//   * ```
//   * 该方法作用：
//   * 1、计算当前 LongPressView 的 top 和 bottom 值与 ScrollView 的距离来判断是否需要让 ScrollView 滚动
//   * 2、计算让 ScrollView 滚动的速度
//   * ```
//   */
//  protected open fun scrollIsNecessary() {
//    mLongPressView?.let { mScrollRunnable.startIfCan(it) }
//  }
//
//  // 滑到显示区域顶部或者底部时，使 mCourseScrollView 滚动的 Runnable
//  private val mScrollRunnable = object : Runnable {
//
//    var isInScrolling = false // 是否处于滚动状态
//      private set
//
//    private var view: View? = null
//
//    private var velocity = 0 // 滚动的速度
//
//    override fun run() {
//      view?.let {
//        if (isAllowScrollAndCalculateVelocity(it)) {
//          scroll.scrollBy(velocity)
//          course.invalidate() // 直接重绘，交给 onDrawAbove() 处理移动
//          course.postOnAnimation(this)
//        } else {
//          isInScrolling = false
//        }
//      }
//    }
//
//    /**
//     * 如果能开启滚动的话就开启滚动，注意：滚动是会自己取消的
//     */
//    fun startIfCan(view: View) {
//      this.view = view
//      if (!isInScrolling) { // 防止重复添加 Runnable
//        isInScrolling = true
//        run()
//      }
//    }
//
//    /**
//     * 取消滚动
//     */
//    fun cancel() {
//      if (isInScrolling) {
//        isInScrolling = false // 重置
//        course.removeCallbacks(this)
//        view = null
//      }
//    }
//
//    /**
//     * 是否允许滚动，如果允许，则计算滚动速度给 [velocity] 变量
//     */
//    private fun isAllowScrollAndCalculateVelocity(view: View): Boolean {
//      val diffHeight = scroll.getDistance(course)
//      val topHeight = (view.y + diffHeight).toInt()
//      val bottomHeight = topHeight + view.height
//      val moveBoundary = 50 // 移动的边界值
//      val pointer = scroll.getPointer(mPointerId)
//      // 向上滚动，即手指移到底部，需要显示下面的内容
//      val isNeedScrollUp =
//        bottomHeight > scroll.getHeight() - moveBoundary
//          && pointer.lastMoveY - pointer.initialY > 0
//          && scroll.getHeight() + scroll.getScrollY() < scroll.innerHeight // 是否滑到底
//
//      // 向下滚动，即手指移到顶部，需要显示上面的内容
//      val isNeedScrollDown =
//        topHeight < moveBoundary
//          && pointer.lastMoveY - pointer.initialY < 0
//          && scroll.getScrollY() > 0 // 是否滑到顶
//      val isAllowScroll = isNeedScrollUp || isNeedScrollDown
//      if (isAllowScroll) {
//        velocity = if (isNeedScrollUp) {
//          // 速度最小为 6，最大为 12，与边界的差值成线性关系
//          min((bottomHeight - (scroll.getHeight() - moveBoundary)) / 10 + 6, 12)
//        } else {
//          // 速度最小为 6，最大为 12，与边界的差值成线性关系
//          -min(((moveBoundary - topHeight) / 10 + 6), 12)
//        }
//      }
//      return isAllowScroll
//    }
//  }
//
//  /**
//   * 平移 LongPressView
//   */
//  protected open fun translateView(view: View) {
//    val pointer = scroll.getPointer(mPointerId)
//    // 使用 CourseScrollView 来计算绝对坐标系下的偏移量，而不是使用 course 自身的坐标系
//    val dx = pointer.lastMoveX - pointer.initialX
//    view.translationX = dx.toFloat()
//    view.y = pointer.lastMoveY - scroll.getDistance(course) - mDistanceDownToViewTop
//    // 判断是否展开中午或者傍晚时间段（在滑过中午或者傍晚时需要将他们自动展开）
//    // 这里应该拿替身 View 去算，因为 mLongPressView 可能进 overlay 了
//    unfoldNoonOrDuskIfNecessary()
//  }
//
//  /**
//   * 作用：如果可以改变位置，则带有动画的移动到新位置
//   *
//   * 如果你要测试的话，建议把 [CourseLayout.DEBUG] 属性给打开
//   * @param isFinalUpEvent 是否是最后一根手指抬起的 UP 事件
//   */
//  protected open fun changeLocationIfNecessary(isFinalUpEvent: Boolean) {
//    if (!getMovable()) {
//      restoreAffairViewToOldLocation(isFinalUpEvent)
//      return
//    }
//    val view = mLongPressView
//    if (view != null) {
//      val location = LocationUtil.getLocation(view, course, itemTypeProvider, this::isSkipForeachJudge)
//      if (location == null) {
//        // 返回 null 就回到原位置
//        restoreAffairViewToOldLocation(isFinalUpEvent)
//        return
//      }
//      mSubstituteLp.changeLocation(location)
//      // 让替身提前去占位，防止点击穿透，
//      // 但请注意：这个占位有些延迟，动画的回调会先于重新布局执行
//      mSubstituteView.layoutParams = mSubstituteLp
//
//      val isInNoonEnd = course.isContainNoon(mSubstituteLp)
//      val isInDuskEnd = course.isContainDusk(mSubstituteLp)
//      if (isInNoonEnd) course.unfoldNoonForce()
//      if (isInDuskEnd) course.unfoldDuskForce()
//      if (mIsInOverlay) {
//        /*
//        * 进入了 overlay，此时说明 view.height 的高度是不变且准确的
//        * 如果没有进入 overlay，说明是包含中午(傍晚)时间段且中午(傍晚)时间段又处于动画中
//        * 此时移动到新位置时因为 view.height 得到的高度不准确，所以不能直接恢复折叠状态，
//        * 需要在移动动画结束后恢复，不然在移动中高度会减少再突然增大
//        * */
//        recoverFoldState(isFinalUpEvent, isInNoonEnd, isInDuskEnd)
//      }
//
//      /*
//      * 这里为什么要设置回调才开启动画?
//      * 原因如下：
//      * 1、前面让 mSubstituteView 提前去占位，但下一次布局回调是在下面这个移动动画回调之后完成的，
//      *    所以动画快于 onLayout() 方法执行，就会导致在前两帧得不到正确的 left、top 值
//      * */
//      mSubstituteView.setOnNextLayoutCallback {
//        val dx = view.x - it.left
//        val dy = view.y - it.top
//
//        // 记录动画开始前的 translationZ
//        val translationZ = view.translationZ
//
//        /*
//        * 开启动画移动到最终位置
//        * 为什么还要改变 view.left 和 view.top ?
//        * 原因如下：
//        * 1、存在此时正处于展开或折叠动画中，最终的位置是无法计算的，但可以使用 view.translationY + view.top
//        *    来计算展开动画每一帧此时对应的位置
//        *
//        * 为什么不直接用 mSubstituteView 的 right、bottom，而只用 left、top ?
//        * 原因如下：
//        * 1、mSubstituteView 即使已经占好了位，也会出现 mSubstituteView 刚好处于正在展开的中午(傍晚)时间段上，
//        *    导致得到的值 right、bottom 小于正确的值
//        * */
//        mMoveAnimation = MoveAnimation(
//          dx, dy, 0F, 0F,
//          getMoveAnimatorDuration(dx, dy)
//        ) { x, y, fraction ->
//          view.translationZ = translationZ * (1 - fraction)
//          if (mIsInOverlay) {
//            view.translationX = x
//            view.translationY = y
//            val ll = mSubstituteView.left
//            val rr = ll + view.width
//            val tt = mSubstituteView.top
//            val bb = tt + view.height
//            view.left = ll
//            view.right = rr
//            view.top = tt
//            view.bottom = bb
//          } else {
//            // 没有添加进 overlay，说明 view 还在 course 中，而且是原来的位置
//            view.translationX = -(view.left - it.left - x)
//            view.translationY = -(view.top - it.top - y)
//          }
//        }.addEndListener {
//          view.translationX = 0F // 还原
//          view.translationY = 0F // 还原
//          view.translationZ = 0F // 重置
//          val lp = view.layoutParams as CourseLayoutParams
//          lp.changeLocation(mSubstituteLp)
//          course.removeView(mSubstituteView)
//          if (mIsInOverlay) {
//            course.overlay.remove(view)
//            course.addLesson(view, lp)
//            mIsInOverlay = false // 重置
//          } else {
//            view.layoutParams = lp
//            // 与前面互相对应，如果 view 没有添加进 overlay，则在移动动画结束后恢复
//            recoverFoldState(isFinalUpEvent, isInNoonEnd, isInDuskEnd)
//          }
//          moveAnimEnd(mPointerId, view)
//          flag = PointerFlag.OVER
//        }.start()
//      }
//    }
//  }
//
//  /**
//   * 是否跳过遍历判断
//   *
//   * 我把抬手时对 LongPressView 与其他 View 能够相交的判断移动了出来，如果以后需要添加的可以直接写在这里，
//   * 比如以后添加一个只有装饰作用的 View 在所有 View 的底部，就得写在这个地方
//   */
//  protected open fun isSkipForeachJudge(child: View): Boolean {
//    return child === mSubstituteView || child === mLongPressView
//  }
//
//  protected open fun getMoveAnimatorDuration(dx: Float, dy: Float): Long {
//    // 自己拟合的一条由距离求出时间的函数，感觉比较适合动画效果 :)
//    // y = 50 * x^0.25 + 90
//    return (hypot(dx, dy).pow(0.25F) * 50 + 90).toLong()
//  }
//
//  /**
//   * 带有动画的恢复 LongPressView 到原位置
//   * @param isFinalUpEvent 是否是最后一根手指抬起的 UP 事件
//   */
//  protected fun restoreAffairViewToOldLocation(isFinalUpEvent: Boolean) {
//    val view = mLongPressView
//    if (view != null) {
//      recoverFoldState(isFinalUpEvent, isContainNoonLongPressStart, isContainDuskLongPressStart)
//
//      val dx = view.x - mSubstituteView.left
//      val dy = view.y - mSubstituteView.top
//
//      val translationZ = view.translationZ
//
//      /*
//      * 这里就可以直接使用 mSubstituteView 的 right、bottom，
//      * 因为是回到原位置，mSubstituteView.height 即使要变化，这个变化也不会造成视觉上的影响
//      * */
//      mMoveAnimation = MoveAnimation(
//        dx, dy, 0F, 0F,
//        getMoveAnimatorDuration(dx, dy)
//      ) { x, y, fraction ->
//        view.translationX = x
//        view.translationY = y
//        view.translationZ = translationZ * (1 - fraction)
//        if (mIsInOverlay) {
//          /*
//          * 1、如果没有进入 overlay，那么 view 就没有被添加进 course，设置它的属性是失效的，
//          *    并且也不需要设置，因为 view 与 mSubstituteView 处于相同的位置
//          * 2、如果进入了 overlay，view 与 mSubstituteView 就不一定处于相同的位置了，
//          *    并且 view 也脱离了 course，此时就得在移动的动画中实时获取
//          *    mSubstituteView 的 left、top、right、bottom，防止 mSubstituteView 正处于宽高改变的动画中
//          * */
//          view.left = mSubstituteView.left
//          view.top = mSubstituteView.top
//          view.right = mSubstituteView.right
//          view.bottom = mSubstituteView.bottom
//        }
//      }.addEndListener {
//        view.translationX = 0F // 还原
//        view.translationY = 0F // 还原
//        view.translationZ = 0F // 重置
//        course.removeView(mSubstituteView)
//        if (mIsInOverlay) {
//          val lp = view.layoutParams as CourseLayoutParams
//          course.overlay.remove(view)
//          course.addLesson(view, lp)
//          mIsInOverlay = false // 重置
//        }
//        moveAnimEnd(mPointerId, view)
//        flag = PointerFlag.OVER
//      }.start()
//    }
//  }
//
//  override fun isEntityInNoon(): Boolean {
//    if (!mIsInLongPress) return false
//    mLongPressView?.let {
//      val topNoon = course.getRowsHeight(0, NOON_TOP - 1)
//      val bottomNoon =
//        topNoon + course.getRowsHeight(NOON_TOP, NOON_BOTTOM)
//      val lp = it.layoutParams as NetLayoutParams
//      val top = lp.constraintTop + it.translationY.roundToInt()
//      val bottom = lp.constraintBottom + it.translationY.roundToInt()
//      return topNoon in top..bottom || bottomNoon in top..bottom
//    }
//    return false
//  }
//
//  override fun isEntityInDusk(): Boolean {
//    if (!mIsInLongPress) return false
//    mLongPressView?.let {
//      val topDusk = course.getRowsHeight(0, DUSK_TOP - 1)
//      val bottomDusk =
//        topDusk + course.getRowsHeight(DUSK_TOP, DUSK_BOTTOM)
//      val lp = it.layoutParams as NetLayoutParams
//      val top = lp.constraintTop + it.translationY.roundToInt()
//      val bottom = lp.constraintBottom + it.translationY.roundToInt()
//      return topDusk in top..bottom || bottomDusk in top..bottom
//    }
//    return false
//  }
//
//  init {
//    course.addItemDecoration(this) // 监听 onDraw() 用于刷新位置
//  }
//
//  // 移动动画的封装
//  protected class MoveAnimation(
//    private val startX: Float,
//    private val startY: Float,
//    private val endX: Float,
//    private val endY: Float,
//    private val time: Long,
//    private val onChange: (x: Float, y: Float, fraction: Float) -> Unit
//  ) {
//    private val animator = ValueAnimator.ofFloat(0F, 1F)
//    fun start(): MoveAnimation {
//      animator.run {
//        addUpdateListener {
//          val now = animatedValue as Float
//          val x = startX - (startX - endX) * now
//          val y = startY - (startY - endY) * now
//          onChange.invoke(x, y, it.animatedFraction)
//        }
//        duration = time
//        interpolator = OvershootInterpolator(0.6F) // 个人认为 0.6F 的回弹比较合适
//        start()
//      }
//      return this
//    }
//
//    fun addEndListener(onEnd: () -> Unit): MoveAnimation {
//      animator.addListener(onEnd = { onEnd.invoke() })
//      return this
//    }
//  }
//}