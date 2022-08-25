package com.mredrock.cyxbs.noclass.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoclassGroup

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.widget
 * @ClassName:      FlexHorizontalScrollView
 * @Author:         Yan
 * @CreateDate:     2022年08月14日 22:03:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    流式布局的翻页效果
 */
class FlexHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * 页面基线
     * 相当于每一页的最左侧
     */
    private var mBaseScrollX = 0

    /**
     * 屏幕宽度
     */
    private var mScreenWidth = 0

    /**
     * 整个view的真实宽度
     */
    private var mRealWidth = 0

    /**
     * 当前页面总数
     */
    private var mPageCount = 0

    /**
     * 屏幕margin
     */
    private var mMargin = 0

    /**
     * 滑动阈值
     */
    private val mScrollX = 225

    /**
     * 当前位置所在页数
     */
    private var mPosition = 0

    /**
     * 选中数据
     */
//    private val mData: MutableList<String> = ArrayList()

    /**
     * 流式布局项数
     */
    private val layoutList: MutableList<MyFlexLayout> = ArrayList()

    /**
     * 数据项
     */
    private lateinit var mList: List<NoclassGroup>

    private val mContainer: LinearLayout by lazy {
        getChildAt(0) as LinearLayout
    }

    /**
     * 绘制完成的回调
     * 通知外布局已完成绘制
     */
    private var mOnCompleteCallback : OnCompleteCallback? = null

    init{
        //去除滚动条
        isHorizontalScrollBarEnabled = false
        //去除阴影效果
        overScrollMode = OVER_SCROLL_NEVER
        isFillViewport = true
        mScreenWidth = resources.displayMetrics.widthPixels
        addView(LinearLayout(context))
    }

    /**
     * 设置数据进行加载View
     */
    fun setData(list: List<NoclassGroup>, onCompleteCallback: OnCompleteCallback? = null) {
        //延迟以获取距离屏幕左侧的距离用来支持 margin 和 padding
        post {
//            mData.clear()
            mContainer.removeAllViews()
            mPageCount = 0
            if(onCompleteCallback!=null){
                mOnCompleteCallback = onCompleteCallback
            }
            val xyLocation = IntArray(2)
            getLocationOnScreen(xyLocation)
            mMargin = xyLocation[0]
            mRealWidth = mScreenWidth - mMargin * 2
            mList = list
            val flexLayout = MyFlexLayout(context)
            flexLayout.setOnFillCallback(onFillCallback)
            layoutList.add(flexLayout)
            addPage(flexLayout)
            fillDataInTextView(mList, flexLayout)
        }
    }

    @SuppressLint("InflateParams")
    /**
     * 默认填充TextView
     */
    private fun fillDataInTextView(list: List<NoclassGroup>, flexLayout: MyFlexLayout) {
        if(list.isEmpty()){
            //这个view是当元素为空的时候显示
            val lp = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            lp.setMargins(5f.dp2px, 5f.dp2px, 5f.dp2px, 5f.dp2px)
            //这个TextView就是每一个显示的Item
            val textView = LayoutInflater.from(context).inflate(R.layout.noclass_item_flex_textview, null) as TextView
            textView.text = "进入分组管理中心，来新建你的第一个分组吧"
            textView.setTextColor(Color.parseColor("#556C8B"))
            textView.layoutParams = lp
            flexLayout.addView(textView)
            return
        }
        for (i in mList.indices) {
            //这个TextView就是每一个显示的Item
            if (list[i].isTop){
                val rootView = LayoutInflater.from(context).inflate(R.layout.noclass_item_flex_group_top, null) as ViewGroup
                rootView.layoutParams = MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(16,10,16,10)
                }
                val textView = rootView.findViewById<TextView>(R.id.tv_noclass_group_top)
                textView.text = list[i].name
                textView.setOnClickListener {
                    //每个item的点击效果

                }
                flexLayout.addView(rootView)
            }else{
                val rootView = LayoutInflater.from(context).inflate(R.layout.noclass_item_flex_group_normal,null) as ViewGroup
                rootView.layoutParams = MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(16,10,16,10)
                }
                rootView.findViewById<TextView>(R.id.tv_noclass_group_normal).apply {
                    text = list[i].name
                }
                flexLayout.addView(rootView)
            }

        }
    }

    /**
     * 填满一页返回再填下一页
     */
    private var onFillCallback : MyFlexLayout.OnFillCallback = object : MyFlexLayout.OnFillCallback {
        override fun onFill(index: Int) {
            //一个一个加载,否则回调顺序就乱掉了
            post Runnable@{
                val count = mList.size
                if (index > count) {
                    return@Runnable
                }
                //subList生成子列表后，不要试图去操作原列表 解决ConcurrentModificationException
                val cache: MutableList<NoclassGroup> = ArrayList()
                cache.addAll(mList.subList(index, count))
                if (cache.isEmpty()) {
                    mOnCompleteCallback?.onComplete(mPageCount)
                    return@Runnable
                }
                mList = cache
                val flexLayout = MyFlexLayout(context)
                flexLayout.setOnFillCallback(this)
                layoutList.add(flexLayout)
                addPage(flexLayout)
                fillDataInTextView(cache, flexLayout)
            }
        }
    }

    /**
     * 当前页数流式布局无法加载完全
     * 需要新添一页进行加载
     */
    fun addPage(page: View) {
        // 为了支持 margin 再套一层
        // 还有个解决办法是 Layout 加上对 padding 的支持
        val child = FrameLayout(context)
        val params = LinearLayout.LayoutParams(mRealWidth, LayoutParams.WRAP_CONTENT)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.leftMargin = mMargin
        layoutParams.rightMargin = mMargin
        page.layoutParams = layoutParams
        child.addView(page)
        mContainer.addView(child, params)
        mPageCount++
    }

    /**
     * 获取相对滑动位置。由右向左滑动，返回正值；由左向右滑动，返回负值。
     *
     * @return
     */
    private fun getBaseScrollX(): Int {
        return scrollX - mBaseScrollX
    }

    /**
     * 使相对于基线移动x距离。
     *
     * @param x x为正值时右移；为负值时左移。
     */
    private fun baseSmoothScrollTo(x: Int) {
        smoothScrollTo(x + mBaseScrollX, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_UP -> {
                val scrollX = getBaseScrollX()
                //左滑
                if (scrollX > mScrollX) {
                    mPosition++
                    mOnCompleteCallback?.onScroll(mPosition)
                    baseSmoothScrollTo(mRealWidth)
                    mBaseScrollX += mRealWidth
                } else if (scrollX > 0 || scrollX > -mScrollX) {
                    baseSmoothScrollTo(0)
                } else {
                    mPosition--
                    mOnCompleteCallback?.onScroll(mPosition)
                    baseSmoothScrollTo(-mRealWidth)
                    mBaseScrollX -= mRealWidth
                }
                return true
            }
        }
        return super.onTouchEvent(ev)
    }

    interface OnCompleteCallback {
        fun onComplete(count: Int)
        fun onScroll(index: Int)
    }

}